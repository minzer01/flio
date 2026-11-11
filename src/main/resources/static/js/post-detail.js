// src/main/resources/static/js/post-detail.js

const POST_API_URL = '/api/v1/posts';

// 로그인한 유저 ID (없으면 1번으로 fallback)
const LOGGED_IN_USER_ID =
    Number(localStorage.getItem('currentUserId')) || 1;


let currentPostId = null;

document.addEventListener('DOMContentLoaded', () => {
    currentPostId = extractPostIdFromPath();

    if (!currentPostId) {
        console.error('유효하지 않은 postId');
        const container = document.getElementById('post-detail-container');
        if (container) {
            container.innerHTML = '<p>유효하지 않은 게시글입니다.</p>';
        }
        return;
    }

    fetchPostDetail(currentPostId);
    fetchComments(currentPostId);

    const submitBtn = document.getElementById('btnCommentSubmit');
    if (submitBtn) {
        submitBtn.addEventListener('click', handleCreateComment);
    }

    const commentListEl = document.getElementById('comment-list');
    if (commentListEl) {
        commentListEl.addEventListener('click', onCommentListClick);
    }
});

// ========================
//  공통 유틸
// ========================
function extractPostIdFromPath() {
    const parts = window.location.pathname.split('/').filter(Boolean);
    if (parts.length < 2) return null;
    const num = Number(parts[1]);
    return Number.isNaN(num) ? null : num;
}

function escapeHtml(str) {
    if (!str) return '';
    return str
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;');
}

// 아바타에 들어갈 이니셜 (닉네임 앞 2글자 정도)
function getInitials(nickname) {
    if (!nickname) return '??';
    const trimmed = nickname.trim();
    if (trimmed.length === 1) return trimmed[0];
    return trimmed.slice(0, 2);
}

// ========================
//  게시글 상세 (네가 이미 쓰던 부분이랑 거의 같으면 이건 생략 가능)
// ========================

const MOOD_LABELS = {
    QUIET: 'QUIET',
    BUZZY: 'BUZZY',
    LOCAL: 'LOCAL',
    TOURISTY: 'TOURISTY',
};

const THEME_LABELS = {
    FOOD: 'FOOD',
    ACCOMMODATION: 'ACCOMMODATION',
    TRANSPORT: 'TRANSPORT',
    PHOTO: 'PHOTO',
    ACTIVITY: 'ACTIVITY',
};

function formatPostDate(isoString) {
    if (!isoString) return '날짜 정보 없음';

    // timeAgo.js에서 전역으로 등록한 함수 사용
    if (window.formatRelativeTime) {
        return window.formatRelativeTime(isoString);
    }

    // 혹시 timeAgo.js가 로드 안 된 예외 상황 대비
    return isoString;
}

async function fetchPostDetail(postId) {
    const container = document.getElementById('post-detail-container');

    try {
        const res = await fetch(`${POST_API_URL}/${postId}`);
        if (!res.ok) throw new Error(`HTTP ${res.status}`);

        const post = await res.json();
        renderPostDetail(post);
    } catch (e) {
        console.error('게시글 상세 불러오기 실패:', e);
        if (container) {
            container.innerHTML = '<p>게시글을 불러오는 중 오류가 발생했습니다.</p>';
        }
    }
}

function renderPostDetail(post) {
    const container = document.getElementById('post-detail-container');
    if (!container) return;

    const displayDate = post.createdAt
        ? timeAgo(post.createdAt)
        : '날짜 정보 없음';

    const contentHtml = (post.content || '').replace(/\n/g, '<br>');

    const moodTag = post.mood
        ? `<span class="tag tag--mood">${MOOD_LABELS[post.mood] || post.mood}</span>`
        : '';

    const themeTags = (post.themes || [])
        .map(t => `<span class="tag tag--theme">${THEME_LABELS[t] || t}</span>`)
        .join('');

    const imageSection = post.imageUrl
        ? `
        <div class="post-detail__image">
            <img src="${post.imageUrl}" alt="post image">
        </div>`
        : '';

    container.innerHTML = `
        <article class="post-detail">
            ${imageSection}

            <header class="post-detail__header">
                <div class="post-detail__title-row">
                    <h1 class="post-detail__title">${post.title || '(제목 없음)'}</h1>
                    <div class="post-detail__actions">
                        <button class="btn btn--secondary post-detail__action-btn" id="btnPostEdit">수정</button>
                        <button class="btn btn--danger post-detail__action-btn" id="btnPostDelete">삭제</button>
                    </div>
                </div>

                <div class="post-detail__tags">
                    ${moodTag}
                    ${themeTags}
                </div>

                <div class="post-detail__meta">
                    <span>${displayDate}</span>
                </div>

                <div class="post-detail__author">
                    By ${post.writerNickname || '익명'}
                </div>
            </header>

            <section class="post-detail__content">
                ${contentHtml}
            </section>
        </article>
    `;

    const btnEdit = document.getElementById('btnPostEdit');
    const btnDelete = document.getElementById('btnPostDelete');

    if (btnEdit) btnEdit.addEventListener('click', onClickEditPost);
    if (btnDelete) btnDelete.addEventListener('click', onClickDeletePost);
}

function onClickEditPost() {
    if (!currentPostId) return;
    window.location.href = `/posts/${currentPostId}/edit`;
}

async function onClickDeletePost() {
    if (!currentPostId) return;

    const ok = window.confirm('정말 이 게시글을 삭제하시겠어요?');
    if (!ok) return;

    try {
        const res = await fetch(`${POST_API_URL}/${currentPostId}`, {
            method: 'DELETE'
        });

        if (!res.ok) throw new Error(`HTTP ${res.status}`);

        window.location.href = '/posts';
    } catch (e) {
        console.error('게시글 삭제 실패:', e);
        alert('게시글 삭제 중 오류가 발생했습니다.');
    }
}

// ========================
//  댓글 목록 조회 & 렌더링
// ========================
async function fetchComments(postId) {
    const listEl = document.getElementById('comment-list');
    if (!listEl) return;

    try {
        const res = await fetch(`/api/v1/posts/${postId}/comments`);
        if (!res.ok) throw new Error(`HTTP ${res.status}`);

        const comments = await res.json(); // CommentResponse[]
        renderComments(comments);
    } catch (e) {
        console.error('댓글 목록 불러오기 실패:', e);
        listEl.innerHTML = '<p>댓글을 불러오는 중 오류가 발생했습니다.</p>';
    }
}

function renderComments(comments) {
    const listEl = document.getElementById('comment-list');
    if (!listEl) return;

    if (!comments || comments.length === 0) {
        listEl.innerHTML = '<p>첫 번째 댓글을 남겨보세요!</p>';
        return;
    }

    listEl.innerHTML = '';

    comments.forEach(comment => {
        const initials = getInitials(comment.writerNickname || '익명');

        const timeText = comment.createdAt ? timeAgo(comment.createdAt) : '';

        const itemHtml = `
            <div class="comment-item" data-id="${comment.id}">
                <div class="comment-avatar">${initials}</div>

                <div class="comment-body">
                    <div class="comment-header-row">
                        <div class="comment-header-main">
                            <span class="comment-author">${comment.writerNickname}</span>
                            <span class="comment-time">${timeText}</span>
                        </div>
                        <div class="comment-actions">
                            <button type="button" class="icon-button comment-edit-btn">
                                <span class="material-icons-outlined">edit</span>
                            </button>
                            <button type="button" class="icon-button icon-button--danger comment-delete-btn">
                                <span class="material-icons-outlined">delete</span>
                            </button>
                        </div>
                    </div>

                    <div class="comment-content-view">
                        ${escapeHtml(comment.content).replace(/\n/g, '<br>')}
                    </div>

                    <div class="comment-edit-area hidden">
                        <textarea class="comment-edit-textarea">${escapeHtml(comment.content)}</textarea>
                        <div class="comment-edit-buttons">
                            <button type="button" class="btn btn--primary comment-save-btn">Save</button>
                            <button type="button" class="btn btn--secondary comment-cancel-btn">Cancel</button>
                        </div>
                    </div>
                </div>
            </div>
        `;

        listEl.insertAdjacentHTML('beforeend', itemHtml);
    });
}

// ========================
//  댓글 작성
// ========================
async function handleCreateComment() {
    if (!currentPostId) return;

    const contentEl = document.getElementById('commentContent');
    const anonymousEl = document.getElementById('commentIsAnonymous');

    const content = contentEl.value.trim();
    if (!content) {
        alert('댓글 내용을 입력해 주세요.');
        return;
    }

    const body = {
        writerId: LOGGED_IN_USER_ID,
        postId: currentPostId,
        content,
        isAnonymous: !!anonymousEl.checked
    };

    try {
        const res = await fetch(`/api/v1/posts/${currentPostId}/comments`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(body)
        });

        if (!res.ok) {
            alert(`댓글 등록 실패 (status: ${res.status})`);
            return;
        }

        contentEl.value = '';
        anonymousEl.checked = false;

        fetchComments(currentPostId);
    } catch (e) {
        console.error('댓글 등록 오류:', e);
        alert('댓글 등록 중 오류가 발생했습니다.');
    }
}

// ========================
//  댓글 수정/삭제 - 인라인 편집
// ========================
function onCommentListClick(e) {
    const target = e.target;
    const itemEl = target.closest('.comment-item');
    if (!itemEl) return;

    const commentId = itemEl.dataset.id;

    const editBtn   = target.closest('.comment-edit-btn');
    const deleteBtn = target.closest('.comment-delete-btn');
    const saveBtn   = target.closest('.comment-save-btn');
    const cancelBtn = target.closest('.comment-cancel-btn');

    if (editBtn) {
        enterEditMode(itemEl);
    } else if (deleteBtn) {
        onClickDeleteComment(commentId);
    } else if (saveBtn) {
        onClickSaveComment(commentId, itemEl);
    } else if (cancelBtn) {
        exitEditMode(itemEl);
    }

}

function enterEditMode(itemEl) {
    const viewEl = itemEl.querySelector('.comment-content-view');
    const editArea = itemEl.querySelector('.comment-edit-area');
    const textarea = itemEl.querySelector('.comment-edit-textarea');

    if (!viewEl || !editArea || !textarea) return;

    // 보기/수정 토글
    viewEl.classList.add('hidden');
    editArea.classList.remove('hidden');

    // textarea 내용은 render할 때 이미 넣어둠 (혹시 모를 sync용)
    textarea.focus();
}

function exitEditMode(itemEl) {
    const viewEl = itemEl.querySelector('.comment-content-view');
    const editArea = itemEl.querySelector('.comment-edit-area');

    if (!viewEl || !editArea) return;

    editArea.classList.add('hidden');
    viewEl.classList.remove('hidden');
}

async function onClickSaveComment(commentId, itemEl) {
    const textarea = itemEl.querySelector('.comment-edit-textarea');
    if (!textarea) return;

    const newText = textarea.value.trim();
    if (!newText) {
        alert('내용이 비어 있습니다.');
        return;
    }

    const body = { content: newText };

    try {
        const res = await fetch(`/api/v1/posts/${currentPostId}/comments/${commentId}`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(body)
        });

        if (!res.ok) {
            alert(`댓글 수정 실패 (status: ${res.status})`);
            return;
        }

        // 성공하면 목록 다시 조회
        fetchComments(currentPostId);
    } catch (e) {
        console.error('댓글 수정 오류:', e);
        alert('댓글 수정 중 오류가 발생했습니다.');
    }
}

async function onClickDeleteComment(commentId) {
    const ok = window.confirm('이 댓글을 삭제하시겠어요?');
    if (!ok) return;

    try {
        const res = await fetch(`/api/v1/posts/${currentPostId}/comments/${commentId}`, {
            method: 'DELETE'
        });

        if (!res.ok) {
            alert(`댓글 삭제 실패 (status: ${res.status})`);
            return;
        }

        fetchComments(currentPostId);
    } catch (e) {
        console.error('댓글 삭제 오류:', e);
        alert('댓글 삭제 중 오류가 발생했습니다.');
    }
}
