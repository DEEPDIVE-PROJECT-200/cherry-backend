package ok.cherry.global.s3;

import java.net.URL;
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
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
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
	public String uploadFile(MultipartFile file, String dirName) {
		// S3에 저장될 파일명 생성
		String fileName = createFileName(file.getOriginalFilename(), dirName);

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
	public List<String> uploadFiles(List<MultipartFile> files, String dirName) {
		validateFiles(files);

		List<CompletableFuture<String>> uploadFutures = files.stream()
			.map(file -> CompletableFuture.supplyAsync(() -> uploadFile(file, dirName)))
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
	public void deleteFile(String filePrefix) {
		try {
			// 파일이 존재 여부 확인
			HeadObjectRequest headRequest = HeadObjectRequest.builder()
				.bucket(bucket)
				.key(filePrefix)
				.build();

			s3Client.headObject(headRequest);
			log.info("삭제하려는 파일: {}", filePrefix);

			// S3에서 파일 삭제
			DeleteObjectRequest deletedObjectRequest = DeleteObjectRequest.builder()
				.bucket(bucket)
				.key(filePrefix)
				.build();

			s3Client.deleteObject(deletedObjectRequest);
		} catch (NoSuchKeyException e) {
			log.warn("삭제하려는 파일이 존재하지 않음: {}", filePrefix);
			throw new BusinessException(S3Error.FILE_NOT_FOUND);
		} catch (Exception e) {
			log.error("S3 파일 삭제 실패: - 파일명: {}, 버킷: {}, 오류: {}", filePrefix, bucket, e.getMessage(), e);
			throw new BusinessException(S3Error.DELETE_FAIL);
		}
	}

	/**
	 * 다중 파일 삭제
	 * */
	public void deleteFiles(List<String> fileUrls) {
		validateFileUrls(fileUrls);

		List<CompletableFuture<Void>> deleteFutures = fileUrls.stream()
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

	private String getFileUrl(String fileName) {
		GetUrlRequest getUrlRequest = GetUrlRequest.builder()
			.bucket(bucket)
			.key(fileName)
			.build();

		return s3Client.utilities().getUrl(getUrlRequest).toString();
	}

	private String createFileName(String originalFileName, String dirName) {
		return dirName + "/" + UUID.randomUUID() + "_" + originalFileName;
	}

	private String extractFileName(String fileUrl) {
		try {
			URL url = new URL(fileUrl);
			String path = url.getPath();

			if (path.startsWith("/")) {
				path = path.substring(1);
			}

			log.info("파일 URL: {}, 추출된 경로: {}", fileUrl, path);
			return path;
		} catch (Exception e) {
			log.error("URL 파싱 중 오류 발생: - 파일 URL: {}, 오류: {}", fileUrl, e.getMessage(), e);
			throw new BusinessException(S3Error.INVALID_FILE_URL);
		}
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
