#!/bin/bash

# N+1 문제 비교 테스트 스크립트
# 사용법: ./test-n-plus-one.sh

cd /Users/ahnmin-yeong/Downloads/flio

echo "============================================"
echo "N+1 문제 해결 전/후 쿼리 비교 테스트"
echo "============================================"
echo ""

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

# 서버 종료
pkill -f "gradlew\|java.*flio" 2>/dev/null
sleep 2

test_queries() {
    local VERSION=$1

    echo ""
    echo "============================================"
    echo "=== $VERSION 테스트 ==="
    echo "============================================"
    echo ""

    # 서버 시작
    echo "서버 시작 중..."
    DB_PASSWORD=1234 JWT_SECRET=$(openssl rand -base64 32) ./gradlew bootRun 2>&1 | tee /tmp/server_$VERSION.log &
    SERVER_PID=$!

    # 서버 준비 대기
    for i in {1..30}; do
        if curl -s http://localhost:8080 > /dev/null 2>&1; then
            echo "서버 준비 완료!"
            break
        fi
        sleep 1
    done

    sleep 3

    # 토큰 획득
    RESPONSE=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
        -H "Content-Type: application/json" \
        -d '{"email":"user1@test.com","password":"1234"}')
    TOKEN=$(echo $RESPONSE | python3 -c "import sys,json; print(json.load(sys.stdin)['accessToken'])" 2>/dev/null)

    if [ -z "$TOKEN" ]; then
        echo "토큰 획득 실패. 테스트 데이터가 있는지 확인하세요."
        pkill -f "gradlew\|java.*flio"
        return
    fi

    echo ""
    echo ">>> 게시글 목록 API 호출"
    echo ">>> 아래 로그에서 'select' 쿼리를 확인하세요"
    echo "-----------------------------------------------"

    curl -s -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/v1/posts > /dev/null
    sleep 2

    echo ""
    echo ">>> 댓글 목록 API 호출 (게시글 1번)"
    echo ">>> 아래 로그에서 'select' 쿼리를 확인하세요"
    echo "-----------------------------------------------"

    curl -s -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/v1/posts/1/comments > /dev/null
    sleep 2

    echo ""
    echo "-----------------------------------------------"
    echo "테스트 완료. 위 로그에서 SELECT 쿼리 개수를 확인하세요."
    echo ""

    # 서버 종료
    pkill -f "gradlew\|java.*flio"
    sleep 2
}

echo "테스트를 시작합니다."
echo ""
echo "주의: 이 스크립트는 터미널에서 직접 실행해야 합니다."
echo "      (서버 로그가 실시간으로 출력됩니다)"
echo ""
echo "Press Enter to continue..."
read

test_queries "AFTER_FETCHJOIN"

echo ""
echo "============================================"
echo "테스트 완료!"
echo "============================================"
echo ""
echo "예상 결과:"
echo "- 변경 전: 게시글 목록 1+N개, 댓글 목록 2+N개 쿼리"
echo "- 변경 후: 게시글 목록 1개, 댓글 목록 2개 쿼리"
echo ""
