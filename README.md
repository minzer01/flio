# Flio — 여행 정보 커뮤니티

여행지에서 겪은 맛집, 숙소, 교통, 사진 명소, 액티비티 정보를 테마와 분위기로 나눠 공유하는 커뮤니티 웹 서비스입니다.

## 기술 스택

- Java 21, Spring Boot
- Spring Data JPA, MySQL
- Spring Security, JWT
- HTML, CSS, JavaScript (fetch로 REST API 호출)
- p6spy (쿼리 로그 확인)

## 주요 기능

- 회원가입, 로그인 (JWT 기반 인증)
- 게시글 작성, 조회, 수정, 삭제
  - 테마: 맛집 · 숙소 · 교통 · 사진 · 액티비티
  - 분위기: 조용한 · 북적이는 · 로컬 · 관광지
- 댓글 작성, 조회, 수정, 삭제
- 회원 정보 · 비밀번호 수정, 회원 탈퇴

## 구현 포인트

### JWT 기반 Stateless 인증
세션 대신 JWT를 사용해 서버가 로그인 상태를 저장하지 않도록 했습니다. 요청마다 `JwtFilter`에서 토큰을 검증하고, 비밀번호는 암호화해 저장합니다.

### 회원 탈퇴 소프트 삭제
탈퇴 시 회원 데이터를 지우지 않고 상태값을 `DELETED`로 바꿉니다. 이메일과 비밀번호는 비우고 닉네임은 "탈퇴한 사용자"로 바꿔, 개인정보는 남기지 않으면서 탈퇴 회원이 쓴 글과 댓글은 다른 사용자가 계속 볼 수 있게 했습니다.

### 연관관계 지연 로딩
게시글·댓글의 작성자 연관관계를 `FetchType.LAZY`로 설정하고, p6spy로 실제 실행되는 쿼리를 확인했습니다.

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

MySQL에 `community` 데이터베이스를 만든 뒤, 환경 변수를 설정하고 실행합니다.

```bash
export DB_PASSWORD=<MySQL 비밀번호>
export JWT_SECRET=<Base64로 인코딩한 시크릿 키>
./gradlew bootRun
```

`http://localhost:8080/posts`에서 화면을 확인할 수 있습니다.
