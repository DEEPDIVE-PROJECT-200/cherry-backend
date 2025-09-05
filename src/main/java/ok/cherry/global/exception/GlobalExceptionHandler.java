package ok.cherry.global.exception;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import io.jsonwebtoken.JwtException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import ok.cherry.global.exception.error.BusinessException;
import ok.cherry.global.exception.error.DomainException;
import ok.cherry.global.exception.error.ErrorCode;
import ok.cherry.global.exception.error.GlobalError;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * @RequestBody 누락: HttpMessageNotReadableException
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ProblemDetail handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
		// Jackson이 던진 가장 구체적인 예외
		Throwable cause = ex.getMostSpecificCause();

		// 1) Jackson의 형식 불일치(예: String → Enum 변환 실패)
		if (cause instanceof InvalidFormatException ife) {
			// 어떤 타입으로 매핑하려다 실패했는지
			Class<?> targetType = ife.getTargetType();

			// 대상 타입이 Enum이면 "없는 Enum 값" 케이스로 분기
			if (targetType != null && targetType.isEnum()) {
				String badValue = String.valueOf(ife.getValue());
				@SuppressWarnings("unchecked")
				Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>)targetType;

				// 허용 가능한 값들
				String allowed = Arrays.stream(enumClass.getEnumConstants())
					.map(Enum::name)
					.sorted()
					.collect(Collectors.joining(", "));

				String fieldPath = ife.getPath() != null && !ife.getPath().isEmpty()
					? ife.getPath().stream()
					.map(ref -> ref.getFieldName() != null ? ref.getFieldName() : "[" + ref.getIndex() + "]")
					.collect(Collectors.joining("."))
					: null;

				String details = fieldPath != null
					? "필드 '%s'에 허용되지 않는 값 '%s' 입니다. 허용값: [%s]".formatted(fieldPath, badValue, allowed)
					: "허용되지 않는 Enum 값 '%s' 입니다. 허용값: [%s]".formatted(badValue, allowed);
				return getProblemDetail(GlobalError.INVALID_REQUEST_PARAM, ex, details);
			}
		}
		String details = cause.getMessage();
		return getProblemDetail(GlobalError.INVALID_REQUEST_PARAM, ex, details);
	}

	/**
	 * @RequestBody 유효성 검사 실패 처리: MethodArgumentNotValidException
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
		Map<String, String> details = new HashMap<>();
		exception.getBindingResult().getFieldErrors().forEach(error -> {
			details.put(error.getField(), error.getDefaultMessage());
		});
		return getProblemDetail(GlobalError.INVALID_REQUEST_BODY, exception, details);
	}

	/**
	 * 파라미터 타입 불일치 처리: MethodArgumentTypeMismatchException, TypeMismatchException
	 */
	@ExceptionHandler({MethodArgumentTypeMismatchException.class, TypeMismatchException.class})
	public ProblemDetail handleTypeMismatch(Exception exception) {
		return getProblemDetail(GlobalError.INVALID_REQUEST_PARAM, exception, "요청 파라미터 타입이 올바르지 않습니다.");
	}

	/**
	 * @RequestParam 누락 처리: MissingServletRequestParameterException
	 */
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ProblemDetail handleMissingServletRequestParameter(MissingServletRequestParameterException exception) {
		String detail = String.format("필수 파라미터 '%s' 누락되었습니다", exception.getParameterName());
		return getProblemDetail(GlobalError.MISSING_REQUEST_PARAM, exception, detail);
	}

	/**
	 * @PathVariable, @RequestParam 유효성 검사 실패 처리: ConstraintViolationException
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	public ProblemDetail handleConstraintViolation(ConstraintViolationException exception) {
		Map<String, String> detail = new HashMap<>();
		exception.getConstraintViolations().forEach(violation -> {
			String propertyPath = violation.getPropertyPath().toString();
			String fieldName = propertyPath.contains(".") ? 
				propertyPath.substring(propertyPath.lastIndexOf(".") + 1) : propertyPath;
			String message = violation.getMessage();
			detail.put(fieldName, message);
		});

		return getProblemDetail(GlobalError.INVALID_REQUEST_PARAM, exception, detail);
	}

	/**
	 * JWT 형식 오류 처리: JwtException
	 */
	@ExceptionHandler(JwtException.class)
	public ProblemDetail handleMalformedJwt(JwtException exception) {
		return getProblemDetail(GlobalError.UNAUTHORIZED_ACCESS, exception, "유효하지 않은 JWT 형식입니다.");
	}

	/**
	 * 허용되지 않은 메소드 예외 처리
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ProblemDetail handleMethodNotAllowed(HttpRequestMethodNotSupportedException exception) {
		String detail = String.format("허용되지 않은 메소드: %s", exception.getMethod());
		return getProblemDetail(GlobalError.METHOD_NOT_ALLOWED, exception, detail);
	}

	/**
	 * Domain 예외 처리
	 */
	@ExceptionHandler(DomainException.class)
	public ProblemDetail handleDomainException(DomainException exception) {
		return getProblemDetail(exception.getErrorCode(), exception);
	}

	/**
	 * Business 예외 처리
	 */
	@ExceptionHandler(BusinessException.class)
	public ProblemDetail handleBusinessException(BusinessException exception) {
		return getProblemDetail(exception.getErrorCode(), exception);
	}

	/**
	 * 예상하지 못한 모든 예외 처리: Exception
	 */
	@ExceptionHandler(Exception.class)
	public ProblemDetail exceptionHandler(Exception exception) {
		log.error("Unhandled exception occurred", exception);
		return getProblemDetail(GlobalError.INTERNAL_SERVER_ERROR, exception);
	}

	private ProblemDetail getProblemDetail(ErrorCode errorCode, Exception exception) {
		ProblemDetail problemDetail = ProblemDetail.forStatus(errorCode.getStatus());
		problemDetail.setTitle(errorCode.getMessage());
		if (exception instanceof BusinessException || exception instanceof DomainException) {
			if (exception.getMessage() != null && !exception.getMessage().isEmpty()) {
				problemDetail.setDetail(exception.getMessage());
			}
		}
		problemDetail.setProperty("code", errorCode.getCode());
		problemDetail.setProperty("timestamp", LocalDateTime.now());
		problemDetail.setProperty("exception", exception.getClass().getSimpleName());
		return problemDetail;
	}

	private ProblemDetail getProblemDetail(GlobalError errorCode, Exception exception, Object detail) {
		ProblemDetail problemDetail = ProblemDetail.forStatus(errorCode.getStatus());
		problemDetail.setTitle(errorCode.getMessage());
		if (detail instanceof String s) {
			problemDetail.setDetail(s);
		} else {
			problemDetail.setProperty("errors", detail);
		}
		problemDetail.setProperty("code", errorCode.getCode());
		problemDetail.setProperty("timestamp", LocalDateTime.now());
		problemDetail.setProperty("exception", exception.getClass().getSimpleName());
		return problemDetail;
	}
}
