# 책도장 — CLAUDE.md

## 프로젝트 개요
독서 전용 SNS. 독후감 피드 + 팔로우 + 책 단위 채팅 + 도서 커머스.
현재 단계: MVP (피드·팔로우·구매 연동. 채팅은 v1.0)

## 기술 스택
- Backend: Java 21, Spring Boot 3.x, Gradle (Kotlin DSL)
- DB: PostgreSQL 17
- ORM: Spring Data JPA + Hibernate
- 인증: Spring Security + JWT
- 외부 API: 카카오 책 API, Google Books API
- 인프라: AWS EC2 + RDS / Vercel (프론트)

## 로컬 환경
- OS: Windows / JDK: Temurin 21 / IDE: IntelliJ IDEA Community
- DB: PostgreSQL 17 (로컬 직접 설치)
- Docker Desktop 설치됨

## 자주 쓰는 명령어
```bash
./gradlew build    # 빌드
./gradlew bootRun  # 실행
./gradlew test     # 테스트
```

## 패키지 구조
```
com.chaekdojang
├── domain
│   ├── user          # 사용자·팔로우
│   ├── review        # 독후감 피드
│   ├── book          # 책 데이터·태그
│   ├── library       # 내 서재
│   └── commerce      # 구매 링크·커미션
├── global
│   ├── config        # Security, JPA, Web 설정
│   ├── exception     # 공통 예외처리
│   └── response      # 공통 응답 형식
└── infra
    ├── kakao         # 카카오 책 API 클라이언트
    └── google        # Google Books API 클라이언트
```

## 코드 컨벤션
- 응답: `ApiResponse<T>` 래퍼 통일
- 예외: `CustomException` + `ErrorCode` enum
- DTO: record 사용 (Java 21)
- 엔티티: Lombok `@Builder`, `@NoArgsConstructor(access = PROTECTED)`
- 브랜치: `feat/기능명`, `fix/버그명`

## DB 스키마 주요 원칙
- 글로벌 확장 대비: users에 language, country 컬럼 포함
- books에 isbn13, source(kakao/google_books) 컬럼 포함
- soft delete 기본 적용 (deleted_at timestamp)

## MVP 범위 (이것만)
- 독후감 CRUD + 피드
- 팔로우/팔로워
- 좋아요·댓글
- 책 검색·태그 (카카오 + Google Books)
- 도서 구매 링크 (쿠팡파트너스·교보)
- 내 서재 (읽는 중·완독·위시리스트)

## MVP 제외 (v1.0 이후)
- 채팅방 (WebSocket + Redis)
- 자동 번역 (DeepL)
- 프리미엄 구독
- Flutter 앱

## 코딩 원칙

### 먼저 생각하고 구현
- 불확실한 것이 있으면 가정하지 말고 먼저 질문한다
- 해석이 여러 가지면 조용히 하나를 고르지 말고 선택지를 제시한다
- 더 단순한 방법이 있으면 말한다

### 단순하게 유지
- 요청한 것만 만든다. 추측으로 메서드·클래스를 추가하지 않는다
- 한 번만 쓸 코드에 인터페이스·추상 클래스를 만들지 않는다
- "나중에 필요할 것 같아서" 미리 만드는 코드는 없다

### 최소한만 건드리기
- 요청과 관계없는 클래스·메서드·포맷을 건드리지 않는다
- 깨지지 않은 것은 리팩터하지 않는다
- 내 변경으로 생긴 불필요 import만 정리한다. 기존 dead code는 건드리지 않는다
- 수정한 모든 줄은 사용자의 요청으로 직접 추적 가능해야 한다

## 검증 규칙 — 반드시 준수

### 1단계: 빌드·테스트 (코드 작성 후 매번)
```bash
./gradlew build
./gradlew test
```
실패 시 에러 분석 → 수정 → 재실행 반복. 통과할 때까지 완료 선언 금지.

### 2단계: API 검증 (서버 실행 중일 때)
```bash
curl -s http://localhost:8080/actuator/health
curl -s http://localhost:8080/api/reviews | jq .
curl -s "http://localhost:8080/api/books/search?q=채식주의자" | jq .
```
예상과 다른 응답이면 원인 분석 후 수정.

### 작업 완료 보고 형식
```
✅ 빌드: PASSED / FAILED
✅ 테스트: X개 통과 / X개 실패
✅ API: [엔드포인트] → HTTP [상태코드] [결과 요약]
⚠️ 미해결: [있으면 명시, 없으면 생략]
```
이 형식 없이 완료 선언 금지.

## 작업 방식
- 코드나 설정 적용할 때마다 각 항목이 무엇인지, 왜 이렇게 설정하는지 초보자도 이해할 수 있게 하나씩 설명한다
- 작업 완료 후 다음 단계를 항상 제안한다
- 모르는 것 있으면 작업 전에 먼저 질문한다

## 커밋 규칙

### 메시지 형식
```
<type>(<scope>): <subject>
```

type: `feat` · `fix` · `refactor` · `test` · `docs` · `chore`
scope: `user` · `review` · `book` · `library` · `commerce` · `auth` · `config`

예시:
- `feat(review): 독후감 CRUD API 구현`
- `fix(book): 카카오 API 검색 결과 null 처리`

규칙: subject는 한국어, 동사로 끝내기 · 50자 이내 · 마침표 금지

### 커밋 타이밍
- 기능 단위로 커밋
- 빌드·테스트 통과 후에만 커밋
- 작업 완료 후 반드시 `git add → git commit → git push origin main` 순서 실행
- push까지 완료된 것을 확인한 후에만 작업 완료 선언
