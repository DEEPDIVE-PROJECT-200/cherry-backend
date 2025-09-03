package ok.cherry.global.s3.dto.response;

import java.util.List;

public record FilesDeleteResponse(
	List<String> fileUrls,
	String message
) {
	public static FilesDeleteResponse of(List<String> fileUrls) {
		return new FilesDeleteResponse(fileUrls, "파일이 성공적으로 삭제되었습니다");
	}
}
