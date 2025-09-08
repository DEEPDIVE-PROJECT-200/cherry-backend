package ok.cherry.global.swagger.Member;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
}

