package ok.cherry.global.s3.dto.response;

import java.util.List;

public record FilesDeleteResponse(
	List<String> fileNames,
	String message
) {
	public static FilesDeleteResponse of(List<String> fileNames) {
		return new FilesDeleteResponse(fileNames, "파일이 성공적으로 삭제되었습니다");
	}
}
