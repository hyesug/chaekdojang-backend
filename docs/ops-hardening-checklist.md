# 운영 보안·비용 점검표

코드만으로 끝낼 수 없는 운영·스테이징 작업을 정리한 점검표입니다.

## DB 백업 (S3 저장)

준비된 코드:

- `scripts/backup-prod-db.sh`가 PostgreSQL custom 형식 덤프를 만듭니다.
- `BACKUP_S3_BUCKET`을 설정하면 덤프를 S3에도 복사합니다.
- `scripts/install-db-backup-cron.sh`가 매일 도는 cron 작업을 설치합니다.

AWS 작업:

- 비공개 S3 버킷 생성 완료: `chaekdojang-prod-backups-863518416212`
- 버킷 버전 관리를 켭니다.
- 기본 서버 측 암호화(SSE-S3 또는 SSE-KMS)를 켭니다.
- 퍼블릭 액세스를 모두 차단합니다.
- 수명 주기 규칙을 추가합니다.
  - 필요하면 오래된 백업을 더 싼 스토리지로 옮깁니다.
  - 정한 보관 기간이 지나면 백업을 삭제합니다.
- 백업 경로에만 쓸 수 있는 EC2 인스턴스 역할을 붙입니다.

EC2 `.env.production` 예시:

```text
BACKUP_S3_BUCKET=chaekdojang-prod-backups-863518416212
BACKUP_S3_PREFIX=db-backups/prod
RETENTION_DAYS=14
```

현재 EC2 상태:

- 매일 `03:17 KST`에 도는 백업 cron이 설치되어 있습니다.
- 테스트 백업을 S3에 올리는 데 성공했습니다.
- 최신 S3 덤프를 임시 DB에 복원하는 훈련을 마쳤고, 임시 DB는 삭제했습니다.

## AWS 비용 알림

준비된 코드:

```bash
cd ~/chaekdojang-api
MONTHLY_LIMIT_USD=20 ./scripts/create-monthly-budget-alert.sh your-email@example.com
```

AWS 작업:

- 받은편지함에서 AWS Budgets 이메일 구독을 확인합니다.
- `MONTHLY_LIMIT_USD`를 실제로 감당할 수 있는 금액으로 맞춥니다.
- 처음에는 아래 두 알림을 권장합니다.
  - 실제 비용이 80%를 넘을 때
  - 예상 비용이 100%를 넘을 때

현재 AWS 상태:

- 월 예산 `chaekdojang-monthly-cost`를 `20 USD`로 만들었습니다.
- 이메일 구독을 확인했습니다.
- SNS 이메일 테스트가 정상 도착했습니다.

## 보안 그룹 최종 점검

준비된 코드:

```bash
cd ~/chaekdojang-api
./scripts/audit-security-group.sh sg-xxxxxxxx
```

기대하는 인바운드 규칙:

- `80/tcp`: 공개. HTTP 리다이렉트와 인증서 갱신용
- `443/tcp`: 공개. HTTPS용
- `22/tcp`: 현재 관리자 IP만
- `8080/tcp`: 비공개
- `5432/tcp`: 비공개
- `6379/tcp`: 비공개

현재 AWS 상태:

- EC2 보안 그룹은 `80/tcp`, `443/tcp`만 공개합니다.
- SSH `22/tcp`는 현재 관리자 IP로만 제한했습니다.
- RDS는 `PubliclyAccessible=false`입니다.

## DB 인덱스 점검

준비된 코드:

- `V2__admin_audit_logs_and_operational_indexes.sql`이 Flyway로 `admin_audit_logs` 테이블과 인덱스를 만듭니다.
- `scripts/apply-operational-indexes.sql`에는 기존 운영 테이블용 인덱스가 들어 있습니다. 애플리케이션 DB 계정이 예전 테이블의 owner가 아닐 수 있으므로, 해당 테이블 owner 계정으로 실행합니다.

현재 운영 상태:

- Flyway V2가 정상 적용되었습니다.
- `scripts/apply-operational-indexes.sql`을 예전 테이블 owner 역할로 적용했습니다.

배포 후 확인:

```sql
SELECT version, description, success
FROM flyway_schema_history
ORDER BY installed_rank DESC
LIMIT 5;
```

실제 트래픽이 생긴 뒤 느린 쿼리 확인:

```sql
SELECT query, calls, mean_exec_time
FROM pg_stat_statements
ORDER BY mean_exec_time DESC
LIMIT 20;
```

두 번째 쿼리를 쓰려면 RDS에서 `pg_stat_statements`를 먼저 켜야 합니다.

## 운영 마이그레이션 사전 점검

백엔드 변경을 `main`에 push하기 전에 `api/src/main/resources/db/migration/` 아래 파일이 추가·변경되었는지 확인합니다. 바뀌었다면 스테이징 결과만 믿지 않습니다. 운영 테이블은 owner나 권한이 달라서, 스테이징은 통과해도 운영에서 실패할 수 있습니다.

필수 점검:

- 새 마이그레이션이 건드리는 기존 운영 테이블을 모두 찾습니다.
- `ALTER TABLE`, `CREATE INDEX`, `DROP`, `RENAME`, 제약 조건 변경이 있으면 배포 전에 테이블 owner를 확인합니다.
- 애플리케이션·Flyway DB 계정이 운영에서 그 마이그레이션을 실행할 수 있는지 확인합니다.
- 배포 전 평소대로 운영 DB 백업을 받습니다.
- 롤백 절차는 준비하되, 롤백으로 마이그레이션 권한 실패를 덮으려 하지 않습니다. 서버 시작이 실패하면 그동안 API가 멈춥니다.

전체 owner 확인:

```sql
SELECT tablename, tableowner
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY tablename;
```

특정 테이블 owner 확인:

```sql
SELECT tablename, tableowner
FROM pg_tables
WHERE schemaname = 'public'
  AND tablename IN ('purchase_links', 'reviews', 'books');
```

Flyway 상태 확인:

```sql
SELECT version, description, success
FROM flyway_schema_history
ORDER BY installed_rank DESC
LIMIT 10;
```

2026-06-24 장애 기록:

- `V4__purchase_link_click_count.sql`이 `purchase_links.click_count` 컬럼을 추가했습니다.
- 스테이징은 통과했지만, 운영에서는 `purchase_links`의 owner가 Flyway 애플리케이션 계정과 달라 서버 시작에 실패했습니다.
- 서비스를 먼저 복구한 뒤 `purchase_links` owner를 Flyway 계정으로 맞추고, 다시 배포해 Flyway V4·V5를 확인했습니다.

## 스테이징 환경

준비된 코드:

- 백엔드: `docker-compose.staging.yml`
- 백엔드 환경 변수 템플릿: `.env.staging.example`
- 프론트엔드 환경 변수 템플릿: `../chaekdojang-web/docs/staging-env.example`

AWS·Vercel 구성:

- 스테이징 전용 DB `chaekdojang_staging`을 만들었습니다.
- 스테이징 전용 DB 계정 `chaekdojang_staging`을 만들었습니다.
- 스테이징 API EC2는 운영과 분리되어 있습니다: `i-0b22c9c04390abada`
- 스테이징 API 고정 IP(Elastic IP): `3.34.214.8`
- 스테이징 API 컨테이너는 EC2의 `127.0.0.1:8081`에서 돌아갑니다.
- Nginx가 `staging-api.chaekdojang.com`을 `127.0.0.1:8081`로 넘깁니다.
- 스테이징 workflow를 수동 실행할 때만 GitHub Actions가 Docker 이미지를 빌드하고 SSM으로 배포합니다. EC2에서는 애플리케이션 코드를 빌드하지 않습니다.
- Cloudflare에 `staging-api.chaekdojang.com`을 `3.34.214.8`로 등록합니다.
- DNS가 연결되면 `staging-api.chaekdojang.com`용 Let's Encrypt 인증서를 발급합니다.
- Vercel 스테이징 프로젝트 또는 Preview 환경을 만듭니다.
- 프론트엔드 환경 변수:
  - `BACKEND_URL=https://staging-api.chaekdojang.com`
  - `NEXT_PUBLIC_API_BASE_URL=https://staging-api.chaekdojang.com`
- 백엔드 환경 변수:
  - `FRONTEND_URL=https://staging.chaekdojang.com`
  - `BACKEND_URL=https://staging-api.chaekdojang.com`
  - `CORS_ALLOWED_ORIGINS=<Vercel 스테이징 URL>`

현재 상태 (2026-10-27 확인):

- 스테이징 API EC2는 2026-10-14에 직접 중지한 상태입니다. 스테이징 API를 쓰려면 EC2를 먼저 시작해야 합니다.

## 관리자 감사 로그

준비된 코드:

- 테이블: `admin_audit_logs`
- API: `GET /api/admin/audit-logs`
- 기록하는 작업:
  - 회원 권한 변경
  - 독후감 숨김·숨김 해제
  - 관리자 문의 답변

프론트엔드:

- 관리자 화면(`app/admin/page.tsx`)에 감사 로그 조회가 들어가 있습니다 (2026-10-27 확인).

## S3 파일 업로드

준비된 코드:

- 기존 multipart 엔드포인트는 그대로 동작합니다: `POST /api/upload/profile-image`
- `STORAGE_TYPE=s3`이면 같은 엔드포인트가 S3에 올립니다.
- 직접 업로드 준비용 엔드포인트도 있습니다: `POST /api/upload/profile-image/presigned-url`

백엔드 환경 변수 예시:

```text
STORAGE_TYPE=s3
S3_UPLOAD_BUCKET=chaekdojang-prod-uploads-863518416212
AWS_REGION=ap-northeast-2
S3_PUBLIC_BASE_URL=https://cdn.chaekdojang.com
S3_PROFILE_IMAGE_PREFIX=profile-images
S3_PRESIGNED_URL_EXPIRATION_MINUTES=10
```

현재 운영 상태:

- 비공개 업로드 버킷 생성 완료: `chaekdojang-prod-uploads-863518416212`
- CloudFront 배포: `EM129YKDL37WV`
- 공개 주소: `https://cdn.chaekdojang.com`
- 업로드 버킷은 비공개로 두고, CloudFront가 Origin Access Control로 읽습니다.
- EC2 역할에 `profile-images/*` 경로의 `s3:PutObject` 권한이 있습니다.
- 버킷 CORS에 운영·스테이징·로컬 프론트엔드 주소를 넣었습니다.
- `cdn.chaekdojang.com`용 ACM 인증서를 발급해 CloudFront에 붙였습니다.

Cloudflare DNS 레코드 (2026-10-27 기준 모두 등록됨):

```text
Type: CNAME
Name: _4a4bd83610a57a27db386dc6d773e896.cdn
Target: _951479b24d12989345c58defd1a62b3d.jkddzztszm.acm-validations.aws

Type: CNAME
Name: cdn
Target: d2due94r4aw772.cloudfront.net

Type: A
Name: staging-api
Target: 3.34.214.8
Proxy status: DNS only
```

버킷 CORS:

```json
[
  {
    "AllowedHeaders": ["*"],
    "AllowedMethods": ["PUT", "POST", "GET"],
    "AllowedOrigins": [
      "https://www.chaekdojang.com",
      "https://chaekdojang.com",
      "https://staging.chaekdojang.com",
      "http://localhost:3000"
    ],
    "ExposeHeaders": ["ETag"],
    "MaxAgeSeconds": 3000
  }
]
```

## 모니터링 알람

현재 운영 상태:

- 서울 리전 운영 알림용 SNS 토픽: `chaekdojang-ops-alerts`
- 공개 API Route 53 헬스 체크: `06519a62-f24b-4284-8c3a-d1f033125a6f`
- EC2 알람:
  - `chaekdojang-ec2-cpu-high`
  - `chaekdojang-ec2-status-check-failed`
  - `chaekdojang-ec2-memory-high`
  - `chaekdojang-ec2-disk-root-high`
- RDS 알람:
  - `chaekdojang-rds-cpu-high`
  - `chaekdojang-rds-free-storage-low`
  - `chaekdojang-rds-connections-high`
- 엣지·API 알람:
  - `chaekdojang-api-public-health-down`
  - `chaekdojang-cloudfront-5xx-high`

## 서평단 전자책 저장소 (PDF)

서평단 PDF는 절대 이미지 버킷에 두지 않습니다. 이미지 버킷은 CloudFront(`cdn.chaekdojang.com`)로 공개 제공되므로, 그곳에 둔 파일은 로그인 없이 읽을 수 있습니다. 그러면 선정·워터마크·만료·회수 확인을 모두 건너뛰게 됩니다.

준비된 코드:

- `EbookStorageService`는 `app.storage.s3.ebook-bucket`만 씁니다.
- `app.storage.s3.bucket`으로 대신 저장하지 않으며, 이미지 버킷과 같은 버킷이 설정되면 사용을 거부합니다.
- `S3_EBOOK_BUCKET`이 비어 있으면 전자책 업로드·다운로드는 `EBOOK_STORAGE_NOT_CONFIGURED`로 막히고 시작 시 경고 로그가 남습니다. 나머지 API는 정상 동작합니다.
- 전자책 파일에는 공개 URL을 주지 않습니다. 읽는 경로는 `GET /api/dojangdan/applications/{id}/ebook/download` 하나뿐이며, 요청마다 열람 권한·만료·회수 여부를 다시 확인합니다.

AWS 작업:

- 비공개 S3 버킷을 만듭니다. 예: `chaekdojang-prod-ebooks-863518416212`
- 그 버킷의 퍼블릭 액세스를 모두 차단합니다.
- 기본 서버 측 암호화를 켭니다.
- CloudFront 배포에 연결하지 않습니다.
- 공개 버킷 정책을 붙이지 않습니다.
- EC2 인스턴스 역할에는 `arn:aws:s3:::chaekdojang-prod-ebooks-863518416212/campaign-ebooks/*` 경로의 `s3:GetObject`, `s3:PutObject`만 허용합니다.

EC2 `.env.production`:

```text
S3_EBOOK_BUCKET=chaekdojang-prod-ebooks-863518416212
```

배포 후 확인:

```bash
# 1) 전용 버킷은 공개 접근이 막혀 있어야 한다 (AccessDenied 기대)
curl -sI "https://chaekdojang-prod-ebooks-863518416212.s3.ap-northeast-2.amazonaws.com/campaign-ebooks/originals/test.pdf" | head -1

# 2) 이미지 버킷 정책이 전자책 프리픽스까지 열어두지 않았는지 확인
aws s3api get-bucket-policy --bucket <image-bucket> --query Policy --output text
```

기존 파일 처리:

- 이 변경 전에 올린 서평단 PDF는 이미지 버킷의 `campaign-ebooks/` 아래에 있습니다. 그 파일들은 지우고 앱에서 다시 올립니다.
