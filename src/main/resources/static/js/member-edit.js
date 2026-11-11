// member-edit.js

const nickInput = document.getElementById("edit-nickname");
const nickHelper = document.getElementById("edit-nickname-helper");
const btnEdit = document.getElementById("btnEdit");

// 모달 요소
const withdrawModal = document.getElementById("withdrawModal");
const btnWithdrawOpen = document.getElementById("btnWithdrawOpen");
const btnWithdrawCancel = document.getElementById("btnWithdrawCancel");
const btnWithdrawConfirm = document.getElementById("btnWithdrawConfirm");

// 유효성 검사
function isValidNickname(nick) {
    return nick && nick.length >= 1 && nick.length <= 10;
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
    const nick = nickInput.value.trim();
    btnEdit.disabled = !isValidNickname(nick);
}

if (btnEdit) {
    btnEdit.disabled = true;
}

// 실시간 검증
nickInput?.addEventListener("input", () => {
    const nick = nickInput.value.trim();

    if (!nick) {
        clearError(nickHelper);
    } else if (!isValidNickname(nick)) {
        showError(nickHelper, "닉네임은 1~10자 사이로 입력해주세요.");
    } else {
        clearError(nickHelper);
    }

    updateBtn();
});

// 수정하기 클릭 (현재는 API 없이 토스트만)
btnEdit?.addEventListener("click", async (e) => {
    e.preventDefault();

    const nick = nickInput.value.trim();
    if (!isValidNickname(nick)) {
        showError(nickHelper, "닉네임은 1~10자 사이로 입력해주세요.");
        return;
    }

    // TODO: 나중에 PATCH /api/v1/users/{id}로 교체
    showToast("닉네임이 수정되었습니다.");
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

// 회원 탈퇴 모달
btnWithdrawOpen?.addEventListener("click", (e) => {
    e.preventDefault();
    withdrawModal?.classList.add("is-open");
});

btnWithdrawCancel?.addEventListener("click", () => {
    withdrawModal?.classList.remove("is-open");
});

withdrawModal?.addEventListener("click", (e) => {
    if (e.target === withdrawModal) withdrawModal.classList.remove("is-open");
});

btnWithdrawConfirm?.addEventListener("click", async () => {
    // TODO: 나중에 DELETE /api/v1/users/{id} 로 교체
    alert("회원 탈퇴가 완료되었습니다.");
    window.location.href = "/login";
});
