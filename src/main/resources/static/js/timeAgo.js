// src/main/resources/static/js/timeAgo.js

function timeAgo(isoString) {
    if (!isoString) return '';

    const date = new Date(isoString);
    const now = new Date();

    const diffMs = now - date;
    const sec = Math.floor(diffMs / 1000);
    const min = Math.floor(sec / 60);
    const hour = Math.floor(min / 60);
    const day = Math.floor(hour / 24);

    if (day >= 5) {
        const y = date.getFullYear();
        const m = String(date.getMonth() + 1).padStart(2, '0');
        const d = String(date.getDate()).padStart(2, '0');
        return `${y}-${m}-${d}`;
    }
    if (day >= 1) return `${day}일 전`;
    if (hour >= 1) return `${hour}시간 전`;
    if (min >= 1) return `${min}분 전`;
    return '방금 전';
}
