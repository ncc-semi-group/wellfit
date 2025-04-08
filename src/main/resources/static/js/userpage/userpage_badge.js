$(document).ready(function () {
    $('.box-title1').click(function () {
        var userId = $(this).attr('data-user-id');
        if (userId) {
            window.location.href = '/userpage/' + userId;
        } else {
            alert('사용자 정보를 찾을 수 없습니다.');
        }
    });
	
	// 친구 수 클릭 시 '/friendpage'로 이동
	$('#friend-link').click(function() {
	    window.location.href = '/friendpage';
	});
});
