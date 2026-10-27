# 책도장 API

책도장 독서 SNS의 백엔드 API 서버입니다. 프론트엔드는 [chaekdojang-web](https://github.com/hyesug/chaekdojang-frontend)에 있습니다.

| 환경 | API 주소 |
| --- | --- |
| 운영 | https://api.chaekdojang.com |
| 스테이징 | https://staging-api.chaekdojang.com (2026-10-14부터 EC2 중지 상태) |

API 주소의 루트(`/`)는 인증이 필요해 `401`을 돌려줍니다. 서버 상태는 `/actuator/health`로 확인합니다.

## 주요 기능

- 카카오·네이버·구글 로그인, JWT 인증
- 독후감 작성·공개 범위·예약 공개, 피드, 좋아요·댓글·북마크
- AI 독서카드, 도장 코멘트(독후감 피드백), 다시 읽기 질문 (OpenAI)
- 책 검색(카카오·구글), 웹소설 정보(네이버·카카오·리디), 구매 링크
- 서재, 독서 목표, 독서 통계
- 독서모임
- 도장단(서평단, 베타): 모집·신청·선정, 워터마크를 넣은 PDF 열람
- 독후감 공모전, 공식 프로필
- 운세 AI 질문권 지급·차감·환불과 사용 기록 (AI 답변 생성은 프론트엔드가 Claude로 처리)
- 알림, 고객 문의, 이미지 업로드
- 관리자: 회원·독후감 관리, 감사 로그, 운영 지표

**미공개 기능**: 책별 채팅(WebSocket), 독서 리캡 구독, 프리미엄 구독, 번역(DeepL). API는 운영 서버에 배포되어 있지만 웹 메뉴에는 열지 않았습니다.

## 기술 스택

Java 21 · Spring Boot 3.5 · Spring Security(OAuth2, JWT) · Spring Data JPA · PostgreSQL 17 · Flyway · Redis · WebSocket(STOMP) · AWS S3 · Docker

## 시작하기

필요한 것: JDK 21, Docker

### 1. PostgreSQL·Redis 실행

`.env.example`을 복사해 `DB_USERNAME`, `DB_PASSWORD`를 채운 뒤 실행합니다.

```bash
cp .env.example .env
docker compose up -d db redis
```

### 2. 로컬 설정 파일 만들기

`api/src/main/resources/application-local.yaml`을 만듭니다. 이 파일은 Git에 올라가지 않고, 서버가 시작할 때 자동으로 읽습니다.

OAuth `client-id`가 비어 있으면 서버가 시작되지 않습니다. 소셜 로그인을 쓰지 않는다면 아무 값이나 넣습니다.

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/chaekdojang
    username: chaekdojang      # .env의 DB_USERNAME
    password: your-password    # .env의 DB_PASSWORD
  security:
    oauth2:
      client:
        registration:
          kakao:
            client-id: local
            client-secret: local
          naver:
            client-id: local
            client-secret: local
          google:
            client-id: local
            client-secret: local
```

### 3. 서버 실행

```bash
cd api
./gradlew bootRun
```

```bash
curl http://localhost:8080/actuator/health
```

로컬에서는 Flyway를 쓰지 않고 Hibernate가 스키마를 자동으로 만듭니다(`ddl-auto: update`).

### 개발 편의 옵션

| 환경 변수 | 설명 |
| --- | --- |
| `DEV_LOGIN_ENABLED=true` | `POST /api/dev/login`으로 개발용 최고관리자 계정에 로그인 |
| `SWAGGER_UI_ENABLED=true`, `API_DOCS_ENABLED=true`, `SWAGGER_PUBLIC_ENABLED=true` | 세 개를 모두 켜면 http://localhost:8080/swagger-ui/index.html 에서 API 문서 확인 |

운영·스테이징에서는 둘 다 켜지 않습니다.

## 환경 변수

주요 값만 적었습니다. 전체 목록과 기본값은 `api/src/main/resources/application.yaml`에 있습니다.

| 변수 | 설명 |
| --- | --- |
| `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` | PostgreSQL 접속 정보 |
| `JWT_SECRET` | 32자 이상 임의 문자열 |
| `FRONTEND_URL` | 웹 주소. OAuth 리다이렉트 URI와 로그인 후 이동 주소에 쓰임 |
| `BACKEND_URL` | API 주소. 로컬에 저장한 업로드 파일의 URL에 쓰임 |
| `CORS_ALLOWED_ORIGINS` | CORS 허용 주소 |
| `KAKAO_CLIENT_ID`, `KAKAO_CLIENT_SECRET`, `NAVER_CLIENT_ID`, `NAVER_CLIENT_SECRET`, `GOOGLE_OAUTH_CLIENT_ID`, `GOOGLE_OAUTH_CLIENT_SECRET` | 소셜 로그인 |
| `KAKAO_API_KEY`, `GOOGLE_API_KEY` | 책 검색 |
| `REDIS_HOST`, `REDIS_PORT` | Redis (기본 `localhost:6379`) |
| `STORAGE_TYPE`, `S3_UPLOAD_BUCKET`, `S3_PUBLIC_BASE_URL`, `S3_EBOOK_BUCKET` | 파일 저장 위치 (`local` 또는 `s3`) |
| `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD` | 알림 메일 발신 계정 |
| `OPENAI_API_KEY` | AI 독서카드·도장 코멘트 |

운영 서버는 EC2의 `.env.production`, 스테이징은 `.env.staging`을 씁니다. 형식은 `.env.example`, `.env.staging.example`을 참고합니다.

## 테스트

```bash
cd api
./gradlew test
```

H2 인메모리 DB로 돌아가므로 PostgreSQL 없이 실행됩니다.

## 배포

| 환경 | 방법 |
| --- | --- |
| 운영 | `main`에 push하면 GitHub Actions `Deploy API to EC2`가 자동 실행 |
| 스테이징 | `staging` 브랜치에 push한 뒤 `Deploy API to Staging EC2`를 수동 실행 |

배포 순서: 테스트 → Docker 이미지 빌드(GHCR) → AWS SSM으로 EC2 배포 → DB 백업 → 컨테이너 교체 → 헬스 체크. 실패하면 이전 이미지로 되돌립니다. 컨테이너를 교체하는 동안 API가 잠시 멈춥니다.

### DB 마이그레이션

- 운영·스테이징은 Flyway로만 스키마를 바꿉니다(`ddl-auto: validate`). 마이그레이션 파일은 `api/src/main/resources/db/migration`에 있습니다.
- 마이그레이션이 있는 변경을 `main`에 올리기 전에 운영 DB 테이블 owner와 Flyway 계정 권한을 확인합니다. 스테이징을 통과해도 운영에서 실패한 적이 있습니다(2026-06-24).

## 프로젝트 구조

```text
api/                                  Spring Boot 애플리케이션 (Gradle)
├─ src/main/java/com/chaekdojang/api/
│  ├─ domain/                         기능별 코드
│  ├─ global/                         공통 응답·예외·보안
│  ├─ infra/                          외부 API 연동
│  └─ config/                         설정
└─ src/main/resources/db/migration/   Flyway 마이그레이션
docker-compose.yml                    로컬 PostgreSQL·Redis
docker-compose.staging.yml            스테이징 서버
docker-compose.prod.yml               운영 서버
nginx/  scripts/  ops/                서버 설정, 운영 스크립트
```

## 관련 문서

- [운영 보안·비용 점검표](docs/ops-hardening-checklist.md): 백업, 비용 알림, 보안 그룹, 마이그레이션 사전 점검, S3, 모니터링
