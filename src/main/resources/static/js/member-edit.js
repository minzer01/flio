// member-edit.js

const USER_API_URL = '/api/v1/users/me';

// 인증 헤더 가져오기
function getAuthHeaders() {
    const token = localStorage.getItem('accessToken');
    return token ? { 'Authorization': `Bearer ${token}` } : {};
}

// 페이지 로드 시 사용자 정보 불러오기
document.addEventListener('DOMContentLoaded', () => {
    loadUserInfo();
    setupNicknameForm();
    setupPasswordForm();
    setupWithdrawModal();
});

// 사용자 정보 로드
async function loadUserInfo() {
    const token = localStorage.getItem('accessToken');
    if (!token) {
        alert('로그인이 필요합니다.');
        window.location.href = '/login';
        return;
    }

    try {
        const res = await fetch(USER_API_URL, {
            headers: getAuthHeaders()
        });

        if (res.status === 401 || res.status === 403) {
            alert('로그인이 필요합니다.');
            window.location.href = '/login';
            return;
        }

        if (!res.ok) {
            throw new Error(`HTTP ${res.status}`);
        }

        const user = await res.json();
        document.getElementById('edit-email').value = user.email;
        document.getElementById('edit-nickname').value = user.nickname || '';
    } catch (err) {
        console.error('사용자 정보 로드 실패:', err);
        alert('사용자 정보를 불러오는 중 오류가 발생했습니다.');
    }
}

// ================== 닉네임 수정 ==================
function setupNicknameForm() {
    const nickInput = document.getElementById('edit-nickname');
    const nickHelper = document.getElementById('edit-nickname-helper');
    const btnEdit = document.getElementById('btnEditNickname');

    function isValidNickname(nick) {
        return nick && nick.length >= 1 && nick.length <= 10;
    }

    function updateBtn() {
        const nick = nickInput.value.trim();
        btnEdit.disabled = !isValidNickname(nick);
    }

    nickInput?.addEventListener('input', () => {
        const nick = nickInput.value.trim();

        if (!nick) {
            clearError(nickHelper);
        } else if (!isValidNickname(nick)) {
            showError(nickHelper, '닉네임은 1~10자 사이로 입력해주세요.');
        } else {
            clearError(nickHelper);
        }

        updateBtn();
    });

    btnEdit?.addEventListener('click', async () => {
        const nick = nickInput.value.trim();
        if (!isValidNickname(nick)) {
            showError(nickHelper, '닉네임은 1~10자 사이로 입력해주세요.');
            return;
        }

        try {
            const res = await fetch(USER_API_URL, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                    ...getAuthHeaders()
                },
                body: JSON.stringify({ nickname: nick })
            });

            if (res.status === 401 || res.status === 403) {
                alert('로그인이 필요합니다.');
                window.location.href = '/login';
                return;
            }

            if (!res.ok) {
                throw new Error(`HTTP ${res.status}`);
            }

            showToast('닉네임이 수정되었습니다.');
        } catch (err) {
            console.error('닉네임 수정 실패:', err);
            alert('닉네임 수정 중 오류가 발생했습니다.');
        }
    });
}

// ================== 비밀번호 변경 ==================
function setupPasswordForm() {
    const pwInput = document.getElementById('pw-new');
    const pwHelper = document.getElementById('pw-new-helper');
    const pwConfirmInput = document.getElementById('pw-confirm');
    const pwConfirmHelper = document.getElementById('pw-confirm-helper');
    const btnEditPw = document.getElementById('btnEditPassword');

    function isValidPassword(pw) {
        if (!pw) return false;
        if (pw.length < 8 || pw.length > 20) return false;
        const hasUpper = /[A-Z]/.test(pw);
        const hasLower = /[a-z]/.test(pw);
        const hasNumber = /[0-9]/.test(pw);
        const hasSpecial = /[^A-Za-z0-9]/.test(pw);
        return hasUpper && hasLower && hasNumber && hasSpecial;
    }

    function updatePwBtn() {
        const pw = pwInput.value.trim();
        const pw2 = pwConfirmInput.value.trim();
        btnEditPw.disabled = !(isValidPassword(pw) && pw === pw2);
    }

    pwInput?.addEventListener('input', () => {
        const pw = pwInput.value.trim();

        if (!pw) {
            clearError(pwHelper);
        } else if (!isValidPassword(pw)) {
            showError(pwHelper, '비밀번호는 8~20자, 대/소문자·숫자·특수문자를 모두 포함해야 합니다.');
        } else {
            clearError(pwHelper);
        }

        const pw2 = pwConfirmInput.value.trim();
        if (pw2 && pw !== pw2) {
            showError(pwConfirmHelper, '비밀번호가 일치하지 않습니다.');
        } else {
            clearError(pwConfirmHelper);
        }

        updatePwBtn();
    });

    pwConfirmInput?.addEventListener('input', () => {
        const pw = pwInput.value.trim();
        const pw2 = pwConfirmInput.value.trim();

        if (!pw2) {
            clearError(pwConfirmHelper);
        } else if (pw !== pw2) {
            showError(pwConfirmHelper, '비밀번호가 일치하지 않습니다.');
        } else {
            clearError(pwConfirmHelper);
        }

        updatePwBtn();
    });

    btnEditPw?.addEventListener('click', async () => {
        const pw = pwInput.value.trim();
        const pw2 = pwConfirmInput.value.trim();

        if (!isValidPassword(pw)) {
            showError(pwHelper, '비밀번호는 8~20자, 대/소문자·숫자·특수문자를 모두 포함해야 합니다.');
            return;
        }
        if (pw !== pw2) {
            showError(pwConfirmHelper, '비밀번호가 일치하지 않습니다.');
            return;
        }

        try {
            const res = await fetch(`${USER_API_URL}/password`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                    ...getAuthHeaders()
                },
                body: JSON.stringify({ newPassword: pw })
            });

            if (res.status === 401 || res.status === 403) {
                alert('로그인이 필요합니다.');
                window.location.href = '/login';
                return;
            }

            if (!res.ok) {
                throw new Error(`HTTP ${res.status}`);
            }

            showToast('비밀번호가 변경되었습니다.');
            pwInput.value = '';
            pwConfirmInput.value = '';
            btnEditPw.disabled = true;
        } catch (err) {
            console.error('비밀번호 변경 실패:', err);
            alert('비밀번호 변경 중 오류가 발생했습니다.');
        }
    });
}

// ================== 회원 탈퇴 모달 ==================
function setupWithdrawModal() {
    const withdrawModal = document.getElementById('withdrawModal');
    const btnWithdrawOpen = document.getElementById('btnWithdrawOpen');
    const btnWithdrawCancel = document.getElementById('btnWithdrawCancel');
    const btnWithdrawConfirm = document.getElementById('btnWithdrawConfirm');

    btnWithdrawOpen?.addEventListener('click', (e) => {
        e.preventDefault();
        withdrawModal?.classList.add('is-open');
    });

    btnWithdrawCancel?.addEventListener('click', () => {
        withdrawModal?.classList.remove('is-open');
    });

    withdrawModal?.addEventListener('click', (e) => {
        if (e.target === withdrawModal) withdrawModal.classList.remove('is-open');
    });

    btnWithdrawConfirm?.addEventListener('click', async () => {
        const userId = localStorage.getItem('currentUserId');
        if (!userId) {
            alert('로그인이 필요합니다.');
            window.location.href = '/login';
            return;
        }

        try {
            const res = await fetch(`/api/v1/users/${userId}`, {
                method: 'DELETE',
                headers: getAuthHeaders()
            });

            if (!res.ok) {
                throw new Error(`HTTP ${res.status}`);
            }

            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            localStorage.removeItem('currentUserId');

            alert('회원 탈퇴가 완료되었습니다.');
            window.location.href = '/login';
        } catch (err) {
            console.error('회원 탈퇴 실패:', err);
            alert('회원 탈퇴 중 오류가 발생했습니다.');
        }
    });
}

// ================== 유틸 함수 ==================
function showError(helperEl, msg) {
    if (!helperEl) return;
    helperEl.textContent = msg;
    helperEl.classList.add('form-helper--error');
}

function clearError(helperEl) {
    if (!helperEl) return;
    helperEl.textContent = '';
    helperEl.classList.remove('form-helper--error');
}

function showToast(msg) {
    const toast = document.createElement('div');
    toast.textContent = msg;
    toast.style.position = 'fixed';
    toast.style.bottom = '40px';
    toast.style.left = '50%';
    toast.style.transform = 'translateX(-50%)';
    toast.style.background = '#222';
    toast.style.color = '#fff';
    toast.style.padding = '10px 14px';
    toast.style.borderRadius = '6px';
    toast.style.zIndex = '9999';
    document.body.appendChild(toast);

    setTimeout(() => toast.remove(), 1500);
}
