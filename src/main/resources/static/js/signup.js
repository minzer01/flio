const SIGNUP_API_URL = '/api/v1/auth/signup'; // AuthController.signup()과 매칭

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('signupForm');
    if (!form) return;

    form.addEventListener('submit', onSignupSubmit);
});

async function onSignupSubmit(e) {
    e.preventDefault();

    const emailEl = document.getElementById('signupEmail');
    const passwordEl = document.getElementById('signupPassword');
    const nicknameEl = document.getElementById('signupNickname');

    const email = emailEl.value.trim();
    const password = passwordEl.value.trim();
    const nickname = nicknameEl.value.trim();

    if (!email || !password || !nickname) {
        alert('이메일 / 비밀번호 / 닉네임을 모두 입력해 주세요.');
        return;
    }

    const body = { email, password, nickname };

    try {
        const res = await fetch(SIGNUP_API_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body),
        });

        if (!res.ok) {
            alert(`회원가입 실패 (status: ${res.status})`);
            return;
        }

        const user = await res.json();
        console.log('회원가입 성공:', user);

        alert('회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.');
        window.location.href = '/login';
    } catch (err) {
        console.error('회원가입 에러:', err);
        alert('회원가입 중 오류가 발생했습니다.');
    }
}
