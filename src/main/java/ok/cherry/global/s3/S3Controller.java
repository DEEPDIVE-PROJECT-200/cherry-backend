package ok.cherry.global.s3;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import ok.cherry.global.s3.dto.request.FilesDeleteRequest;
import ok.cherry.global.s3.dto.response.FilesDeleteResponse;
import ok.cherry.global.s3.dto.response.ProductImagesResponse;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class S3Controller {

	private static final String THUMBNAIL_IMAGE_PATH = "product/thumbnails";
	private static final String DETAIL_IMAGE_PATH = "product/details";

	private final S3Service s3Service;

	/**
	 * 썸네일 이미지, 상세 이미지 업로드
	 * */
	@PostMapping("/product-image")
	public ResponseEntity<ProductImagesResponse> uploadProductImages(
		@RequestPart("thumbnails") List<MultipartFile> thumbnailImages,
		@RequestPart("details") List<MultipartFile> detailImages) {

		List<String> thumbnailUrls = s3Service.uploadFiles(thumbnailImages, THUMBNAIL_IMAGE_PATH);
		List<String> detailUrls = s3Service.uploadFiles(detailImages, DETAIL_IMAGE_PATH);

		return ResponseEntity.ok(new ProductImagesResponse(thumbnailUrls, detailUrls));
	}

	/**
	 * 단일 파일 삭제
	 * */
	@DeleteMapping("image")
	public ResponseEntity<FilesDeleteResponse> deleteProductImage(@Param("image") String imagePrefix) {
		s3Service.deleteFile(imagePrefix);

		List<String> fileUrls = new ArrayList<>();
		fileUrls.add(imagePrefix);

		FilesDeleteResponse response = FilesDeleteResponse.of(fileUrls);
		return ResponseEntity.ok(response);
	}

	/**
	 * 다중 파일 삭제
	 * */
	@DeleteMapping("/images")
	public ResponseEntity<FilesDeleteResponse> deleteProductImages(@RequestBody FilesDeleteRequest request) {
		s3Service.deleteFiles(request.fileUrls());
		FilesDeleteResponse response = FilesDeleteResponse.of(request.fileUrls());
		return ResponseEntity.ok(response);
	}

}
