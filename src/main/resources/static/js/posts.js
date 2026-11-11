// src/main/resources/static/js/posts.js

const POST_API_URL = '/api/v1/posts';

// 페이지 로드 완료 시점에 실행
document.addEventListener('DOMContentLoaded', () => {
    const postListContainer = document.querySelector('.posts-list');

    if (!postListContainer) {
        console.error(
            "'.posts-list' 요소를 찾을 수 없습니다. posts.html에 div.posts-list가 있는지 확인하세요."
        );
        return;
    }

    fetchPostList(postListContainer);
});

// 백엔드에서 게시글 목록 가져오기
async function fetchPostList(postListContainer) {
    try {
        // 1. localStorage에서 accessToken 꺼내기
        const token = localStorage.getItem("accessToken");

        // 2. 토큰이 없으면 로그인 페이지로 보내기
        if (!token) {
            alert("로그인이 필요합니다.");
            window.location.href = "/login";
            return;
        }

        // 3. Authorization 헤더에 토큰 실어서 요청 보내기
        const response = await fetch(POST_API_URL, {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        // 4. 인증 실패(401/403)면 로그인 페이지로
        if (response.status === 401 || response.status === 403) {
            alert("로그인이 필요한 서비스입니다.");
            window.location.href = "/login";
            return;
        }

        if (!response.ok) {
            throw new Error(`HTTP 오류! 상태: ${response.status}`);
        }

        const posts = await response.json(); // JSON 배열
        renderPosts(posts, postListContainer);
    } catch (error) {
        console.error('게시글 목록을 불러오는 데 실패했습니다:', error);
        if (postListContainer) {
            postListContainer.innerHTML =
                '<p>데이터를 불러오는 중 오류가 발생했습니다.</p>';
        }
    }
}



// JSON 배열 → HTML 렌더링
function renderPosts(posts, postListContainer) {
    if (!postListContainer) {
        console.warn('renderPosts: postListContainer가 없습니다.');
        return;
    }

    if (!posts || posts.length === 0) {
        postListContainer.innerHTML =
            '<p>게시글이 없습니다. 첫 글을 작성해 보세요!</p>';
        return;
    }

    // 목록 내용을 비우고, 새 데이터 채우기
    postListContainer.innerHTML = '';

    // PostResponse: { id, title, content, writerId, writerNickname, imageUrl, createdAt, ... }
    posts.forEach(post => {
        const hasImage =
            post.imageUrl &&
            typeof post.imageUrl === 'string' &&
            post.imageUrl.trim() !== '';

        const itemClass = hasImage
            ? 'post-item post-item--with-image'
            : 'post-item';

        // 썸네일 HTML (imageUrl 있을 때만)
        const thumbHtml = hasImage
            ? `
                <div class="post-item__thumb">
                    <img src="${post.imageUrl}" alt="post image" />
                </div>
              `
            : '';

        const displayDate = post.createdAt
            ? timeAgo(post.createdAt)          // ⭐ 여기서 timeAgo 사용
            : '날짜 정보 없음';

        const bodyHtml = `
            <div class="post-item__body">
                <div class="post-item__title">
                    ${post.title || '(제목 없음)'}
                </div>

                <div class="post-item__meta">
                    <span>${displayDate}</span>
                </div>

                <div class="post-item__writer-row">
                    <span>작성자: ${post.writerNickname || '익명'}</span>
                </div>
            </div>
        `;

        const postItemHtml = `
            <article class="${itemClass}" data-id="${post.id}">
                <a href="/posts/${post.id}" class="post-item__link"></a>
                ${thumbHtml}
                ${bodyHtml}
            </article>
        `;

        postListContainer.insertAdjacentHTML('beforeend', postItemHtml);
    });
}
