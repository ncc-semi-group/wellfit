document.addEventListener('DOMContentLoaded', function() {
    // 팔로우/언팔로우 버튼 클릭 이벤트 처리
    document.querySelectorAll('.follow-btn').forEach(button => {
        button.addEventListener('click', function() {
            const userId = this.getAttribute('data-user-id');
            const isFollowing = this.classList.contains('following');
            
            if (confirm(isFollowing ? '팔로우를 취소하시겠습니까?' : '팔로우하시겠습니까?')) {
                // 팔로우/언팔로우 API 호출
                fetch(`/api/follow/${userId}`, {
                    method: isFollowing ? 'DELETE' : 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                })
                .then(response => {
                    if (response.ok) {
                        // 버튼 상태 토글
                        this.classList.toggle('following');
                        this.textContent = isFollowing ? '팔로우' : '팔로잉';
                        
                        // 팔로잉 취소 시 목록에서 제거
                        if (isFollowing) {
                            this.closest('.following-item').remove();
                            
                            // 팔로잉 카운트 업데이트
                            const countElement = document.querySelector('.following-count');
                            const currentCount = parseInt(countElement.textContent.match(/\d+/)[0]);
                            countElement.textContent = `팔로잉 ${currentCount - 1}`;
                        }
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('작업을 수행하는 중 오류가 발생했습니다.');
                });
            }
        });
    });

    // 뒤로가기 버튼 클릭 이벤트
    document.querySelector('.back-btn').addEventListener('click', function() {
        history.back();
    });
}); 