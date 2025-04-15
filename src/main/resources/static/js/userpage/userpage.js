$(document).ready(function() {
    
    // 뱃지 버튼 클릭 이벤트
    $('.box-title2').click(function() {
        var userId = $(this).data('user-id');
        if (userId) {
            window.location.href = '/userpageBadge/' + userId;
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

    // 캘린더 초기화
    const userId = $('.box-title2').data('user-id');
    let currentDate = new Date();
    
    function renderCalendar() {
        const year = currentDate.getFullYear();
        const month = currentDate.getMonth() + 1;
        
        console.log('Fetching calendar data for:', { userId, year, month });
        
        fetch(`/api/daily-statistics/${userId}/monthly?year=${year}&month=${month}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(statistics => {
                console.log('Calendar data received:', statistics);
                const calendar = $('#calendar');
                calendar.empty();
                
                // 요일 헤더 추가
                const days = ['일', '월', '화', '수', '목', '금', '토'];
                days.forEach(day => {
                    calendar.append($('<div>')
                        .addClass('calendar-day-header')
                        .text(day));
                });
                
                // 달력 날짜 채우기
                const firstDay = new Date(year, month - 1, 1);
                const lastDay = new Date(year, month, 0);
                const prevLastDay = new Date(year, month - 1, 0);
                
                // 이전 달의 마지막 날짜들
                for (let i = 0; i < firstDay.getDay(); i++) {
                    const prevDate = prevLastDay.getDate() - firstDay.getDay() + i + 1;
                    calendar.append($('<div>')
                        .addClass('calendar-day other-month')
                        .append($('<span>')
                            .addClass('date-number')
                            .text(prevDate)));
                }
                
                // 현재 달의 날짜들
                for (let date = 1; date <= lastDay.getDate(); date++) {
                    const dayDiv = $('<div>').addClass('calendar-day');
                    const dateSpan = $('<span>').addClass('date-number').text(date);
                    dayDiv.append(dateSpan);
                    
                    const dateStr = `${year}-${String(month).padStart(2, '0')}-${String(date).padStart(2, '0')}`;
                    const dayStats = statistics.find(s => s.date === dateStr);
                    
                    if (dayStats && (dayStats.is_achieved === true || dayStats.is_achieved === 1)) {
                        console.log('Adding badge for date:', dateStr);
                        const badge = $('<img>')
                            .addClass('success-badge')
                            .attr('src', '/images/성공.png')
                            .attr('alt', '성공')
                            .on('error', function() {
                                console.error('Failed to load image:', this.src);
                            })
                            .on('load', function() {
                                console.log('Successfully loaded image:', this.src);
                            });
                        dayDiv.append(badge);
                    }
                    
                    calendar.append(dayDiv);
                }
                
                // 다음 달의 시작 날짜들
                const remainingDays = 7 - ((firstDay.getDay() + lastDay.getDate()) % 7); // 한 줄만 채우기
                for (let i = 1; i <= (remainingDays === 7 ? 0 : remainingDays); i++) {
                    calendar.append($('<div>')
                        .addClass('calendar-day other-month')
                        .append($('<span>')
                            .addClass('date-number')
                            .text(i)));
                }
            })
            .catch(error => {
                console.error('Error fetching calendar data:', error);
            });
            
        $('#currentMonth').text(`${currentDate.getFullYear()}년 ${currentDate.getMonth() + 1}월`);
    }
    
    // 이전/다음 월 버튼 이벤트
    $('#prevMonth').click(() => {
        currentDate.setMonth(currentDate.getMonth() - 1);
        renderCalendar();
    });
    
    $('#nextMonth').click(() => {
        currentDate.setMonth(currentDate.getMonth() + 1);
        renderCalendar();
    });
    
    // 초기 캘린더 렌더링
    renderCalendar();

    // 뒤로가기 버튼 클릭 이벤트
    $('.back-btn').click(function() {
        history.back();
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
	                userId: targetUserId
	            }),
	            success: function (res) {
	                console.log("✅ 입장 처리 성공:", res);
	                window.location.href = '/chatroom/enter/' + res.roomId + '?userId=' + currentUserId;
	            },
	            error: function (xhr) {
	                console.error("❌ 입장 실패:", xhr.responseText);
	                alert('채팅방 입장 실패: ' + xhr.responseText);
	            }
	        });
	    });
	}); // $(document).ready 종료

	// DOMContentLoaded 이벤트 리스너를 밖으로 이동
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
		
		// 1대1 채팅 버튼 클릭 이벤트
		    $('.chat').click(function() {
		        const targetUserId = $('.friend-link').data('user-id');
		        const currentUserId = $('.box-title2').data('user-id');
		        
		        if (!targetUserId || !currentUserId) {
		            alert('사용자 정보를 찾을 수 없습니다.');
		            return;
		        }

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
		});