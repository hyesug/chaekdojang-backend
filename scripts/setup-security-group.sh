#!/bin/bash
# EC2 보안 그룹 인바운드 규칙 설정 스크립트
#
# 사전 조건:
#   - AWS CLI 설치 및 자격증명 설정 완료 (aws configure)
#   - 권한: ec2:AuthorizeSecurityGroupIngress, ec2:RevokeSecurityGroupIngress
#
# 사용법:
#   ./setup-security-group.sh <보안그룹ID> <내IP>
#   예) ./setup-security-group.sh sg-0123456789abcdef0 203.0.113.1/32
#
# 보안 그룹 ID 확인 방법:
#   AWS 콘솔 → EC2 → 인스턴스 → 해당 인스턴스 클릭 → 보안 탭 → 보안 그룹 ID

set -e

SG_ID="${1:?'[오류] 보안 그룹 ID를 입력하세요. 예: ./setup-security-group.sh sg-xxxxxxxx 내IP/32'}"
MY_IP="${2:?'[오류] 내 IP를 입력하세요. 예: ./setup-security-group.sh sg-xxxxxxxx 203.0.113.1/32'}"

echo ""
echo "==================================================="
echo "  보안 그룹 설정: $SG_ID"
echo "  내 IP (SSH 허용): $MY_IP"
echo "==================================================="

# ── 기존 규칙 제거 ────────────────────────────────────

echo ""
echo ">>> 8080 포트 외부 오픈 제거 (이미 없으면 무시됩니다)"
aws ec2 revoke-security-group-ingress \
    --group-id "$SG_ID" \
    --ip-permissions '[
        {"IpProtocol":"tcp","FromPort":8080,"ToPort":8080,"IpRanges":[{"CidrIp":"0.0.0.0/0"}]},
        {"IpProtocol":"tcp","FromPort":8080,"ToPort":8080,"Ipv6Ranges":[{"CidrIpv6":"::/0"}]}
    ]' 2>/dev/null && echo "    8080 외부 접근 제거 완료" || echo "    8080 규칙 없음 (건너뜀)"

echo ""
echo ">>> SSH 22포트: 내 IP만 허용"
aws ec2 revoke-security-group-ingress \
    --group-id "$SG_ID" \
    --ip-permissions '[{"IpProtocol":"tcp","FromPort":22,"ToPort":22,"IpRanges":[{"CidrIp":"0.0.0.0/0"}]}]' \
    2>/dev/null || true

aws ec2 authorize-security-group-ingress \
    --group-id "$SG_ID" \
    --ip-permissions "[
        {\"IpProtocol\":\"tcp\",\"FromPort\":22,\"ToPort\":22,
         \"IpRanges\":[{\"CidrIp\":\"$MY_IP\",\"Description\":\"SSH - my IP only\"}]}
    ]" && echo "    SSH $MY_IP 허용 완료" || echo "    SSH 규칙 이미 있음 (건너뜀)"

# ── 필요한 포트 추가 ──────────────────────────────────

echo ""
echo ">>> HTTP 80포트: 전체 허용 (HTTPS 리다이렉트 + Let's Encrypt 인증용)"
aws ec2 authorize-security-group-ingress \
    --group-id "$SG_ID" \
    --ip-permissions '[
        {"IpProtocol":"tcp","FromPort":80,"ToPort":80,
         "IpRanges":[{"CidrIp":"0.0.0.0/0","Description":"HTTP"}],
         "Ipv6Ranges":[{"CidrIpv6":"::/0","Description":"HTTP IPv6"}]}
    ]' && echo "    HTTP 80 허용 완료" || echo "    HTTP 규칙 이미 있음 (건너뜀)"

echo ""
echo ">>> HTTPS 443포트: 전체 허용"
aws ec2 authorize-security-group-ingress \
    --group-id "$SG_ID" \
    --ip-permissions '[
        {"IpProtocol":"tcp","FromPort":443,"ToPort":443,
         "IpRanges":[{"CidrIp":"0.0.0.0/0","Description":"HTTPS"}],
         "Ipv6Ranges":[{"CidrIpv6":"::/0","Description":"HTTPS IPv6"}]}
    ]' && echo "    HTTPS 443 허용 완료" || echo "    HTTPS 규칙 이미 있음 (건너뜀)"

# ── 최종 상태 확인 ────────────────────────────────────

echo ""
echo "==================================================="
echo "  최종 인바운드 규칙:"
echo "==================================================="
aws ec2 describe-security-groups \
    --group-ids "$SG_ID" \
    --query 'SecurityGroups[0].IpPermissions[*].{Port:FromPort,Protocol:IpProtocol,CIDR:IpRanges[0].CidrIp}' \
    --output table

echo ""
echo "설정 완료!"
echo ""
echo "최종 인바운드 규칙이어야 합니다:"
echo "  ✅ 22  (SSH)   → $MY_IP 만"
echo "  ✅ 80  (HTTP)  → 0.0.0.0/0"
echo "  ✅ 443 (HTTPS) → 0.0.0.0/0"
echo "  ❌ 8080        → 없음 (docker-compose에서 127.0.0.1로 바인딩)"
