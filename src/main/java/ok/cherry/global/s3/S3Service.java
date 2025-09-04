package ok.cherry.global.s3;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ok.cherry.global.exception.error.BusinessException;
import ok.cherry.global.s3.exception.S3Error;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

	private final S3Client s3Client;

	@Value("${spring.cloud.aws.s3.bucket}")
	private String bucket;

	@Value("${spring.cloud.aws.s3.allowed-mime-types}")
	private List<String> allowedMimeTypes;

	/**
	 * 단일 파일 업로드
	 * */
	public String uploadFile(MultipartFile file) { // todo: 경로 지정 할 필요가 없어서 관련 로직 모두 제거
		// S3에 저장될 파일명 생성
		String fileName = createFileName(file.getOriginalFilename());

		// 파일 확장자를 기반으로 MIME 타입 유추
		String contentType = MediaTypeFactory.getMediaType(file.getOriginalFilename())
			.orElse(MediaType.APPLICATION_OCTET_STREAM)
			.toString();

		try {
			// 파일 존재 여부 확인
			HeadObjectRequest headRequest = HeadObjectRequest.builder()
				.bucket(bucket)
				.key(fileName)
				.build();

			s3Client.headObject(headRequest);
			throw new BusinessException(S3Error.DUPLICATE_FILE);
		} catch (NoSuchKeyException e) {
			try {
				// S3에 업로드
				PutObjectRequest putObjectRequest = PutObjectRequest.builder()
					.bucket(bucket)
					.key(fileName)
					.contentType(contentType)
					.contentLength(file.getSize())
					.build();

				s3Client.putObject(putObjectRequest,
					RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

				// 버킷 주소 이후의 prefix 반환
				return fileName;
			} catch (Exception ex) {
				log.error("S3 파일 업로드 실패 - 파일명: {}, 버킷: {}, 오류: {}", fileName, bucket, ex.getMessage(), ex);
				throw new BusinessException(S3Error.UPLOAD_FAIL);
			}
		}
	}

	/**
	 * 다중 파일 업로드
	 * */
	public List<String> uploadFiles(List<MultipartFile> files) {
		validateFiles(files);

		List<CompletableFuture<String>> uploadFutures = files.stream()
			.map(file -> CompletableFuture.supplyAsync(() -> uploadFile(file)))
			.toList();

		// 비동기 작업 완료 후 결과 취합
		CompletableFuture<Void> allOf = CompletableFuture.allOf(uploadFutures.toArray(new CompletableFuture[0]));

		try {
			allOf.join(); // 모든 작업 완료 대기
			return uploadFutures.stream()
				.map(CompletableFuture::join)
				.toList();
		} catch (CompletionException e) {
			log.error("S3 병렬 파일 업로드 실패: {}", e.getMessage(), e);
			throw new BusinessException(S3Error.UPLOAD_FAIL);
		}
	}

	/**
	 * 단일 파일 삭제
	 * */
	public void deleteFile(String fileName) {
		try {
			// 파일이 존재 여부 확인
			HeadObjectRequest headRequest = HeadObjectRequest.builder()
				.bucket(bucket)
				.key(fileName)
				.build();

			s3Client.headObject(headRequest);
			log.info("삭제하려는 파일: {}", fileName);

			// S3에서 파일 삭제
			DeleteObjectRequest deletedObjectRequest = DeleteObjectRequest.builder()
				.bucket(bucket)
				.key(fileName)
				.build();

			s3Client.deleteObject(deletedObjectRequest);
		} catch (NoSuchKeyException e) {
			log.warn("삭제하려는 파일이 존재하지 않음: {}", fileName);
			throw new BusinessException(S3Error.FILE_NOT_FOUND);
		} catch (Exception e) {
			log.error("S3 파일 삭제 실패: - 파일명: {}, 버킷: {}, 오류: {}", fileName, bucket, e.getMessage(), e);
			throw new BusinessException(S3Error.DELETE_FAIL);
		}
	}

	/**
	 * 다중 파일 삭제
	 * */
	public void deleteFiles(List<String> fileNames) {
		validateFileUrls(fileNames);

		List<CompletableFuture<Void>> deleteFutures = fileNames.stream()
			.map(fileUrl -> CompletableFuture.runAsync(() -> deleteFile(fileUrl)))
			.toList();

		CompletableFuture<Void> allOf = CompletableFuture.allOf(deleteFutures.toArray(new CompletableFuture[0]));

		try {
			allOf.join();
		} catch (CompletionException e) {
			log.error("S3 병렬 파일 삭제 실패: {}", e.getMessage(), e);
			throw new BusinessException(S3Error.DELETE_FAIL);
		}
	}

	private Set<String> getAllowedMimeTypes() {
		return new HashSet<>(allowedMimeTypes);
	}

	private String createFileName(String originalFileName) {
		return UUID.randomUUID() + "_" + originalFileName;
	}

	private void validateFiles(List<MultipartFile> files) {
		if (files == null || files.isEmpty()) {
			throw new BusinessException(S3Error.EMPTY_FILE_LIST);
		}

		if (files.stream().anyMatch(file -> file == null || file.isEmpty())) {
			throw new BusinessException(S3Error.INVALID_FILE);
		}

		// 파일 확장자 기반으로 Mime 타입 검증
		Set<String> allowedMimeTypes = getAllowedMimeTypes();
		boolean hasInvalidType = files.stream()
			.anyMatch(file -> {
				Optional<MediaType> mediaType = MediaTypeFactory.getMediaType(file.getOriginalFilename());
				return mediaType.isEmpty() || !allowedMimeTypes.contains(mediaType.get().toString());
			});

		if (hasInvalidType) {
			throw new BusinessException(S3Error.INVALID_FILE_TYPE);
		}
	}

	private void validateFileUrls(List<String> fileUrls) {
		if (fileUrls == null || fileUrls.isEmpty()) {
			throw new BusinessException(S3Error.EMPTY_FILE_LIST);
		}

		if (fileUrls.stream().anyMatch(fileUrl -> fileUrl == null || fileUrl.isEmpty())) {
			throw new BusinessException(S3Error.INVALID_FILE_URL);
		}
	}
}
