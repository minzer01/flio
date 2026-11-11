// src/main/resources/static/js/post-edit.js

const POST_API_URL = '/api/v1/posts';

let currentPostId = null;

document.addEventListener('DOMContentLoaded', () => {
    currentPostId = extractPostIdFromPath();

    if (!currentPostId) {
        alert('잘못된 접근입니다.');
        window.location.href = '/posts';
        return;
    }

    const moodGroup = document.getElementById('moodGroup');
    const themeGroup = document.getElementById('themeGroup');

    setupMoodSelection(moodGroup);
    setupThemeSelection(themeGroup);

    // 기존 데이터 불러오기
    loadPostForEdit(currentPostId);
});

// URL: /posts/{id}/edit 에서 {id} 뽑아내기
function extractPostIdFromPath() {
    const segments = window.location.pathname.split('/').filter(Boolean);
    // 예: "/posts/9/edit" -> ["posts","9","edit"]
    if (segments.length < 3) return null;
    const idStr = segments[1];
    const num = Number(idStr);
    return Number.isNaN(num) ? null : num;
}

// Mood 단일 선택
function setupMoodSelection(groupEl) {
    if (!groupEl) return;
    const chips = Array.from(groupEl.querySelectorAll('.choice-chip'));
    chips.forEach(chip => {
        chip.addEventListener('click', () => {
            chips.forEach(c => c.classList.remove('choice-chip--active'));
            chip.classList.add('choice-chip--active');
        });
    });
}

// Theme 다중 선택
function setupThemeSelection(groupEl) {
    if (!groupEl) return;
    const chips = Array.from(groupEl.querySelectorAll('.choice-chip'));
    chips.forEach(chip => {
        chip.addEventListener('click', () => {
            chip.classList.toggle('choice-chip--active');
        });
    });
}

function getSelectedMood() {
    const active = document.querySelector('#moodGroup .choice-chip--active');
    return active ? active.dataset.value : null;
}

function getSelectedThemes() {
    const actives = Array.from(
        document.querySelectorAll('#themeGroup .choice-chip--active')
    );
    return actives.map(chip => chip.dataset.value);
}

// 1) 기존 게시글 정보 불러와서 폼에 세팅
async function loadPostForEdit(postId) {
    try {
        const res = await fetch(`${POST_API_URL}/${postId}`);
        if (!res.ok) {
            alert(`게시글 정보를 불러오지 못했습니다. (status: ${res.status})`);
            return;
        }

        const post = await res.json();

        // 기본 필드
        document.getElementById('title').value = post.title || '';
        document.getElementById('content').value = post.content || '';
        document.getElementById('country').value = post.country || '';
        document.getElementById('imageUrl').value = post.imageUrl || '';
        document.getElementById('isAnonymous').checked = !!post.isAnonymous;

        // mood
        if (post.mood) {
            const moodBtn = document.querySelector(
                `#moodGroup .choice-chip[data-value="${post.mood}"]`
            );
            if (moodBtn) {
                document
                    .querySelectorAll('#moodGroup .choice-chip')
                    .forEach(c => c.classList.remove('choice-chip--active'));
                moodBtn.classList.add('choice-chip--active');
            }
        }

        // themes (다중)
        if (post.themes && Array.isArray(post.themes)) {
            document
                .querySelectorAll('#themeGroup .choice-chip')
                .forEach(c => {
                    if (post.themes.includes(c.dataset.value)) {
                        c.classList.add('choice-chip--active');
                    } else {
                        c.classList.remove('choice-chip--active');
                    }
                });
        }

    } catch (err) {
        console.error('loadPostForEdit error:', err);
        alert('게시글 정보를 불러오는 중 오류가 발생했습니다.');
    }
}

// 2) "Save Changes" 버튼 클릭 시 호출
async function handleEditPost() {
    if (!currentPostId) {
        alert('잘못된 게시글 ID입니다.');
        return;
    }

    const title = document.getElementById('title').value.trim();
    const content = document.getElementById('content').value.trim();

    if (!title || !content) {
        alert('제목과 내용을 입력해 주세요.');
        return;
    }

    const country = document.getElementById('country').value.trim() || null;
    const imageUrl = document.getElementById('imageUrl').value.trim() || null;
    const isAnonymous = document.getElementById('isAnonymous').checked;

    const updateData = {
        title,
        content,
        country,
        mood: getSelectedMood(),
        themes: getSelectedThemes(),
        isAnonymous,
        imageUrl
    };

    console.log('update body:', updateData);

    try {
        const res = await fetch(`${POST_API_URL}/${currentPostId}`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(updateData)
        });

        if (!res.ok) {
            alert(`게시글 수정 실패 (status: ${res.status})`);
            return;
        }

        // 성공 시 상세 페이지로 이동
        window.location.href = `/posts/${currentPostId}`;
    } catch (err) {
        console.error('handleEditPost error:', err);
        alert('게시글 수정 중 오류가 발생했습니다.');
    }
}
