// 뒤로가기 버튼 기능
document.addEventListener('DOMContentLoaded', function() {
    const backBtn = document.querySelector('.back-btn');
    if (backBtn) {
        backBtn.addEventListener('click', function() {
            history.back();
        });
    }
}); 