package ok.cherry.global.swagger.member;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import ok.cherry.member.application.dto.request.EmailDuplicationRequest;
import ok.cherry.member.application.dto.request.NicknameDuplicationRequest;

@Tag(name = "Members", description = "👤 회원 API - 회원 정보 조회, 수정, 탈퇴, 관리 API")
public interface MemberControllerDoc {

	@Operation(method = "POST", summary = "회원 이메일 중복 검증", description = "회원가입 시 입력한 이메일의 중복을 검증합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200",
			description = "가입 가능한 이메일"),
		@ApiResponse(responseCode = "409",
			description = "이미 존재하는 이메일")
	})
	ResponseEntity<Void> verifyEmailDuplication(EmailDuplicationRequest request);

	@Operation(method = "POST", summary = "회원 닉네임 중복 검증", description = "회원가입 시 입력한 닉네임의 중복을 검증합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200",
			description = "가입 가능한 닉네임"),
		@ApiResponse(responseCode = "409",
			description = "이미 존재하는 닉네임")
	})
	ResponseEntity<Void> verifyNicknameDuplication(NicknameDuplicationRequest request);

	@Operation(method = "DELETE", summary = "회원 탈퇴", description = "회원 탈퇴를 진행합니다.",
		security = {@SecurityRequirement(name = "JWT")}
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204"),
		@ApiResponse(responseCode = "404", description = "회원 탈퇴 실패 - 사용자를 찾을 수 없음",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
		@ApiResponse(responseCode = "409", description = "회원 탈퇴 실패 - 이미 탈퇴된 회원",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
	})
	ResponseEntity<Void> deactivateMember(HttpServletRequest request, String providerId);
}

