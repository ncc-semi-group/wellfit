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
		
		    // 친구 상태 확인 및 버튼 업데이트
		    function updateFriendButton() {
		        const userId = $('.friend-link').data('user-id');
		        if (userId) {
		            fetch(`/api/friends/check/${userId}`)
		                .then(response => response.json())
		                .then(isFriend => {
		                    const friendButton = $('.addfriend');
		                    if (isFriend) {
		                        friendButton.text('친구✓');
		                        friendButton.addClass('is-friend');
		                    } else {
		                        friendButton.text('친구추가');
		                        friendButton.removeClass('is-friend');
		                    }
		                })
		                .catch(error => console.error('친구 상태 확인 중 오류:', error));
		        }
		    }

		    // 페이지 로드 시 친구 상태 확인
		    updateFriendButton();

		    // 친구 추가/삭제 버튼 클릭 이벤트
		    $('.addfriend').click(function() {
		        const userId = $('.friend-link').data('user-id');
		        if (!userId) {
		            alert('사용자 정보를 찾을 수 없습니다.');
		            return;
		        }

		        const isFriend = $(this).hasClass('is-friend');
		        if (isFriend) {
		            // 친구 삭제
		            if (confirm('친구를 삭제하시겠습니까?')) {
		                fetch(`/api/friends/delete/${userId}`, {
		                    method: 'DELETE'
		                })
		                .then(response => {
		                    if (!response.ok) {
		                        return response.text().then(text => { throw new Error(text) });
		                    }
		                    return response.text();
		                })
		                .then(message => {
		                    alert(message);
		                    // 친구 수 감소
		                    const friendCountElement = $('.friend-link .stat-number');
		                    const currentCount = parseInt(friendCountElement.text());
		                    friendCountElement.text(currentCount - 1);
		                    // 버튼 상태 업데이트
		                    updateFriendButton();
		                })
		                .catch(error => {
		                    alert(error.message);
		                });
		            }
		        } else {
		            // 친구 추가
		            fetch('/api/friends/add', {
		                method: 'POST',
		                headers: {
		                    'Content-Type': 'application/json',
		                },
		                body: JSON.stringify({
		                    userId: userId
		                })
		            })
		            .then(response => {
		                if (!response.ok) {
		                    return response.text().then(text => { throw new Error(text) });
		                }
		                return response.text();
		            })
		            .then(message => {
		                alert(message);
		                // 버튼 상태 업데이트
		                updateFriendButton();
		            })
		            .catch(error => {
		                alert(error.message);
		            });
		        }
		    });

		    // 팔로우 상태 확인 및 버튼 업데이트
		    function updateFollowButton() {
		        const userId = $('.friend-link').data('user-id');
		        if (userId) {
		            fetch(`/api/follow/check/${userId}`)
		                .then(response => response.json())
		                .then(isFollowing => {
		                    const followButton = $('.follow');
		                    if (isFollowing) {
		                        followButton.text('팔로잉');
		                        followButton.addClass('is-following');
		                    } else {
		                        followButton.text('팔로우');
		                        followButton.removeClass('is-following');
		                    }
		                })
		                .catch(error => console.error('팔로우 상태 확인 중 오류:', error));
		        }
		    }

		    // 페이지 로드 시 팔로우 상태 확인
		    updateFollowButton();

		    // 팔로우/언팔로우 버튼 클릭 이벤트
		    $('.follow').click(function() {
		        const userId = $('.friend-link').data('user-id');
		        if (!userId) {
		            alert('사용자 정보를 찾을 수 없습니다.');
		            return;
		        }

		        fetch(`/api/follow/toggle/${userId}`, {
		            method: 'POST'
		        })
		        .then(response => {
		            if (!response.ok) {
		                return response.text().then(text => { throw new Error(text) });
		            }
		            return response.text();
		        })
		        .then(message => {
		            alert(message);
		            // 팔로잉/팔로워 수 업데이트
		            const isNowFollowing = message === "팔로우했습니다.";
		            const followerCountElement = $('#follower .stat-number');
		            const currentCount = parseInt(followerCountElement.text());
		            followerCountElement.text(isNowFollowing ? currentCount + 1 : currentCount - 1);
		            // 버튼 상태 업데이트
		            updateFollowButton();
		        })
		        .catch(error => {
		            alert(error.message);
		        });
		    });
		});

		document.addEventListener('DOMContentLoaded', function() {
		    // 친구 목록 클릭 이벤트
		    const friendLink = document.querySelector('.friend-link');
		    if (friendLink) {
		        friendLink.addEventListener('click', function() {
		            const userId = this.getAttribute('data-user-id');
		            if (userId) {
		                window.location.href = '/userpage/friends/' + userId;
		            }
		        });
		    }

		    // 팔로워 클릭 이벤트
		    const followerBtn = document.getElementById('follower');
		    if (followerBtn) {
		        followerBtn.addEventListener('click', function() {
		            const userId = document.querySelector('.friend-link').getAttribute('data-user-id');
		            window.location.href = `/userpage/follower/${userId}`;
		        });
		    }

		    // 팔로잉 클릭 이벤트
		    const followingBtn = document.getElementById('following');
		    if (followingBtn) {
		        followingBtn.addEventListener('click', function() {
		            const userId = document.querySelector('.friend-link').getAttribute('data-user-id');
		            window.location.href = `/userpage/following/${userId}`;
		        });
		    }
		    
		    // 뒤로가기 버튼 클릭 이벤트
		    const backBtn = document.querySelector('.back-btn');
		    if (backBtn) {
		        backBtn.addEventListener('click', function() {
		            window.history.back();
		        });
		    }
});

// 1대1 채팅 버튼 클릭 이벤트
$('.chat').click(function() {
    const targetUserId = $('.friend-link').data('user-id');
    const currentUserId = $('.box-title2').data('user-id');
    
    if (!targetUserId || !currentUserId) {
        alert('사용자 정보를 찾을 수 없습니다.');
        return;
    }

    // 1대1 채팅방 생성 또는 입장
	$.ajax({
	    url: '/chatroom/create/1vs1',
	    type: 'POST',
	    contentType: 'application/json',
	    data: JSON.stringify({
	        userId: targetUserId,
	    }),
	    success: function (res) {
	        console.log("✅ 입장 처리 성공:", res);
	
	        // 2. DB 등록 성공 시, 채팅방으로 이동
	        window.location.href = '/chatroom/enter/ + res.roomId'
	    },
	    error: function (xhr) {
	        console.error("❌ 입장 실패:", xhr.responseText);
	        alert('채팅방 입장 실패: ' + xhr.responseText);
	    }
	});
});
