$(document).ready(function() {
    // 게시물 클릭 시 상세 페이지로 이동
    $('.post-content').click(function() {
        var postId = $(this).data('post-id');
        window.location.href = '/board/detail/' + postId;
    });

    // 좋아요 버튼 클릭 이벤트
    $('.like-button').click(function(e) {
        e.stopPropagation(); // 상위 요소로의 이벤트 전파 방지
        var $button = $(this);
        var $icon = $button.find('i');
        var $count = $button.find('span');
        
        if ($icon.hasClass('bi-heart')) {
            $icon.removeClass('bi-heart').addClass('bi-heart-fill');
            $count.text(parseInt($count.text()) + 1);
        } else {
            $icon.removeClass('bi-heart-fill').addClass('bi-heart');
            $count.text(parseInt($count.text()) - 1);
        }
    });

    // 댓글 버튼 클릭 이벤트
    $('.comment-button').click(function(e) {
        e.stopPropagation(); // 상위 요소로의 이벤트 전파 방지
        // TODO: 댓글 기능 구현
    });
}); 