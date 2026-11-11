// password-edit.js

const pwInput = document.getElementById("pw-new");
const pwHelper = document.getElementById("pw-new-helper");

const pwConfirmInput = document.getElementById("pw-confirm");
const pwConfirmHelper = document.getElementById("pw-confirm-helper");

const btnEditPw = document.getElementById("btnEdit");

// 유효성 검사
function isValidPassword(pw) {
    if (!pw) return false;
    if (pw.length < 8 || pw.length > 20) return false;
    const hasUpper = /[A-Z]/.test(pw);
    const hasLower = /[a-z]/.test(pw);
    const hasNumber = /[0-9]/.test(pw);
    const hasSpecial = /[^A-Za-z0-9]/.test(pw);
    return hasUpper && hasLower && hasNumber && hasSpecial;
}

// helper 표시
function showError(helperEl, msg) {
    helperEl.textContent = msg;
    helperEl.classList.add("form-helper--error");
}
function clearError(helperEl) {
    helperEl.textContent = "";
    helperEl.classList.remove("form-helper--error");
}

// 버튼 활성화
function updateBtn() {
    const pw = pwInput.value.trim();
    const pw2 = pwConfirmInput.value.trim();

    const ok = isValidPassword(pw) && pw === pw2;
    btnEditPw.disabled = !ok;
}

if (btnEditPw) {
    btnEditPw.disabled = true;
}

// 실시간 검증
pwInput?.addEventListener("input", () => {
    const pw = pwInput.value.trim();

    if (!pw) {
        clearError(pwHelper);
    } else if (!isValidPassword(pw)) {
        showError(
            pwHelper,
            "비밀번호는 8~20자, 대/소문자·숫자·특수문자를 모두 포함해야 합니다."
        );
    } else {
        clearError(pwHelper);
    }

    // 비밀번호가 바뀌면 확인도 다시 체크
    const pw2 = pwConfirmInput.value.trim();
    if (pw2 && pw !== pw2) {
        showError(pwConfirmHelper, "비밀번호가 일치하지 않습니다.");
    } else {
        clearError(pwConfirmHelper);
    }

    updateBtn();
});

pwConfirmInput?.addEventListener("input", () => {
    const pw = pwInput.value.trim();
    const pw2 = pwConfirmInput.value.trim();

    if (!pw2) {
        clearError(pwConfirmHelper);
    } else if (pw !== pw2) {
        showError(pwConfirmHelper, "비밀번호가 일치하지 않습니다.");
    } else {
        clearError(pwConfirmHelper);
    }

    updateBtn();
});

// 수정하기 클릭
btnEditPw?.addEventListener("click", async (e) => {
    e.preventDefault();

    const pw = pwInput.value.trim();
    const pw2 = pwConfirmInput.value.trim();

    if (!isValidPassword(pw)) {
        showError(
            pwHelper,
            "비밀번호는 8~20자, 대/소문자·숫자·특수문자를 모두 포함해야 합니다."
        );
        return;
    }
    if (pw !== pw2) {
        showError(pwConfirmHelper, "비밀번호가 일치하지 않습니다.");
        return;
    }

    // TODO: 나중에 PATCH /api/v1/users/{id}/password 로 교체
    showToast("비밀번호가 수정되었습니다.");
});

// 토스트
function showToast(msg) {
    const toast = document.createElement("div");
    toast.textContent = msg;
    toast.style.position = "fixed";
    toast.style.bottom = "40px";
    toast.style.left = "50%";
    toast.style.transform = "translateX(-50%)";
    toast.style.background = "#222";
    toast.style.color = "#fff";
    toast.style.padding = "10px 14px";
    toast.style.borderRadius = "6px";
    toast.style.zIndex = "9999";
    document.body.appendChild(toast);

    setTimeout(() => toast.remove(), 1500);
}
