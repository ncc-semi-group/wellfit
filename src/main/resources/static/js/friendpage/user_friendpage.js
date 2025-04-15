document.addEventListener("DOMContentLoaded", function() {
    // 친구 항목 클릭 이벤트
    document.querySelectorAll('.friend-wrapper').forEach(item => {
        item.addEventListener('click', function() {
            const userId = this.getAttribute('data-user-id');
            if (userId) {
                window.location.href = `/userpage/${userId}`;
            }
        });
    });

    // 뒤로가기 버튼 클릭 이벤트
    const backBtn = document.querySelector('.back-btn');
    if (backBtn) {
        backBtn.addEventListener('click', function() {
            window.history.back();
        });
    }
}); 