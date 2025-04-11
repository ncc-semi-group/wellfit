$(document).ready(function() {
    // 뒤로가기 버튼 클릭 이벤트
    $('.back-btn').click(function() {
        window.location.href = '/mypage';
    });

    // 게시물 클릭 시 상세 페이지로 이동
    $('.post-item').click(function() {
        var postId = $(this).data('post-id');
        window.location.href = '/board/detail/' + postId;
    });
});