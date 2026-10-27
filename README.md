# 책도장 API

책도장(독서 SNS)의 백엔드 API 서버입니다. 독후감·서재·독서모임·서평단(도장단)·공모전과 운세 AI 질문권, 관리자 기능을 제공합니다.

- 운영: https://api.chaekdojang.com
- 스테이징: https://staging-api.chaekdojang.com
- 프론트엔드: [hyesug/chaekdojang-frontend](https://github.com/hyesug/chaekdojang-frontend)

## 목차

- [주요 기능](#주요-기능)
- [기술 스택](#기술-스택)
- [시작하기](#시작하기)
- [환경 변수](#환경-변수)
- [테스트](#테스트)
- [데이터베이스](#데이터베이스)
- [프로젝트 구조](#프로젝트-구조)
- [배포](#배포)
- [개발 규칙](#개발-규칙)
- [관련 문서](#관련-문서)

## 주요 기능

### 서비스 중

| 영역 | 내용 | 주요 경로 |
| --- | --- | --- |
| 회원 | 카카오·네이버·구글 OAuth 로그인, JWT(액세스·리프레시 토큰), 온보딩, 팔로우 | `/api/auth`, `/api/users` |
| 독후감 | 작성·수정·삭제, 공개 범위, 예약 공개, 피드, 좋아요·댓글·북마크 | `/api/reviews` |
| AI 독후감 도우미 | AI 독서카드(독후감 요약), 도장 코멘트(독후감 피드백), 다시 읽기 질문·변화 비교 | `/api/reviews/{id}/reflection`, `/api/feedback` |
| 책 | 카카오·구글 도서 검색, 웹소설 정보(네이버·카카오·리디), 책 상세·테마, 구매 링크 | `/api/books` |
| 서재·기록 | 내 서재, 독서 목표, 독서 캘린더·통계 | `/api/library` |
| 독서모임 | 모임 생성·가입·초대, 함께 읽는 책, 질문·답변, AI 모임 분석 | `/api/groups` |
| 도장단 (베타) | 서평단 모집·신청·선정, 전자책 PDF 열람(워터마크), 독후감 활용 동의 | `/api/dojangdan` |
| 공모전 | 독후감 공모전 응모, 주최자 관리 | `/api/contests` |
| 공식 프로필 | 출판사·도서관 등 공식 프로필 신청·운영 | `/api/profiles`, `/api/profile-applications` |
| 운세 AI 질문권 | 질문권 지급(독후감 작성 보상)·차감·환불, AI 사용량·원가 기록, 답변 피드백 | `/api/fortune-ai`, `/api/fortune-profiles` |
| 알림·문의 | 알림, 고객 문의, 이미지 업로드(로컬 또는 S3) | `/api/notifications`, `/api/inquiries`, `/api/upload` |
| 관리자 | 회원·독후감 관리, 감사 로그, 접속·에러 로그, 운영 지표, 질문권·운세 운영, 로또 미래검증 원장 조회 | `/api/admin` |

> 운세 AI 답변 생성(Claude 호출)은 프론트엔드의 `app/fortune-ai/route.ts`가 맡고, 이 서버는 질문권 정산과 사용 기록을 맡습니다.
> 질문권 유료 판매(PortOne 결제)는 `AI_CREDIT_SALES_ENABLED`로 켜고 끄며 기본값은 꺼짐입니다.

### 미공개 기능

코드는 있지만 서비스 화면에 열지 않은 기능입니다. 공개 전에는 프론트엔드 연결과 운영 점검이 더 필요합니다.

| 기능 | 경로 | 상태 |
| --- | --- | --- |
| 책별 채팅방 (WebSocket/STOMP) | `/api/chat`, `/ws` | 프론트 진입 링크 없음 |
| 독서 리캡 구독 | `/api/reading-recaps` | 프론트 화면만 있고 메뉴 링크 없음 |
| 프리미엄 구독 | `/api/subscriptions` | 프론트 화면 없음 |
| 번역 (DeepL) | `/api/translate` | 프론트 화면 없음 |

## 기술 스택

| 구분 | 기술 |
| --- | --- |
| 언어·프레임워크 | Java 21, Spring Boot 3.5 |
| 인증 | Spring Security, OAuth2 Client, JWT (jjwt) |
| 데이터 | Spring Data JPA, PostgreSQL 17, Flyway, Redis |
| 실시간 | Spring WebSocket (STOMP) |
| 외부 연동 | WebClient(WebFlux), AWS S3, Spring Mail(Gmail SMTP), OpenAI, PortOne |
| 문서 처리 | Apache PDFBox (서평단 PDF 워터마크) |
| API 문서 | springdoc-openapi (Swagger UI) |
| 인프라 | Docker, GHCR, AWS EC2·RDS, Nginx, GitHub Actions |

## 시작하기

### 요구 사항

- JDK 21
- Docker (로컬 PostgreSQL·Redis 실행용)

### 1. PostgreSQL·Redis 실행

```bash
cp .env.example .env
docker compose up -d db redis
```

`.env`의 `DB_USERNAME`, `DB_PASSWORD`를 먼저 채웁니다.

### 2. 로컬 설정 파일 만들기

`api/src/main/resources/application-local.yaml`을 만들면 `application.yaml`이 자동으로 읽습니다. 이 파일은 Git에 올라가지 않습니다.

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/chaekdojang
    username: chaekdojang
    password: your-password
```

같은 값을 환경 변수(`SPRING_DATASOURCE_URL` 등)로 넣어도 됩니다.

### 3. 서버 실행

```bash
cd api
./gradlew bootRun
```

Windows에서는 `gradlew.bat bootRun`을 씁니다. 실행 후 상태를 확인합니다.

```bash
curl http://localhost:8080/actuator/health
```

### Swagger UI

기본으로 꺼져 있습니다. `SWAGGER_UI_ENABLED=true`, `API_DOCS_ENABLED=true`로 실행하면 http://localhost:8080/swagger-ui/index.html 에서 볼 수 있습니다.

### 로컬 개발 로그인

`DEV_LOGIN_ENABLED=true`로 실행하면 OAuth 없이 `/api/dev/login`으로 개발용 계정에 로그인할 수 있습니다. 운영·스테이징에서는 켜지 않습니다.

## 환경 변수

자주 쓰는 값만 정리했습니다. 전체 목록과 기본값은 `api/src/main/resources/application.yaml`에 있습니다. 실제 키와 비밀번호는 커밋하지 않습니다.

| 구분 | 변수 | 설명 |
| --- | --- | --- |
| DB | `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` | PostgreSQL 접속 정보 |
| 인증 | `JWT_SECRET` | 32자 이상 임의 문자열 (운영 필수) |
| URL | `FRONTEND_URL`, `BACKEND_URL`, `CORS_ALLOWED_ORIGINS` | OAuth 리다이렉트와 CORS 허용 주소 |
| OAuth | `KAKAO_CLIENT_ID`, `KAKAO_CLIENT_SECRET`, `NAVER_CLIENT_ID`, `NAVER_CLIENT_SECRET`, `GOOGLE_OAUTH_CLIENT_ID`, `GOOGLE_OAUTH_CLIENT_SECRET` | 소셜 로그인 |
| 책 검색 | `KAKAO_API_KEY`, `GOOGLE_API_KEY` | 카카오·구글 도서 검색 |
| Redis | `REDIS_HOST`, `REDIS_PORT` | 기본 `localhost:6379` |
| 파일 저장 | `STORAGE_TYPE`, `S3_UPLOAD_BUCKET`, `S3_PUBLIC_BASE_URL`, `S3_EBOOK_BUCKET`, `AWS_REGION` | `STORAGE_TYPE`은 `local` 또는 `s3` |
| 메일 | `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD`, `NOTIFICATION_ADMIN_EMAIL` | Docker compose에서는 `MAIL_USERNAME`, `MAIL_PASSWORD`로 넘깁니다 |
| AI | `OPENAI_API_KEY`, `OPENAI_REVIEW_SUMMARY_MODEL`, `AI_SUMMARY_ENABLED`, `FEEDBACK_ENABLED`, `BOOK_THEME_ENABLED` | AI 독서카드·도장 코멘트·책 테마 |
| 질문권 결제 | `AI_CREDIT_SALES_ENABLED`, `PORTONE_API_SECRET` | 질문권 유료 판매 (기본 꺼짐) |
| 로또 미래검증 | `LOTTO_FUTURE_GENERATOR_URL`, `LOTTO_FUTURE_GENERATOR_TOKEN`, `LOTTO_FUTURE_LEADERBOARD_URL` | 관리자용 예측 생성 연동 |
| 개발·보안 | `DEV_LOGIN_ENABLED`, `SWAGGER_UI_ENABLED`, `API_DOCS_ENABLED`, `RATE_LIMIT_ENABLED`, `APP_SECURITY_ENFORCE_PRODUCTION_SECRETS` | 개발 편의·요청 제한·운영 비밀값 검사 |

환경별 예시는 `.env.example`(로컬·운영 compose)과 `.env.staging.example`(스테이징)을 참고합니다.

## 테스트

```bash
cd api
./gradlew test
```

테스트는 `application-test.yaml` 설정과 H2 인메모리 DB로 실행됩니다.

## 데이터베이스

- 마이그레이션 위치: `api/src/main/resources/db/migration`
- 파일 이름: `V{yyyyMMdd}{순번 2자리}__{설명}.sql` (예: `V2026101501__lotto_future_leaderboard.sql`)
- DB 구조를 바꿀 때는 엔티티와 Flyway 마이그레이션을 함께 추가합니다.

| 환경 | Flyway | `ddl-auto` |
| --- | --- | --- |
| 로컬 (기본값) | 꺼짐 | `update` |
| 스테이징·운영 | 켜짐 | `validate` |

운영·스테이징은 Hibernate가 스키마를 바꾸지 않으므로, 마이그레이션이 빠지면 서버가 뜨지 않습니다. 마이그레이션이 있는 변경을 `main`에 올리기 전에는 운영 DB의 Flyway 계정 권한과 테이블 owner를 확인합니다.

## 프로젝트 구조

```text
chaekdojang-backend/
├─ api/                          # Spring Boot 애플리케이션 (Gradle 루트)
│  ├─ src/main/java/com/chaekdojang/api/
│  │  ├─ config/                 # 보안, WebSocket, Swagger, WebClient, 비동기 설정
│  │  ├─ domain/                 # 기능별 패키지
│  │  │  ├─ user/  review/  book/  library/  readinggoal/
│  │  │  ├─ readinggroup/  dojangdan/  contest/  officialprofile/
│  │  │  ├─ fortune/  lotto/  notification/  inquiry/  upload/
│  │  │  ├─ admin/  metrics/  accesslog/  errorlog/  logretention/
│  │  │  └─ chat/  recap/  subscription/      # 미공개 기능
│  │  ├─ global/                 # 공통 응답(ApiResponse), 예외(CustomException·ErrorCode), 보안 필터
│  │  └─ infra/                  # 외부 API 클라이언트 (kakao, google, naver, ridi, wikidata, mail, translation)
│  ├─ src/main/resources/
│  │  ├─ application.yaml
│  │  ├─ book-themes/            # 책 테마 기본 데이터
│  │  └─ db/migration/           # Flyway 마이그레이션
│  ├─ src/test/
│  ├─ Dockerfile
│  └─ build.gradle.kts
├─ docker-compose.yml            # 로컬: PostgreSQL, Redis, 앱
├─ docker-compose.staging.yml    # 스테이징 EC2
├─ docker-compose.prod.yml       # 운영 EC2 (DB는 RDS)
├─ nginx/                        # 리버스 프록시 설정
├─ scripts/                      # EC2 설정, DB 백업, 스모크 테스트, 보안 그룹·비용 점검
├─ ops/                          # CloudWatch 에이전트 설정
├─ docs/                         # 운영 점검표, 설계 문서
└─ .github/workflows/            # 배포, 운영 모니터링
```

## 배포

GitHub Actions가 Docker 이미지를 빌드해 GHCR에 올리고, AWS SSM으로 EC2에서 새 이미지를 받아 실행합니다.

| 환경 | 방법 | 주소 |
| --- | --- | --- |
| 운영 | `main` 브랜치에 push하면 `Deploy API to EC2`가 자동 실행 | https://api.chaekdojang.com |
| 스테이징 | `staging` 브랜치에 push한 뒤 `Deploy API to Staging EC2`를 수동 실행 | https://staging-api.chaekdojang.com |

운영 배포 순서는 테스트 → 이미지 빌드 → DB 백업 → 컨테이너 교체 → 헬스 체크·스모크 테스트이며, 실패하면 이전 이미지로 되돌립니다. 배포 중에는 서버가 잠시 내려갑니다.

`Monitor Production` workflow가 매시 정각에 운영 API와 웹을 점검합니다.

## 개발 규칙

- 응답은 `ApiResponse<T>`, 예외는 `CustomException`·`ErrorCode`, DTO는 `record`를 씁니다.
- DB 변경은 마이그레이션으로만 하고, soft delete·호환성·권한을 함께 확인합니다.
- API 계약을 바꾸면 프론트엔드 호출부도 함께 확인합니다.
- 커밋 메시지는 `type(scope): 한국어 설명` 형식을 따릅니다. 예: `fix(book): 도서 검색 쉼표·저자 검색 보정`

자세한 작업 지침은 [AGENTS.md](AGENTS.md)에 있습니다.

## 관련 문서

- [운영 보안·비용 점검표](docs/ops-hardening-checklist.md)
- [유료 운세 AI 점검](docs/compliance/2026-10-01-paid-fortune-ai-audit.md)
- [AI 질문권 정합성 점검 SQL](docs/ops/ai-credit-integrity.sql)
- [도장 코멘트 지표 SQL](docs/feedback-metrics.sql)
