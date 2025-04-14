$(document).ready(function () {
    $('.box-title1').click(function () {
        var userId = $(this).attr('data-user-id');
        if (userId) {
            window.location.href = '/userpage/' + userId;
        } else {
            alert('사용자 정보를 찾을 수 없습니다.');
        }
    });
	
	// 친구 수 클릭 시 해당 유저의 친구 목록 페이지로 이동
	$('.friend-link').click(function() {
	    var userId = $(this).data('user-id');
	    if (userId) {
	        window.location.href = '/userpage/friends/' + userId;
	    } else {
	        alert('사용자 정보를 찾을 수 없습니다.');
	    }
	});
	
	$('#follower').click(function() {
			var userId = $('.friend-link').data('user-id');
			if (userId) {
				window.location.href = '/userpage/follower/' + userId;
			} else {
				alert('사용자 정보를 찾을 수 없습니다.');
			}
		});
			
		$('#following').click(function() {
			var userId = $('.friend-link').data('user-id');
			if (userId) {
				window.location.href = '/userpage/following/' + userId;
			} else {
				alert('사용자 정보를 찾을 수 없습니다.');
			}
		});
});
