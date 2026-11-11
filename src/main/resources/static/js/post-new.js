// src/main/resources/static/js/post-new.js

const POST_API_URL = '/api/v1/posts';
// 로그인 붙기 전까지 임시 writerId
const TEMP_WRITER_ID = 1;

document.addEventListener('DOMContentLoaded', () => {
    const moodGroup = document.getElementById('moodGroup');
    const themeGroup = document.getElementById('themeGroup');

    setupMoodSelection(moodGroup);
    setupThemeSelection(themeGroup);

    console.log('post-new.js loaded');
});

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
    return active ? active.dataset.value : null; // QUIET / BUZZY ...
}

function getSelectedThemes() {
    const actives = Array.from(
        document.querySelectorAll('#themeGroup .choice-chip--active')
    );
    return actives.map(chip => chip.dataset.value); // ["PHOTO","ACTIVITY",...]
}

// 🔥 버튼에서 직접 호출: onclick="handleCreatePost()"
async function handleCreatePost() {
    const titleEl = document.getElementById('title');
    const contentEl = document.getElementById('content');
    const countryEl = document.getElementById('country');
    const imageUrlEl = document.getElementById('imageUrl');
    const isAnonymousEl = document.getElementById('isAnonymous');

    const title = titleEl.value.trim();
    const content = contentEl.value.trim();

    if (!title || !content) {
        alert('제목과 내용을 입력해 주세요.');
        return;
    }

    const body = {
        writerId: TEMP_WRITER_ID,
        title,
        content,
        country: countryEl.value.trim() || null,
        mood: getSelectedMood(),
        themes: getSelectedThemes(),
        isAnonymous: !!isAnonymousEl.checked,
        imageUrl: imageUrlEl.value.trim() || null
    };

    console.log('create body:', body);

    try {
        const response = await fetch(POST_API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(body)
        });

        if (!response.ok) {
            alert(`게시글 생성 실패 (status: ${response.status})`);
            return;
        }

        const created = await response.json(); // PostResponse
        if (created && created.id) {
            window.location.href = `/posts/${created.id}`;
        } else {
            window.location.href = '/posts';
        }
    } catch (err) {
        console.error('create error:', err);
        alert('게시글 생성 중 오류가 발생했습니다.');
    }
}
