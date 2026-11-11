// header-menu.js
document.addEventListener("DOMContentLoaded", () => {
    const btn = document.getElementById("profileMenuButton");
    const menu = document.getElementById("profileMenu");

    if (!btn || !menu) return;

    // 버튼 클릭 → 토글
    btn.addEventListener("click", (e) => {
        e.stopPropagation();
        menu.classList.toggle("is-open");
    });

    // 메뉴 밖 클릭 → 닫기
    document.addEventListener("click", () => {
        menu.classList.remove("is-open");
    });

    // 메뉴 아이템 클릭 처리
    menu.addEventListener("click", (e) => {
        const item = e.target.closest(".profile-menu__item");
        if (!item) return;

        const action = item.dataset.action;
        menu.classList.remove("is-open");

        if (action === "edit") {
            // 회원정보수정 페이지
            window.location.href = "/members/edit";
        }
        if (action === "password") {
            // 비밀번호수정 페이지
            window.location.href = "/members/password";
        }
        if (action === "logout") {
            // 로그인 백엔드 붙이기 전까지는 임시 로그아웃 처리
            alert("로그아웃 되었습니다.");
            window.location.href = "/login";
        }
    });
});
