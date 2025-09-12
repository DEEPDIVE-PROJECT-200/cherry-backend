# 🍒 Cherry

## 🙌🏻 멤버

<table>
  <tbody>
    <tr>
      <td align="center">
        <a href="https://github.com/chanwonlee"><img src="https://avatars.githubusercontent.com/u/116537544?v=4" width="100px;" alt="이찬원"/><br /></a>
      </td>
      <td align="center">
        <a href="https://github.com/Ogu1208"><img src="https://avatars.githubusercontent.com/u/76902448?v=4" width="100px;" alt="김민아"/><br /></a>
      </td>
      <td align="center">
        <a href="https://github.com/Jimin730"><img src="https://avatars.githubusercontent.com/u/108002997?v=4" width="100px;" alt="신지민"/><br /></a>
      </td>
    </tr>
    <tr>
      <td align="center"><a href="https://github.com/chanwonlee">이찬원</a></td>
      <td align="center"><a href="https://github.com/Ogu1208">김민아</a></td>
      <td align="center"><a href="https://github.com/Jimin730">신지민</a></td>
    </tr>
  </tbody>
</table>

## 📋 목차

- [프로젝트 개요](#-프로젝트-개요)
- [기술 스택](#-기술-스택)
- [아키텍처](#-아키텍처)
- [주요 기능](#-주요-기능)
- [주요 엔드포인트](#-주요-엔드포인트)

## 📖 프로젝트 개요

**Cherry**는 최신 프리미엄 IT 기기를 간편하게 대여하고 반납할 수 있는 렌탈 플랫폼입니다. <br>
실제 렌탈 서비스의 핵심 기능(상품 조회, 대여, 결제, 배송)을 구현하며 현업에서 사용되는 기술 스택과 아키텍처 설계를 학습하고 적용한 프로젝트 입니다.

### 📅 진행 기간
- 초기 MVP 출시 (기획 및 개발) : 2025. 08. 11. ~ 2025. 09. 15.

### 🎯 핵심 목표

- **초기 MVP 출시를 위한 유기적 협업**: 기획자, 디자이너, 백엔드, 프론트엔드 개발자가 하나의 팀으로서 긴밀하게 소통하고 협력하여 완성도 높은 초기 MVP를 출시합니다.
- **직군 간 역할 분담 및 전문성 강화**: 각자의 전문 분야에 따라 역할을 명확히 분담하고, 책임감 있는 자세로 자신의 역할을 수행하며 전문성을 높입니다.
- **효율적인 협업 도구 활용**: GitHub, Notion, Figma, Discord 등 다양한 협업 도구를 적극적으로 활용하여 체계적인 버전 관리, 이슈 트래킹, 코드 리뷰, 디자인 시스템 공유를 통해 원활한 협업 환경을 구축합니다.
- **실무 중심의 개발 문화 경험**: 실제 현업과 유사한 애자일 스프린트, 데일리 스크럼, 회고 등의 개발 문화를 도입하여 실무적인 문제 해결 능력과 커뮤니케이션 스킬을 함양합니다.


## 🔧 기술 스택

### Backend

[![backend](https://skillicons.dev/icons?i=java,spring,redis,mysql,gradle)](https://skillicons.dev)

- **Language** : Java 21
- **Framework** : Spring Boot 3.5, Spring Security, Spring OAuth2 Client, Thymeleaf
- **ORM** : Spring Data JPA, QueryDSL
- **DB** : MySQL (Production), H2 (Test)
- **Cache** : Redis
- **Cloud Storage** : AWS S3
- **Authentication** : JWT, Redis Session Store
- **API Documentation** : Springdoc OpenAPI (Swagger UI)
- **Build Tool** : Gradle

### Infrastructure & DevOps

[![infra,devops](https://skillicons.dev/icons?i=git,github,docker,kubernetes,aws,prometheus,grafana,githubactions)](https://skillicons.dev)

- **Cloud Platform** : AWS (EKS, RDS, Route53, Load Balancer)
- **Container** : Docker, Kubernetes
- **CI/CD** : GitHub Actions, ArgoCD (GitOps)
- **Monitoring** : Prometheus, Grafana
- **VCS** : Git, GitHub

## 🏗️ 아키텍처
<img width="1092" height="607" alt="image" src="https://github.com/user-attachments/assets/c341ec73-d809-4e79-9f64-242f80af750b" />

### ERD
<img width="1271" height="688" alt="image" src="https://github.com/user-attachments/assets/f6212bca-a2ae-4624-9ffb-97e1eb6badc6" />

### 프로젝트 폴더 구조

```
src/main/java/ok/cherry/
├── admin/      # 관리자 도메인
├── auth/       # 인증 및 인가 도메인
├── cart/       # 장바구니 도메인
├── global/     # 공통 기능 및 설정
├── member/     # 회원 도메인
├── payment/    # 결제 도메인
├── product/    # 상품 도메인
├── rental/     # 대여 도메인
└── shipping/   # 배송 도메인
```

## 🚀 주요 기능

### 👑 Admin 도메인

- **관리자 전용 페이지**: Thymeleaf를 사용하여 회원, 상품, 대여 내역을 관리할 수 있는 웹 페이지를 제공합니다.
- **독립된 인증 시스템**: 관리자 전용 로그인 및 보안 설정을 통해 시스템의 주요 기능을 안전하게 관리합니다.

### 🔑 Auth 도메인

- **JWT 기반 인증 시스템**: Access Token과 Refresh Token을 활용한 Stateless 인증 및 인가.
- **소셜 로그인**: Kakao OAuth2를 이용한 간편 로그인 기능.
- **보안 강화**: 로그아웃 처리 및 토큰 재발급 로직 구현.

### 👤 Member 도메인

- **회원 관리**: 회원 가입, 탈퇴, 정보 관리 기능.
- **중복 확인**: 이메일 및 닉네임 중복 확인을 통해 데이터 무결성 유지.

### 🛒 Cart 도메인

- **장바구니 기능**: 상품 추가, 조회, 삭제 등 장바구니 관련 기능 제공.

### 📦 Product 도메인

- **상품 관리**: 상품 등록, 단건 조회, 목록 조회 기능.
- **정렬 기능**: 다양한 조건(등록순 등)으로 상품 목록 정렬.

### 💳 Payment 도메인

- **결제 내역 관리**: 사용자의 결제 내역 조회 기능.

### 🗓️ 렌탈 도메인

- **대여 주문 시스템**: 상품 직접 선택 또는 장바구니를 통한 대여 주문 기능.
- **주문/결제 통합**: 주문과 동시에 결제가 이루어지는 시스템.
- **대여 내역 조회**: 사용자의 대여 주문 목록 및 상세 내역 조회.

### 🚚 Shipping 도메인

- **배송 추적**: 대여 건에 대한 운송장 번호 및 발송일 조회 기능.

## 📌 주요 엔드포인트

```http
# Auth
POST /api/v1/auth/logout
POST /api/v1/auth/reissue
POST /test/token/{providerId} - 로컬 전용 테스트 컨트롤러

# Member
POST /api/v1/members/verify/email
POST /api/v1/members/verify/nickname
DELETE /api/v1/members

# Product
POST /api/v1/product
GET /api/v1/product/{productId}
GET /api/v1/products

# Cart
POST /api/v1/cart
DELETE /api/v1/cart
GET /api/v1/cart

# Rental
POST /api/v1/rentals
GET /api/v1/rentals/{rentalId}
GET /api/v1/rentals
GET /api/v1/rentals/count
GET /api/v1/rentals/{rentalId}/payment

# Payment
GET /api/v1/payments/{paymentId}

# Shipping
GET /api/v1/shipping/{rentalId}
```
