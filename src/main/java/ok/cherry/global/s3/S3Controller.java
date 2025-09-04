package ok.cherry.global.s3;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import ok.cherry.global.s3.dto.request.FilesDeleteRequest;
import ok.cherry.global.s3.dto.response.FilesDeleteResponse;
import ok.cherry.global.s3.dto.response.ImageUploadResponse;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class S3Controller {

	private final S3Service s3Service;

	/**
	 * 단일 파일 업로드
	 * */
	@PostMapping
	public ResponseEntity<ImageUploadResponse> uploadImage(@RequestParam("image") MultipartFile image) {
		List<String> fileNames = new ArrayList<>();
		fileNames.add(s3Service.uploadFile(image));
		ImageUploadResponse response = ImageUploadResponse.of(fileNames);
		return ResponseEntity.ok(response);
	}

	/**
	 * 단일 파일 삭제
	 * */
	@DeleteMapping("/image")
	public ResponseEntity<FilesDeleteResponse> deleteImage(@RequestParam("image") String imagePrefix) {
		s3Service.deleteFile(imagePrefix);

		List<String> fileNames = new ArrayList<>();
		fileNames.add(imagePrefix);

		FilesDeleteResponse response = FilesDeleteResponse.of(fileNames);
		return ResponseEntity.ok(response);
	}

	/**
	 * 다중 파일 삭제
	 * */
	@DeleteMapping("/images")
	public ResponseEntity<FilesDeleteResponse> deleteImages(@RequestBody FilesDeleteRequest request) {
		s3Service.deleteFiles(request.fileNames());
		FilesDeleteResponse response = FilesDeleteResponse.of(request.fileNames());
		return ResponseEntity.ok(response);
	}

}
