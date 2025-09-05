package ok.cherry.global.s3.dto.response;

public record ImageUploadResponse(
	String fileName,
	String message
) {

	public static ImageUploadResponse of(String fileName) {
		return new ImageUploadResponse(fileName, "파일이 성공적으로 업로드되었습니다");
	}
}
