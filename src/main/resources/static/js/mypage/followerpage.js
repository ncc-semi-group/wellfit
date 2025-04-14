document.addEventListener('DOMContentLoaded', function() {
    // 팔로우/언팔로우 버튼 클릭 이벤트 처리
    document.querySelectorAll('.follow-btn').forEach(button => {
        button.addEventListener('click', function() {
            const userId = this.getAttribute('data-user-id');
            const isFollowing = this.classList.contains('following');
            
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
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('작업을 수행하는 중 오류가 발생했습니다.');
            });
        });
    });

    // 뒤로가기 버튼 클릭 이벤트
    document.querySelector('.back-btn').addEventListener('click', function() {
        history.back();
    });
}); 