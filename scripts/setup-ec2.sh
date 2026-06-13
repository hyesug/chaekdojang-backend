#!/bin/bash
# EC2 최초 1회 실행하는 초기 설정 스크립트
# 실행 방법: chmod +x setup-ec2.sh && ./setup-ec2.sh your-domain.com
#
# 사전 조건:
#   - Ubuntu 22.04 EC2 인스턴스
#   - 도메인 DNS A 레코드가 이 EC2 IP를 가리키고 있어야 함
#   - EC2 보안 그룹에서 80, 443, 8080 포트 열려 있어야 함

set -e  # 에러 발생 즉시 중단

DOMAIN="${1:?'사용법: ./setup-ec2.sh your-domain.com'}"

echo ""
echo "==================================================="
echo "  책도장 EC2 초기 설정"
echo "  도메인: $DOMAIN"
echo "==================================================="
echo ""

# ── 1. 시스템 업데이트 ────────────────────────────────
echo ">>> [1/5] 시스템 업데이트"
sudo apt update && sudo apt upgrade -y

# ── 2. Docker 설치 ───────────────────────────────────
echo ">>> [2/5] Docker 설치"
sudo apt install -y docker.io docker-compose-plugin
sudo systemctl enable docker
sudo systemctl start docker
sudo usermod -aG docker "$USER"
echo "    Docker 설치 완료: $(docker --version)"

# ── 3. Nginx + Certbot 설치 ──────────────────────────
echo ">>> [3/5] Nginx + Certbot 설치"
sudo apt install -y nginx certbot python3-certbot-nginx
sudo systemctl enable nginx
echo "    Nginx 설치 완료: $(nginx -v 2>&1)"

# ── 4. Nginx 설정 ────────────────────────────────────
echo ">>> [4/5] Nginx 설정 적용"

# nginx 설정 파일에서 YOUR_DOMAIN을 실제 도메인으로 교체 후 복사
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
CONF_SRC="$SCRIPT_DIR/../nginx/chaekdojang.conf"

if [ ! -f "$CONF_SRC" ]; then
    echo "ERROR: nginx/chaekdojang.conf 파일을 찾을 수 없습니다."
    echo "       먼저 이 레포지토리를 EC2에 클론하거나 파일을 복사해주세요."
    exit 1
fi

sudo sed "s/YOUR_DOMAIN/$DOMAIN/g" "$CONF_SRC" \
    | sudo tee /etc/nginx/sites-available/chaekdojang > /dev/null

sudo ln -sf /etc/nginx/sites-available/chaekdojang /etc/nginx/sites-enabled/chaekdojang

# 기본 nginx 사이트 비활성화
sudo rm -f /etc/nginx/sites-enabled/default

sudo nginx -t
sudo systemctl reload nginx
echo "    Nginx 설정 완료"

# ── 5. SSL 인증서 발급 (Let's Encrypt) ───────────────
echo ">>> [5/5] SSL 인증서 발급"
echo ""
echo "    이메일 주소를 입력하라는 프롬프트가 나옵니다."
echo "    약관 동의(A), 이메일 공유 여부(N 권장)를 선택하세요."
echo ""

sudo certbot --nginx -d "$DOMAIN"

# ── 6. GitHub Actions에서 sudo nginx -s reload 허용 ──
# CI/CD 스크립트가 비밀번호 없이 nginx를 리로드할 수 있도록 설정
echo "$USER ALL=(ALL) NOPASSWD: /usr/sbin/nginx" \
    | sudo tee /etc/sudoers.d/nginx-reload > /dev/null
sudo chmod 440 /etc/sudoers.d/nginx-reload
echo "    sudoers 설정 완료 (nginx reload 권한)"

# ── 자동 갱신 확인 (Certbot이 systemd 타이머 자동 등록) ──
echo ""
echo "==================================================="
echo "  설정 완료!"
echo "==================================================="
echo ""
echo "  다음 단계:"
echo "  1. 앱 폴더 생성 및 환경변수 설정:"
echo "     mkdir ~/chaekdojang"
echo "     nano ~/chaekdojang/.env   ← .env.example 참고"
echo ""
echo "  2. 첫 배포:"
echo "     main 브랜치에 push하면 GitHub Actions가 자동 배포"
echo ""
echo "  3. SSL 자동 갱신 확인:"
echo "     sudo certbot renew --dry-run"
echo ""
echo "  현재 Nginx 상태:"
sudo systemctl status nginx --no-pager -l | head -20
