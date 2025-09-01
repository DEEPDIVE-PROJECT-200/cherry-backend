package ok.cherry.global.s3;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ok.cherry.global.exception.error.BusinessException;
import ok.cherry.global.s3.exception.S3Error;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	/**
	 * 단일 파일 업로드
	 * */
	public String uploadFile(MultipartFile file, String dirName) {
		// S3에 저장될 파일명 생성
		String fileName = createFileName(file.getOriginalFilename(), dirName);

		// 메타 데이터 설정
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(file.getSize());
		objectMetadata.setContentType(file.getContentType());

		try{
			// S3에 업로드
			amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), objectMetadata));

			// 업로드된 파일의 URL 반환
			return amazonS3.getUrl(bucket, fileName).toString();
		} catch (Exception e) {
			log.error("S3 파일 업로드 중 오류 발생: {}", e.getMessage(), e);
			throw new BusinessException(S3Error.UPLOAD_FAIL);
		}
	}

	/**
	 * 다중 파일 업로드
	 * */
	public List<String> uploadFiles(List<MultipartFile> files, String dirName) {
		validateFiles(files);
		return files.stream()
			.map(file -> uploadFile(file, dirName))
			.toList();
	}

	/**
	 * 단일 파일 삭제
	 * */
	public void deleteFile(String fileUrl) {
		// URL에서 파일 이름(객체 키) 추출
		String fileName = extractFileName(fileUrl);

		try {
			// S3에서 파일 삭제
			amazonS3.deleteObject(bucket, fileName);
		} catch (Exception e) {
			log.error("S3 파일 삭제 중 오류 발생: {}", e.getMessage(), e);
			throw new BusinessException(S3Error.DELETE_FAIL);
		}

	}

	/**
	 * 다중 파일 삭제
	 * */
	public void deleteFiles(List<String> fileUrls) {
		validateFileUrls(fileUrls);
		fileUrls.forEach(this::deleteFile);
	}

	private String createFileName(String originalFileName, String dirName) {
		return dirName + "/" + UUID.randomUUID() + "_" + originalFileName;
	}

	private String extractFileName(String fileUrl) {
		String bucketUrl = amazonS3.getUrl(bucket, "").toString();
		return fileUrl.substring(bucketUrl.length());
	}

	private void validateFiles(List<MultipartFile> files) {
		if(files == null || files.isEmpty()) {
			throw new BusinessException(S3Error.EMPTY_FILE_LIST);
		}

		if(files.stream().anyMatch(file -> file == null || file.isEmpty())) {
			throw new BusinessException(S3Error.INVALID_FILE);
		}
	}

	private void validateFileUrls(List<String> fileUrls) {
		if (fileUrls == null || fileUrls.isEmpty()) {
			throw new BusinessException(S3Error.EMPTY_FILE_LIST);
		}

		if(fileUrls.stream().anyMatch(fileUrl -> fileUrl == null || fileUrl.isEmpty())) {
			throw new BusinessException(S3Error.INVALID_FILE_URL);
		}
	}


}
