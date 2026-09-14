# Daejeoniac API Server

> 대전 관광 데이터를 기반으로 장소 탐색, 방문 인증, 영수증 OCR, 리워드 뽑기, AI 추천을 제공하는 Spring Boot 백엔드 서버

## Overview

**Daejeoniac**은 대전의 관광지, 음식점, 쇼핑 장소, 방문자 수, 혼잡도 데이터를 활용해 사용자가 대전을 더 쉽게 탐색하고 방문하도록 돕는 서비스입니다.

이 저장소는 Daejeoniac 서비스의 백엔드 API 서버입니다. 한국관광공사 및 대전 관광 데이터, 카카오 장소 검색, OpenAI 기반 추정 데이터, 별도 AI 추천 서버를 연동해 장소 탐색부터 인증, 리워드, 개인화 추천까지 처리합니다.

## Key Features

### 장소 탐색

- 대전 장소 목록 조회
- 장소 상세 조회
- DB 기반 장소 검색
- 특정 장소 기준 1km 반경 주변 장소 조회
- 현재 위치 기준 방문자 수 상위 장소 조회
- 장소 클릭 로그 및 일별 조회 수 집계

### 관광 데이터 동기화

- 관광지, 음식점, 쇼핑 장소 데이터 수집
- 지역 방문자 수 데이터 동기화
- 장소별 방문자 수 저장
- 장소별 혼잡도 저장
- 매일 새벽 관광 데이터 자동 동기화

### AI 추천

- 선택한 장소와 유사한 장소 추천
- 현재 장소와 방문 이력을 기반으로 다음 장소 추천
- 자연어 검색 기반 장소 추천
- AI 서버에 장소, 혼잡도, 방문자 수, 태그 정보를 전달해 추천 결과 수신

### 방문 인증 및 영수증

- 사용자 위치 기반 방문 인증
- 방문 장소별 영수증 업로드 URL 발급
- S3 기반 영수증 이미지 저장
- OCR 처리 요청 및 결과 저장
- 승인된 영수증 기반 리워드 뽑기

### 리워드

- 관리자 리워드 상품 등록 및 수정
- 승인된 영수증으로 리워드 뽑기
- 뽑기 로그 저장
- 당첨 포인트를 회원 포인트에 반영

### 인증

- 관리자 회원가입 및 로그인
- Kakao OAuth 로그인
- Apple Sign in with Apple 네이티브 로그인 흐름 지원
- JWT access token, refresh token 발급
- 사용자 정보 조회, 수정, 탈퇴

## Tech Stack

| Area | Stack |
| --- | --- |
| Language | Java 21 |
| Framework | Spring Boot 4.0.7 |
| Web | Spring Web MVC, WebFlux WebClient |
| Persistence | Spring Data JPA, Hibernate |
| Database | MySQL |
| Cache / Token Store | Redis |
| Security | Spring Security, JWT |
| Storage | AWS S3 |
| API Docs | Springdoc OpenAPI / Swagger UI |
| External APIs | 한국관광공사 OpenAPI, 대전 관광 API, Kakao API, OpenAI API, Apple API |
| Build | Gradle |
| Deploy | Docker, AWS ECR/EC2/RDS/S3 |

## System Architecture

```text
           ┌──────────────┐     ┌──────────────────┐
           │ GitHub main  │ ──▶ │  GitHub Actions  │
           └──────────────┘     └─────────┬────────┘
                                          ┼─────────────────────────────────────────────┐
                                          │                                             │
                                 Docker Build & Push                              SSM Run Command
                                          │                                             │
                                          ▼                                             │
┌──────────────┐    ╔════════════ AWS Cloud (ap-northeast-2) ═══════════════════╗       │
│    사용자     │    ║                                                           ║       │
│  RN WebView  │    ║            ┌─────────────────┐                            ║       │
└──────┬───────┘    ║            │   Amazon ECR    │                            ║       │
       │            ║            │ Docker Registry │                            ║       │
       │ HTTPS/JWT  ║            └────────┬────────┘                            ║       │
       │            ║                     │ Image Pull                          ║       │
       │            ║                     ▼                                     ║       │
       │            ║       ┌──────────── EC2 ──────────────┐                   ║       │
       └────────────╫───────┼──▶┌───────────────────────┐   │◀──── SSM ─────────╫───────┘
                    ║       │   │ Nginx + Certbot       │   │                   ║
                    ║       │   │ Reverse Proxy / TLS   │   │                   ║
                    ║       │   └───────────┬───────────┘   │                   ║
                    ║       │               │               │                   ║
                    ║       │               ▼               │                   ║
                    ║       │   ┌───────────────────────┐   │                   ║
                    ║       │   │ Spring Boot API Server│   │                   ║
                    ║       │   │ Docker Container      │   │                   ║
                    ║       │   └─────┬──────────┬──────┘   │                   ║
                    ║       └─────────┼──────────┼──────────┘                   ║
                    ║                 │          │                              ║
                    ║                 ▼          ▼                              ║
                    ║          ┌──────────┐ ┌──────────────────┐                ║
                    ║          │   RDS    │ │   ElastiCache    │                ║
                    ║          │  MySQL   │ │ Valkey / Redis   │                ║
                    ║          └──────────┘ │ 추천 세션 TTL 24h│                  ║
                    ║                       └──────────────────┘                ║
                    ║               ┌─────────────────┐                         ║
                    ║               │    Amazon S3    │◀ ─ ─ Client             ║
                    ║               │ 영수증/장소 사진   │  Presigned URL Upload   ║
                    ║               └─────────────────┘                         ║
                    ╚═══════════════════════════════════════════════════════════╝
                                             │
                          ┌──────────────────┼─────────────────┐
                          ▼                  ▼                 ▼
                   ┌─────────────┐    ┌─────────────┐   ┌──────────────┐
                   │  AI Server  │    │  Kakao API  │   │ Apple Login  │
                   │ 추천 / OCR   │    │ 주소 → 좌표  │   │ ID Token 검증 │
                   └─────────────┘    └─────────────┘   └──────────────┘
```

## Project Structure

```text
src/main/java/com/daejeongwang/uoscrazydaejeon
├── client        # 외부 API 클라이언트
├── config        # Security, Swagger, S3, Redis, Scheduler 설정
├── controller    # 사용자/관리자 API 컨트롤러
├── dto           # 요청/응답 DTO
├── entity        # JPA 엔티티
├── exception     # 전역 예외 처리 및 커스텀 예외
├── repository    # Spring Data JPA Repository
├── security      # JWT Provider, 인증 필터
├── service       # 비즈니스 로직
└── util          # OAuth, 거리 계산 등 유틸
```

## Main API Groups

| Group | Description |
| --- | --- |
| `/api/v1/auth/**` | Kakao/Apple 로그인, 토큰 갱신 |
| `/api/v1/admin/auth/**` | 관리자 회원가입, 로그인 |
| `/api/v1/member/**` | 내 정보 조회, 수정, 탈퇴 |
| `/api/v1/places/**` | 장소 조회, 검색, 주변 장소, 방문 인증, 클릭 로그 |
| `/api/v1/receipts/**` | 영수증 업로드, OCR 처리, 상태 조회 |
| `/api/v1/reward/**` | 리워드 뽑기, 뽑기 기록 |
| `/api/v1/recommendations/**` | AI 기반 장소 추천 |
| `/api/v1/visitor-count/**` | 장소별 방문자 수 조회 |
| `/api/v1/regional-visitor-count/**` | 지역 방문자 수 조회 |
| `/api/v1/admin/sync/**` | 관리자 데이터 동기화 |
| `/api/v1/admin/rewards/**` | 관리자 리워드 상품 관리 |

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

## Getting Started

### Requirements

- Java 21
- MySQL 8.x
- Redis
- Gradle Wrapper
- AWS S3 bucket
- Kakao Developers app
- Apple Developer app configuration
- OpenAI API key

### Environment Variables

운영 환경에서는 민감 정보가 코드나 `application.properties`에 직접 들어가지 않도록 환경변수로 주입합니다.

```env
DB_URL=
DB_USERNAME=
DB_PASSWORD=

REDIS_HOST=
REDIS_PORT=

JWT_SECRET=

DAEJEON_API_SERVICE_KEY=
KAKAO_REST_KEY=
KAKAO_CLIENT_ID=
KAKAO_CLIENT_SECRET=

OPEN_API_SERVICE_KEY=
OPENAI_API_KEY=

APPLE_TEAM_ID=
APPLE_KEY_ID=
PRIVATE_KEY_PATH=

AWS_ACCESS_KEY_ID=
AWS_SECRET_ACCESS_KEY=
AWS_REGION=
AWS_S3_BUCKET=

AI_SERVER_URL=
```

> 실제 키, DB 비밀번호, Apple `.p8` 키 파일은 저장소에 커밋하지 않습니다.

### Run Locally

로컬 프로필을 사용하는 경우:

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

기본 실행:

```bash
./gradlew bootRun
```

빌드:

```bash
./gradlew clean build
```

컴파일 확인:

```bash
./gradlew compileJava
```

## Docker

JAR 빌드:

```bash
./gradlew clean bootJar
```

Docker 이미지 빌드:

```bash
docker build -t daejeoniac-api .
```

Docker 실행:

```bash
docker run -p 8080:8080 \
  --env-file .env \
  daejeoniac-api
```

## Data Sync

관리자 API를 통해 관광 데이터를 수동 동기화할 수 있습니다.

| Endpoint | Description |
| --- | --- |
| `POST /api/v1/admin/sync/places` | 관광지, 쇼핑, 음식점 전체 동기화 |
| `POST /api/v1/admin/sync/congestion` | 공공 데이터 기반 혼잡도 동기화 |
| `POST /api/v1/admin/sync/congestion/forecast` | LLM 기반 혼잡도 생성 작업 시작 |
| `POST /api/v1/admin/sync/visitor-count` | LLM 기반 방문자 수 생성 |
| `POST /api/v1/admin/sync/regional-visitor-count/latest` | 최신 지역 방문자 수 동기화 |

스케줄러는 매일 새벽 관광 데이터와 예측 데이터를 자동 갱신합니다.

## Authentication

### JWT

로그인 성공 시 서버는 다음 토큰을 발급합니다.

- `accessToken`: API 인증용 토큰
- `refreshToken`: access token 재발급용 토큰
- `tokenType`: `Bearer`

인증이 필요한 API는 다음 헤더를 사용합니다.

```http
Authorization: Bearer {accessToken}
```

### Apple Login

iOS 클라이언트는 `expo-apple-authentication` 또는 Apple `AuthenticationServices`를 통해 Apple Identity Token을 발급받고, 백엔드는 해당 토큰을 검증한 뒤 서비스 자체 JWT를 발급합니다.

### Kakao Login

Kakao OAuth authorization code를 서버에 전달하면 서버가 Kakao token/profile API를 호출하고, 회원을 생성하거나 기존 회원을 조회한 뒤 서비스 자체 JWT를 발급합니다.

## Recommendation Flow

### Similar Places

```text
placeId
  -> DB에서 현재 장소 조회
  -> 현재 장소 기준 1km 주변 장소 조회
  -> AI 서버에 selected_place + nearby_places 전달
  -> 유사 장소 목록 반환
```

### Next Places

```text
memberId + current placeId
  -> 사용자의 방문 장소 조회
  -> 현재 장소 기준 1km 주변 장소 조회
  -> AI 서버에 current_place + visited_places + nearby_places 전달
  -> 다음 방문 추천 장소 반환
```

### Natural Search

```text
placeId + query + top_k
  -> 현재 장소 기준 1km 주변 장소 조회
  -> places + query + top_k를 AI 서버에 전달
  -> 자연어 조건에 맞는 장소 추천
```

## Notes

- 관리자 API는 `ADMIN` 권한이 필요합니다.
- 장소 상세 조회 시 클릭 로그가 저장되고 당일 조회 수가 응답에 포함됩니다.
- LLM 기반 혼잡도 생성은 시간이 오래 걸릴 수 있어 비동기 작업으로 처리합니다.
- 영수증 이미지는 S3에 저장되며, OCR 결과를 기반으로 승인 상태를 관리합니다.
- 승인된 영수증은 리워드 뽑기 기회로 사용됩니다.

## Commit Convention

권장 커밋 메시지 형식:

```text
feat: 새로운 기능 추가
fix: 버그 수정
refactor: 구조 개선
docs: 문서 수정
chore: 설정/빌드 작업
```

예시:

```text
feat: DB 기반 장소 검색 API 추가
fix: Apple 로그인 예외 응답 처리
docs: README 프로젝트 문서화
```
