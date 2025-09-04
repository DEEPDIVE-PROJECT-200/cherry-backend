# 한국어 코드 리뷰 가이드라인 - CHERRY 프로젝트 전용

## 언어 설정
- **모든 코드 리뷰는 한국어로 작성해주세요.**
- 기술 용어는 필요시 영어 원문을 병기하되, 주요 설명은 한국어로 제공해주세요.
- 예시: "메모리 누수(Memory Leak)가 발생할 수 있습니다."

## CHERRY 프로젝트별 특수 규칙

### 1. 클래스 구조 검토 항목

#### 1.1 클래스 시작 시 띄어쓰기 검사
- **모든 클래스, record, interface, enum은 선언 후 반드시 한 줄 띄어쓰기가 있어야 합니다.**

```java
// ❌ 문제가 되는 코드:
public class Member {
    @Id  // 띄어쓰기 없음
    private Long id;
}

// ✅ 올바른 코드:
public class Member {
// 띄어쓰기
    @Id
    private Long id;
}
```

💡 **설명**: 클래스 가독성을 위해 선언 후 한 줄 띄어쓰기를 반드시 적용해주세요.

#### 1.2 Controller/Service 클래스 구조 검토
올바른 멤버 순서:
1. private static final 상수들
2. @Autowired 의존성 (또는 final 필드)
3. public 메서드들
4. private 메서드들 (마지막에 위치)

```java
❌ 문제가 되는 코드:
@RestController
public class KakaoAuthController {
    private final KakaoAuthService kakaoAuthService;
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken"; // 순서 잘못
    
    private void validateToken(String token) {} // private 메서드가 중간에
    
    @PostMapping("/api/auth/kakao")
    public ResponseEntity<AuthResponse> login() {}
}

✅ 개선된 코드:
@RestController
public class KakaoAuthController {
// 띄어쓰기
    // 1. 상수들 먼저
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
// 띄어쓰기    
    // 2. 의존성 필드들
    private final KakaoAuthService kakaoAuthService;
// 띄어쓰기
    // 3. public 메서드들
    @PostMapping("/api/auth/kakao")
    public ResponseEntity<AuthResponse> login() {}
// 띄어쓰기
    // 4. private 메서드들은 마지막에
    private void validateToken(String token) {}
}
```

### 2. 엔티티 생성 패턴 검토

#### 2.1 @Builder 패턴 사용 지양
- **@Builder 패턴 대신 정적 팩토리 메서드를 사용해야 합니다.**

```java
❌ 문제가 되는 코드:
@Entity
@Builder  // Builder 패턴 사용
public class Member {
    // 필드들
}

✅ 개선된 코드:
@Entity
public class Member {
    // 정적 팩토리 메서드 사용
    public static Member register(String providerId, Provider provider, String emailAddress, String nickname) {
        validateNickname(nickname);
        Member member = new Member();
        // 초기화 로직
        return member;
    }
}
```

💡 **설명**: 정적 팩토리 메서드는 생성 의도를 명확히 하고, 검증 로직을 포함할 수 있어 더 안전합니다.

### 3. 서비스 계층 아키텍처 검토

#### 3.1 ApplicationService 패턴 검증
**중요한 아키텍처 규칙들:**
1. Service → ApplicationService 사용 **금지**
2. ApplicationService → ApplicationService 사용 **금지**
3. ApplicationService만 여러 Service를 조합하여 사용

```java
❌ 문제가 되는 코드:
@Service
public class RentalService {
    private final RentalApplicationService rentalApplicationService; // 금지!
}

@Service  
public class RentalApplicationService {
    private final PaymentApplicationService paymentApplicationService; // 금지!
}

✅ 올바른 코드:
@Service
public class RentalApplicationService {
    // ApplicationService는 여러 Service를 조합
    private final RentalService rentalService;
    private final PaymentService paymentService;
    private final ShipService shipService;
}
```

💡 **설명**: 이 규칙을 통해 순환 참조를 방지하고 계층 구조를 명확히 유지할 수 있습니다.

## 네이버 핵데이 컨벤션 기반 검토 항목

### 4. 네이밍 규칙

#### 4.1 한국어 발음 영어 표기 금지
```java
❌ 문제가 되는 코드:
String moohyungJasan; // 무형자산

✅ 개선된 코드:
String intangibleAssets; // 무형자산
```

#### 4.2 패키지명 소문자 규칙
```java
❌ 문제가 되는 코드:
package com.cherry.apiGateway;
package com.cherry.api_gateway;

✅ 개선된 코드:
package com.cherry.apigateway;
```

### 5. Import 관리

#### 5.1 불필요한 Import 제거
- 사용하지 않는 import문을 발견하면 제거를 권장해주세요.

#### 5.2 와일드카드 Import 사용 제한
```java
❌ 문제가 되는 코드:
import java.util.*;

✅ 개선된 코드:
import java.util.List;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*; // static import만 허용
```

### 6. 포맷팅 규칙

#### 6.1 중괄호 K&R 스타일 검증
```java
❌ 문제가 되는 코드:
public class Member
{
    public void method()
    {
        // 구현
    }
}

✅ 개선된 코드:
public class Member {
    public void method() {
        // 구현
    }
}
```

#### 6.2 조건/반복문 중괄호 필수 사용
```java
❌ 문제가 되는 코드:
if (condition) return false;

✅ 개선된 코드:
if (condition) {
    return false;
}
```

#### 6.3 최대 줄 길이 120자 준수
- 120자를 초과하는 줄을 발견하면 적절한 줄바꿈을 제안해주세요.

### 7. 테스트 코드 검토

#### 7.1 테스트 메서드 구조 검증
- **@DisplayName 어노테이션으로 테스트 의도가 명확히 표현되어야 합니다.**
- **given, when, then 주석으로 테스트 단계가 구분되어야 합니다.**

```java
❌ 문제가 되는 코드:
@Test
void test() {
    Email email1 = new Email("test@test.com");
    Email email2 = new Email("test@test.com");
    assertThat(email1).isEqualTo(email2);
}

✅ 개선된 코드:
@Test
@DisplayName("같은 이메일 주소를 가진 Email 객체들은 동등하다")
void equality() {
    // given
    var email1 = new Email("test@test.com");
    var email2 = new Email("test@test.com");

    // when & then
    assertThat(email1).isEqualTo(email2);
}
```

## 코드 리뷰 우선순위

### 1. 정확성 (Correctness) - Critical/High
- 로직 오류 및 버그 가능성을 한국어로 명확히 설명해주세요.
- NullPointerException 가능성을 체크해주세요.
- 엣지 케이스 처리 누락을 지적할 때는 구체적인 시나리오를 제시해주세요.

### 2. CHERRY 프로젝트 아키텍처 준수 - High
- ApplicationService 패턴 위반 사항을 엄격히 검토해주세요.
- 엔티티 생성 패턴(@Builder 사용 등) 위반 사항을 확인해주세요.

### 3. 네이버 핵데이 컨벤션 준수 - Medium/High
- 클래스 구조, 네이밍, 포맷팅 규칙 준수 여부를 확인해주세요.
- 띄어쓰기, 중괄호 스타일 등 기본 포맷팅을 검토해주세요.

### 4. 보안 (Security) - Critical/High
- 입력 검증 누락을 확인해주세요.
- SQL 인젝션, XSS 등의 보안 취약점을 점검해주세요.

### 5. 성능 및 효율성 - Medium
- N+1 쿼리 문제를 확인해주세요.
- 불필요한 반복문이나 비효율적인 데이터 구조 사용을 지적해주세요.

### 6. 유지보수성 - Medium/Low
- 코드 가독성 개선을 위한 구체적인 제안을 해주세요.
- 매직 넘버를 상수로 추출하는 것을 권장해주세요.

## 리뷰 응답 템플릿

### CHERRY 프로젝트 특수 규칙 위반 시
```
## 🚨 CHERRY 프로젝트 아키텍처 규칙 위반

**심각도**: High

**문제점**: [구체적인 위반 사항]

**제안사항**: [CHERRY 프로젝트 규칙에 맞는 개선 방법]

**참고**: CHERRY 코딩 컨벤션 문서의 해당 섹션을 참조하세요.
```

### 일반적인 리뷰 댓글
```
## 🔍 코드 리뷰 요약

**심각도**: [Critical/High/Medium/Low]

**문제점**: [구체적인 문제 설명]

**제안사항**: [개선 방법]

**참고자료**: [네이버 핵데이 컨벤션 또는 관련 문서 링크]
```

### 긍정적인 피드백
```
## 👍 잘 작성된 코드

이 부분은 [구체적인 칭찬 포인트]로 매우 잘 작성되었습니다.
특히 CHERRY 프로젝트의 [특정 규칙]을 잘 따르고 있어 인상적입니다.
```

## 추가 지침

### CHERRY 프로젝트 특화 사항
- **@Builder 패턴 사용**을 발견하면 정적 팩토리 메서드로 변경을 제안해주세요.
- **클래스 선언 후 띄어쓰기 누락**은 Medium 우선순위로 지적해주세요.

### 교육적 가치 중시
- 왜 특정 방식이 더 좋은지 CHERRY 프로젝트 컨텍스트에서 설명해주세요.
- 네이버 핵데이 컨벤션의 배경과 이유를 설명해주세요.

### 일관성 유지
- CHERRY 프로젝트 규칙과 네이버 핵데이 컨벤션을 모두 고려하여 리뷰해주세요.
- 비슷한 문제에 대해서는 동일한 기준으로 리뷰해주세요.

## 마무리
이 가이드라인을 따라 **CHERRY 프로젝트의 아키텍처 규칙과 네이버 핵데이 Java 컨벤션을 모두 고려한 한국어 코드 리뷰**를 제공해주세요. 개발자들이 프로젝트의 코딩 표준을 익히고 더 나은 코드 품질을 달성할 수 있도록 도와주는 것이 목표입니다.