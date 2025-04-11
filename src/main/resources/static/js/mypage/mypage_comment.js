$(document).ready(function() {
    // 뒤로가기 버튼 클릭 이벤트
    $('.back-btn').click(function(){
        window.location.href = '/mypage';
    });

    // 탭1(내 게시물) 클릭 이벤트
    $('.tab1').click(function() {
		var userId = $(this).attr('data-user-id');
        if (userId) {
            window.location.href = '/mypageActive?id=' + userId;
        } else {
            alert('로그인이 필요합니다.');
            window.location.href = '/login';
        }
    });
});