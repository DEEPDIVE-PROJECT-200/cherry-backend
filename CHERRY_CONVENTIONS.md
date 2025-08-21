# CHERRY 코딩 컨벤션

이 문서는 팀의 고유 컨벤션과 [네이버 핵데이 Java 코딩 컨벤션](https://naver.github.io/hackday-conventions-java/)을 기반으로 작성되었습니다.

## 0. 파일 공통 요건

### 0.1 파일 인코딩은 UTF-8

- 모든 소스, 텍스트 문서 파일의 인코딩은 UTF-8로 통일

### 0.2 새줄 문자는 LF

- Unix 형식의 새줄 문자(LF, 0x0A) 사용
- Windows 형식인 CRLF가 섞이지 않도록 편집기와 Git 설정 확인

### 0.3 파일의 마지막에는 새줄

- 파일의 마지막은 새줄 문자 LF로 끝나야 함

### 0.4 최대 줄 너비는 120자

- 한 줄의 최대 길이는 120자까지 허용

## 1. 클래스 구조 및 포맷팅

### 1.1 클래스 시작 시 띄어쓰기

- **모든 클래스, record, interface, enum은 선언 후 반드시 한 줄 띄어쓰기**

```java
// ✅ 올바른 예시 - Class
public class Member {
// 띄어쓰기
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}

// ✅ 올바른 예시 - Record
public record Email(String address) {
// 띄어쓰기
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,63}$");
}

// ✅ 올바른 예시 - Enum
public enum MemberError implements ErrorCode {
// 띄어쓰기
    NOT_ACTIVE_STATUS("ACTIVE 상태가 아닙니다", HttpStatus.NOT_FOUND, "M_001"),
    INVALID_EMAIL("이메일 형식이 바르지 않습니다", HttpStatus.CONFLICT, "M_002"),
    INVALID_NICKNAME("닉네임은 2 ~ 10글자만 가능합니다", HttpStatus.UNAUTHORIZED, "M_003");
}
```

## 2. Import 관리

### 2.1 불필요한 Import 제거

- 사용하지 않는 import문은 반드시 제거
- IDE의 자동 정리 기능 활용 권장

### 2.2 static import에만 와일드카드 허용

- 클래스 import 시 와일드카드(*) 없이 모든 클래스명을 명시
- static import에서는 와일드카드 허용

```java
// ❌ 잘못된 예시
import java.util.*;

// ✅ 올바른 예시
import java.util.List;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;  // static import는 허용
```

### 2.3 import 선언 순서와 그룹핑

import 구절은 다음 순서로 그룹을 묶어서 선언하고, 각 그룹 사이에는 빈줄 삽입:

1. static imports
2. java.*
3. javax.*
4. org.*
5. net.*
6. 1~5를 제외한 com.*
7. 1~6을 제외한 패키지
8. com.nhncorp.*
9. com.navercorp.*
10. com.naver.*

```java
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.springframework.util.Assert;

import com.google.common.base.Function;

import com.naver.lucy.util.AnnotationUtils;
```

## 3. 네이밍 규칙

### 3.1 식별자에는 영문/숫자/언더스코어만 허용

- 변수명, 클래스명, 메서드명 등에는 영어와 숫자만 사용
- 상수에는 단어 사이 구분을 위해 언더스코어(_) 사용

### 3.2 한국어 발음대로의 표기 금지

- 한글 발음을 영어로 옮겨서 표기하지 않음
- 한국어 고유명사는 예외

```java
// ❌ 나쁜 예
String moohyungJasan;  // 무형자산

// ✅ 좋은 예
String intangibleAssets;  // 무형자산
```

### 3.3 패키지 이름은 소문자로 구성

- 소문자만 사용하여 작성
- 언더스코어(_)나 대문자를 섞지 않음

```java
// ❌ 잘못된 예시
package com.navercorp.apiGateway;
package com.navercorp.api_gateway;

// ✅ 올바른 예시
package com.navercorp.apigateway;
```

### 3.4 클래스/인터페이스 이름에 대문자 카멜표기법 적용

- 대문자 카멜표기법(Upper camel case) 사용
- 클래스 이름은 명사나 명사절로 작성
- 인터페이스 이름은 명사/명사절 또는 형용사/형용사절로 작성

```java
// ✅ 올바른 예시
public class AccessToken {  // 명사
}

public interface RowMapper {  // 명사
}

public interface AutoClosable {  // 형용사
}
```

### 3.5 메서드 이름에 소문자 카멜표기법 적용

- 소문자 카멜표기법(Lower camel case) 사용
- 기본적으로 동사로 시작
- 전환 메서드나 빌더 패턴에서는 전치사 사용 가능

```java
// ✅ 올바른 예시
public void renderHtml() {}  // 동사
public String toString() {}  // 전환메서드의 전치사
public Builder withUserId(String id) {}  // Builder 패턴의 전치사
```

### 3.6 상수는 대문자와 언더스코어로 구성

- static final로 선언된 상수는 대문자로 작성
- 복합어는 언더스코어(_)로 단어 구분

```java
public static final int UNLIMITED = -1;
public static final String POSTAL_CODE_EXPRESSION = "POST";
```

### 3.7 변수에 소문자 카멜표기법 적용

- 멤버변수, 지역변수, 메서드 파라미터에 소문자 카멜표기법 사용

### 3.8 임시 변수 외에는 1글자 이름 사용 금지

- 메서드 블럭 범위 이상의 변수에는 1글자 이름 사용 금지
- 반복문 인덱스나 람다 표현식 파라미터 등 짧은 범위의 임시 변수는 예외

### 3.9 테스트 클래스는 'Test'로 끝남

- JUnit 등으로 작성한 테스트 클래스는 'Test'를 마지막에 붙임

```java
public class MemberServiceTest {
}
```

### 5.1 Controller/Service 클래스 구조

```java
@RestController
@RequiredArgsConstructor
@Slf4j
public class KakaoAuthController {
// 띄어쓰기
    // 1. private static final 상수들
    private static final int REFRESH_TOKEN_MAX_AGE = 7 * 24 * 60 * 60;
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
// 띄어쓰기
    // 2. @Autowired 의존성 (또는 final 필드)
    private final KakaoAuthService kakaoAuthService;
    private final CookieManager cookieManager;
    private final KakaoUtil kakaoUtil;
// 띄어쓰기
    // 3. public 메서드들
    @PostMapping("/api/auth/kakao")
    public ResponseEntity<AuthResponse> login(@RequestBody KakaoLoginRequest request) {
        // 구현
    }
// 띄어쓰기
    // 4. private 메서드들 (마지막에 위치)
    private void validateToken(String token) {
        // 구현
    }
}
```

### 5.2 Entity 클래스에서 private 메서드 위치

```java
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
// 띄어쓰기
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // public 메서드들
    public void updateEmail(String emailAddress) {
        validateIsActive();
        this.email = new Email(emailAddress);
    }

    public void updateNickname(String nickname) {
        validateIsActive();
        validateNickname(nickname);
        this.nickname = nickname;
    }
    
    // private 메서드들은 마지막에 위치
    private static void validateNickname(String nickname) {
        if (nickname.length() < 2 || nickname.length() > 10) {
            throw new DomainException(MemberError.INVALID_NICKNAME);
        }
    }

    private void validateIsActive() {
        if (status != MemberStatus.ACTIVE) {
            throw new DomainException(MemberError.NOT_ACTIVE_STATUS);
        }
    }
}
```

## 6. 들여쓰기

### 6.1 하드탭 사용

- 탭(tab) 문자를 사용하여 들여쓰기
- 스페이스 대신 탭 사용
- 에디터에서 스페이스와 탭을 구별해서 보도록 설정

### 6.2 탭의 크기는 4개의 스페이스

- 1개의 탭 크기는 스페이스 4개와 같도록 에디터에서 설정

### 6.3 블럭 들여쓰기

- 클래스, 메서드, 제어문 등의 코드 블럭이 생길 때마다 1단계 더 들여쓰기

### 6.4 줄바꿈 후 추가 들여쓰기

- 줄바꿈 이후 이어지는 줄에서는 최초 시작한 줄보다 적어도 1단계 더 들여쓰기

## 7. 중괄호

### 7.1 K&R 스타일로 중괄호 선언

- 줄의 마지막에서 시작 중괄호({) 작성 후 새줄 삽입
- 블럭 완료 후 새줄 삽입 후 중괄호 닫기

```java
public class SearchConditionParser {
    public boolean isValidExpression(String exp) {
        if (exp == null) {
            return false;
        }
        return true;
    }
}
```

### 7.2 닫는 중괄호와 같은 줄에 else, catch, finally, while 선언

```java
if (condition) {
    doSomething();
} else if (otherCondition) {
    doOther();
} else {
    doDefault();
}

try {
    writeLog();
} catch (IOException ioe) {
    reportFailure(ioe);
} finally {
    writeFooter();
}

do {
    write(line);
    line = readLine();
} while (line != null);
```

### 7.3 빈 블럭에 새줄 없이 중괄호 닫기 허용

```java
public void close() {}
```

### 7.4 조건/반복문에 중괄호 필수 사용

- 한 줄로 끝나더라도 중괄호 사용

```java
// ❌ 잘못된 예시
if (exp == null) return false;

// ✅ 올바른 예시
if (exp == null) {
    return false;
}
```

## 8. 줄바꿈

### 8.1 package, import 선언문은 한 줄로

- 최대 줄수를 초과하더라도 한 줄로 작성

### 8.2 줄바꿈 허용 위치

가독성을 위해 줄을 바꾸는 위치:

- extends 선언 후
- implements 선언 후
- throws 선언 후
- 시작 소괄호(() 선언 후
- 콤마(,) 후
- 점(.) 전
- 연산자 전 (+, -, *, /, %, ==, !=, >=, >, <=, <, &&, ||, &, |, ^, >>>, >>, <<, ?, instanceof)

## 9. 빈 줄

### 9.1 package 선언 후 빈 줄 삽입

```java
package com.naver.lucy.util;

import java.util.Date;
```

### 9.2 메서드 사이에 빈 줄 삽입

```java
public void setId(int id) {
    this.id = id;
}

public void setName(String name) {
    this.name = name;
}
```

## 10. 공백

### 10.1 공백으로 줄을 끝내지 않음

- 모든 줄은 탭이나 공백으로 끝내지 않음

### 10.2 중괄호의 시작 전, 종료 후에 공백 삽입

```java
public void printMessage() {
    if (condition) {
        // 처리
    } else {
        // 다른 처리
    }
}
```

### 10.3 제어문 키워드와 여는 소괄호 사이에 공백 삽입

```java
if (condition) {
    // 처리
}

for (int i = 0; i < length; i++) {
    // 처리
}
```

### 10.4 식별자와 여는 소괄호 사이에 공백 미삽입

```java
// ✅ 올바른 예시
public StringProcessor() {}
@Cached("local")
public String removeEndingDot(String original) {
    assertNotNull(original);
}
```

### 10.5 콤마/구분자 세미콜론의 뒤에만 공백 삽입

```java
for (int i = 0; i < length; i++) {
    display(level, message, i);
}
```

### 10.6 이항/삼항 연산자의 앞 뒤에 공백 삽입

```java
finalScore += weight * rawScore - absentCount;
String result = condition ? "yes" : "no";
```

### 10.7 단항 연산자와 연산 대상 사이에 공백 미삽입

```java
int point = score[++index] * rank-- * -1;
```

### 10.8 주석문 기호 전후의 공백 삽입

```java
System.out.print(true); // 주석 기호 앞 뒤로 공백
/* 주석내용 앞에 공백, 뒤에도 공백 */
```

## 11. 테스트 코드 작성 규칙

### 11.1 테스트 메서드 구조

- **@DisplayName 어노테이션으로 테스트 의도 명확히 표현**
- **given, when, then 주석으로 테스트 단계 구분**

```java
class EmailTest {

    @Test
    @DisplayName("같은 이메일 주소를 가진 Email 객체들은 동등하다")
    void equality() {
        // given
        var email1 = new Email("test@test.com");
        var email2 = new Email("test@test.com");

        // when & then
        assertThat(email1).isEqualTo(email2);
    }

    @Test
    @DisplayName("잘못된 이메일 형식으로 Email 객체 생성 시 예외가 발생한다")
    void createEmailFail() {
        // given
        String wrongAddress = "wrongAddress";

        // when & then
        assertThatThrownBy(() -> new Email(wrongAddress))
            .isInstanceOf(DomainException.class)
            .hasMessage(MemberError.INVALID_EMAIL.getMessage());
    }

    @Test
    @DisplayName("회원 비활성화 시 상태가 변경되고 비활성화 일자가 설정된다")
    void deactivate() {
        // given
        Member member = Member.register("12345", Provider.KAKAO, "test@test.com", "tester");

        // when
        member.deactivate();

        // then
        assertThat(member.getStatus()).isEqualTo(MemberStatus.DEACTIVATED);
        assertThat(member.getDetail().getDeactivatedAt()).isNotNull();
    }
}
```

## 12. 엔티티 생성 패턴

### 12.1 정적 팩토리 메서드 사용

- **@Builder 패턴 사용 지양**
- **정적 팩토리 메서드로 엔티티 생성**

```java
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
// 띄어쓰기
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    // ✅ 정적 팩토리 메서드 사용
    public static Member register(String providerId, Provider provider, String emailAddress, String nickname) {
        validateNickname(nickname);

        Member member = new Member();
        member.email = new Email(emailAddress);
        member.providerId = providerId;
        member.provider = provider;
        member.nickname = nickname;
        member.status = MemberStatus.ACTIVE;
        member.detail = MemberDetail.create();
        return member;
    }

    // ❌ @Builder 사용 지양
    // @Builder
    // public static Member createMember(...) { ... }
}
```

## 13. 서비스 계층 아키텍처

### 13.1 ApplicationService 패턴

- **복잡한 비즈니스 로직은 XXXApplicationService에서 처리**
- **여러 Service를 조합하는 역할**

#### 규칙:

1. Service가 ApplicationService를 사용하면 **안됨**
2. ApplicationService가 다른 ApplicationService를 사용하면 **안됨**
3. ApplicationService만 여러 Service를 조합하여 사용

```java
// ✅ 올바른 구조
@Service
@RequiredArgsConstructor
public class RentalApplicationService {
// 띄어쓰기
    private final RentalService rentalService;
    private final PaymentService paymentService;
    private final ShipService shipService;
    private final ProductItemService productItemService;
    
    @Transactional
    public void processRentalFromCart(Long memberId, CartRequest request) {
        // 1. 대여 생성
        Rental rental = rentalService.createRental(memberId, request);
        
        // 2. 결제 처리
        Payment payment = paymentService.processPayment(rental.getId(), rental.getTotalPrice());
        
        // 3. 대여 확정
        rentalService.confirmRental(rental.getId());
        
        // 4. 상품 아이템 상태 변경
        productItemService.markAsRented(request.getProductItemIds());
        
        // 5. 배송 생성
        shipService.createShipping(rental.getId(), memberId, request.getAddress());
    }
}

// ✅ 일반 Service는 단일 책임
@Service
@RequiredArgsConstructor
public class RentalService {
// 띄어쓰기
    private final RentalRepository rentalRepository;
    
    public Rental createRental(Long memberId, CartRequest request) {
        // 대여 생성 로직만 처리
    }
    
    public void confirmRental(Long rentalId) {
        // 대여 확정 로직만 처리
    }
}
```

### 13.2 금지사항

```java
// ❌ Service에서 ApplicationService 사용 금지
@Service
public class RentalService {
    private final RentalApplicationService rentalApplicationService; // 금지!
}

// ❌ ApplicationService에서 다른 ApplicationService 사용 금지
@Service
public class RentalApplicationService {
    private final PaymentApplicationService paymentApplicationService; // 금지!
}
```

## 14. 일반적인 네이밍 및 포맷팅

### 14.1 변수 및 메서드명

- camelCase 사용
- 의미 있는 이름 사용
- boolean 변수는 is, has, can 등의 접두사 사용

### 14.2 상수명

- UPPER_SNAKE_CASE 사용
- private static final 순서로 선언

### 14.3 패키지명

- 소문자 사용
- 단수형 사용 (member, product, rental)

## 15. 추가 권장사항

### 15.1 특수 문자의 전용 선언 방식 활용

```java
// ❌ 잘못된 예시
System.out.println("---\012---");

// ✅ 올바른 예시
System.out.println("---\n---");
```

### 15.2 타입 캐스팅에 쓰이는 소괄호 내부 공백 미삽입

```java
// ❌ 잘못된 예시
String message = ( String ) rawLine;

// ✅ 올바른 예시
String message = (String)rawLine;
```

### 15.3 제네릭스 산괄호의 공백 규칙

```java
// 제네릭스 메서드 선언 시에만 < 앞에 공백
public static <A extends Annotation> A find(AnnotatedElement elem, Class<A> type) {
    List<Integer> l1 = new ArrayList<>();  // '(' 가 바로 이어질 때
    List<String> l2 = ImmutableList.Builder<String>::new;  // 메서드 레퍼런스
    int diff = Util.<Integer, String>compare(l1, l2);  // 메서드 이름이 바로 이어질 때
}
```

### 15.4 콜론의 앞 뒤에 공백 삽입

```java
for (Customer customer : visitedCustomers) {
    AccessPattern pattern = isAbnormal(accessLog) ? AccessPattern.ABUSE : AccessPattern.NORMAL;
    switch (grade) {
        case GOLD :
            sendSms(customer);
            break;
        default :
            increasePoint(customer);
    }
}
```

---

## 7. 일반적인 네이밍 및 포맷팅

### 7.1 변수 및 메서드명

- camelCase 사용
- 의미 있는 이름 사용
- boolean 변수는 is, has, can 등의 접두사 사용

### 7.2 상수명

- UPPER_SNAKE_CASE 사용
- private static final 순서로 선언

### 7.3 패키지명

- 소문자 사용
- 단수형 사용 (member, product, rental)

---

이 컨벤션은 팀의 코드 일관성과 가독성을 위해 반드시 준수해야 합니다.