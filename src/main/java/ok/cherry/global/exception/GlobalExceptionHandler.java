package ok.cherry.global.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import ok.cherry.global.exception.error.BusinessException;
import ok.cherry.global.exception.error.DomainException;
import ok.cherry.global.exception.error.ErrorCode;
import ok.cherry.global.exception.error.GlobalError;

@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * @RequestBody 누락: HttpMessageNotReadableException
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ProblemDetail handleHttpMessageNotReadable(HttpMessageNotReadableException exception, HttpServletRequest request) {
		return getProblemDetail(GlobalError.INVALID_REQUEST_BODY, exception);
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
