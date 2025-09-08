package ok.cherry.member.application.dto.request;

import org.hibernate.validator.constraints.Length;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "닉네임 중복 검증 요청 DTO")
public record NicknameDuplicationRequest(

    @Schema(description = "닉네임", example = "nickname")
    @NotBlank(message = "닉네임은 필수입니다.")
    @Length(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
    String nickname
) {
}
