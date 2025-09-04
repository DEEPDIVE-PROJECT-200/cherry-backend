package ok.cherry.global.s3.dto.response;

import java.util.List;

public record ImageUploadResponse(
	List<String> fileName,
	String message
) {
	public static ImageUploadResponse of(List<String> fileNames) {
		return new ImageUploadResponse(fileNames, "파일이 성공적으로 업로드되었습니다");
	}
}
