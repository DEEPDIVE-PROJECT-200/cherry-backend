package ok.cherry.global.s3.dto.request;

import java.util.List;

public record FilesDeleteRequest(
	List<String> fileUrls
) {
}
