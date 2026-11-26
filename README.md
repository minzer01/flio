# Flio — 여행 정보 커뮤니티

> **English summary** — Flio is a travel-tip community backend built with Java 21 and Spring Boot. It provides JWT-based stateless authentication, post/comment CRUD with owner-only edit and delete, soft-delete account withdrawal, S3 image upload, and Docker Compose deployment. After the initial build, I fixed an IDOR vulnerability by taking the writer ID from the JWT instead of the request body, removed N+1 queries on list APIs with fetch joins, and added unit tests for the ownership and withdrawal rules.

여행지에서 겪은 맛집, 숙소, 교통, 사진 명소, 액티비티 정보를 테마와 분위기로 나눠 공유하는 커뮤니티 웹 서비스입니다.

- **개발 기간**: 2024.09 ~ 2025.02 (교내 중앙동아리 개인 프로젝트)
- **이후 보완**: 보안 취약점 수정, N+1 해결, S3 이미지 업로드, Docker 배포 구성, 테스트 추가
- GitHub에는 보완 작업과 함께 새로 올려, 커밋 기록은 보완 시점부터 시작합니다.

## 기술 스택

- Java 21, Spring Boot 3.5
- Spring Data JPA, MySQL
- Spring Security, JWT
- AWS S3 (이미지 업로드)
- Docker, Docker Compose
- JUnit 5, Mockito
- HTML, CSS, JavaScript (fetch로 REST API 호출)
- p6spy (쿼리 로그 확인)

## 주요 기능

- 회원가입, 로그인 (JWT 기반 인증)
- 게시글 작성, 조회, 수정, 삭제
  - 테마: 맛집 · 숙소 · 교통 · 사진 · 액티비티
  - 분위기: 조용한 · 북적이는 · 로컬 · 관광지
- 게시글 이미지 업로드 (S3)
- 댓글 작성, 조회, 수정, 삭제
- 회원 정보 · 비밀번호 수정, 회원 탈퇴

## 구현 포인트

### JWT 기반 Stateless 인증
세션 대신 JWT를 사용해 서버가 로그인 상태를 저장하지 않도록 했습니다. 요청마다 `JwtFilter`에서 토큰을 검증하고, 비밀번호는 암호화해 저장합니다.

### 작성자 위조(IDOR) 방지와 소유권 검증
처음에는 게시글·댓글 작성 요청 본문에 작성자 ID를 담아 보내, 클라이언트가 다른 사용자 ID로 글을 쓸 수 있는 구조였습니다. 작성자 ID를 요청 본문에서 빼고 JWT로 인증된 사용자(`@AuthenticationPrincipal`)에서 꺼내도록 바꿨고, 수정·삭제 시에는 작성자 본인인지 서비스 계층에서 확인합니다.

### 목록 조회 N+1 해결
게시글·댓글 목록에서 작성자 정보를 지연 로딩으로 읽어, 목록 크기만큼 추가 쿼리가 나가는 N+1 문제가 있었습니다. Fetch Join으로 작성자(게시글은 테마까지)를 한 번에 조회하도록 바꿨습니다.

| API | 변경 전 | 변경 후 |
| --- | --- | --- |
| 게시글 목록 | 1 + N | 1 |
| 댓글 목록 | 2 + N | 2 |

`test-n-plus-one.sh`로 실제 실행되는 SELECT 쿼리 수를 확인할 수 있습니다.

### 회원 탈퇴 소프트 삭제
탈퇴 시 회원 데이터를 지우지 않고 상태값을 `DELETED`로 바꿉니다. 이메일과 비밀번호는 비우고 닉네임은 "탈퇴한 사용자"로 바꿔, 개인정보는 남기지 않으면서 탈퇴 회원이 쓴 글과 댓글은 다른 사용자가 계속 볼 수 있게 했습니다.

### 테스트
DB 없이 실행되는 단위 테스트로 핵심 규칙을 검증합니다.
- 게시글 작성자는 인증된 사용자로 저장된다
- 다른 사용자의 게시글은 수정·삭제할 수 없다
- 탈퇴하면 개인정보를 지우고 상태만 `DELETED`로 바꾼다

## API

| 기능 | Method | URL |
| --- | --- | --- |
| 회원가입 | POST | /api/v1/auth/signup |
| 로그인 | POST | /api/v1/auth/login |
| 회원 조회 · 수정 · 탈퇴 | GET · PATCH · DELETE | /api/v1/users/{id} |
| 게시글 목록 · 작성 | GET · POST | /api/v1/posts |
| 게시글 상세 · 수정 · 삭제 | GET · PATCH · DELETE | /api/v1/posts/{postId} |
| 댓글 목록 · 작성 | GET · POST | /api/v1/posts/{postId}/comments |
| 댓글 상세 · 수정 · 삭제 | GET · PATCH · DELETE | /api/v1/posts/{postId}/comments/{commentId} |

## 실행 방법

### Docker Compose
`.env.example`을 `.env`로 복사해 값을 채운 뒤 실행합니다. MySQL과 애플리케이션이 함께 뜹니다.

```bash
cp .env.example .env
docker compose up -d --build
```

### 로컬 실행
MySQL에 `community` 데이터베이스를 만든 뒤, 환경 변수를 설정하고 실행합니다.

```bash
export DB_PASSWORD=<MySQL 비밀번호>
export JWT_SECRET=<Base64로 인코딩한 시크릿 키>
export AWS_S3_BUCKET=<버킷 이름>
export AWS_ACCESS_KEY=<액세스 키>
export AWS_SECRET_KEY=<시크릿 키>
./gradlew bootRun
```

`http://localhost:8080/posts`에서 화면을 확인할 수 있습니다.

### 테스트

```bash
./gradlew test
```
