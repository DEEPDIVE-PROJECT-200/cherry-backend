package ok.cherry.global.s3.dto.response;

import java.util.List;

public record ProductImagesResponse(
	List<String> thumbnailUrls,
	List<String> detailUrls
) {
}
