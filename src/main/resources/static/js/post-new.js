// src/main/resources/static/js/post-new.js

const POST_API_URL = '/api/v1/posts';
const IMAGE_UPLOAD_URL = '/api/v1/images/upload';

document.addEventListener('DOMContentLoaded', () => {
    const moodGroup = document.getElementById('moodGroup');
    const themeGroup = document.getElementById('themeGroup');

    setupMoodSelection(moodGroup);
    setupThemeSelection(themeGroup);
    setupImageUpload();

    console.log('post-new.js loaded');
});

// 이미지 업로드 설정
function setupImageUpload() {
    const fileInput = document.getElementById('imageFile');
    const previewContainer = document.getElementById('image-preview');
    const previewImg = document.getElementById('preview-img');

    fileInput?.addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (file) {
            // 미리보기 표시
            const reader = new FileReader();
            reader.onload = (event) => {
                previewImg.src = event.target.result;
                previewContainer.style.display = 'block';
            };
            reader.readAsDataURL(file);
        } else {
            previewContainer.style.display = 'none';
            previewImg.src = '';
        }
    });
}

// 이미지 S3 업로드
async function uploadImage(file) {
    const token = localStorage.getItem('accessToken');
    const formData = new FormData();
    formData.append('file', file);

    const response = await fetch(IMAGE_UPLOAD_URL, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`
        },
        body: formData
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.error || '이미지 업로드 실패');
    }

    const result = await response.json();
    return result.imageUrl;
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

// 게시글 생성
async function handleCreatePost() {
    const titleEl = document.getElementById('title');
    const contentEl = document.getElementById('content');
    const countryEl = document.getElementById('country');
    const imageFileEl = document.getElementById('imageFile');
    const isAnonymousEl = document.getElementById('isAnonymous');
    const imageHelper = document.getElementById('image-helper');

    const title = titleEl.value.trim();
    const content = contentEl.value.trim();

    if (!title || !content) {
        alert('제목과 내용을 입력해 주세요.');
        return;
    }

    const token = localStorage.getItem('accessToken');
    if (!token) {
        alert('로그인이 필요합니다.');
        window.location.href = '/login';
        return;
    }

    let imageUrl = null;

    // 이미지 파일이 선택되었으면 업로드
    const file = imageFileEl?.files[0];
    if (file) {
        try {
            imageHelper.textContent = '이미지 업로드 중...';
            imageUrl = await uploadImage(file);
            imageHelper.textContent = '이미지 업로드 완료!';
        } catch (err) {
            console.error('Image upload error:', err);
            alert('이미지 업로드에 실패했습니다: ' + err.message);
            imageHelper.textContent = '이미지 업로드 실패';
            return;
        }
    }

    const body = {
        title,
        content,
        country: countryEl.value.trim() || null,
        mood: getSelectedMood(),
        themes: getSelectedThemes(),
        isAnonymous: !!isAnonymousEl.checked,
        imageUrl
    };

    console.log('create body:', body);

    try {
        const response = await fetch(POST_API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(body)
        });

        if (response.status === 401 || response.status === 403) {
            alert('로그인이 필요합니다.');
            window.location.href = '/login';
            return;
        }

        if (!response.ok) {
            alert(`게시글 생성 실패 (status: ${response.status})`);
            return;
        }

        const created = await response.json();
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
