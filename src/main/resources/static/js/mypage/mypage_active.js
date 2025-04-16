$(document).ready(function() {
	// 뒤로가기 버튼 클릭 이벤트
	$('.back-btn').click(function() {
		window.location.href = '/mypage';
	});
	
	// 내 댓글 탭 클릭 이벤트
	$('.tab2').click(function() {
		var userId = $(this).attr('data-user-id');
		if (userId) {
			window.location.href = '/mypageComment?id=' + userId;
	    } else {
			alert('로그인이 필요합니다.');
			window.location.href = '/loginpage';
	    }
	});
});