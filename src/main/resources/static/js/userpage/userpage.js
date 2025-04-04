$(document).ready(function() {
    var userId = $('#calendar').data('user-id');
    
    // 뱃지 버튼 클릭 이벤트
    $('.box-title2').click(function() {
        var userId = $(this).data('user-id');
        if (userId) {
            window.location.href = '/userpageBadge/' + userId;
        } else {
            alert('사용자 정보를 찾을 수 없습니다.');
        }
    });

    // 캘린더 초기화
    $('#calendar').fullCalendar({
        header: {
            left: 'prev',
            center: 'title',
            right: 'next'
        },
        defaultView: 'month',
        locale: 'ko',
        height: 'auto',
        contentHeight: 'auto',
        events: function(start, end, timezone, callback) {
            $.ajax({
                url: '/api/userExercises/' + userId,
                type: 'GET',
                data: {
                    start: start.format('YYYY-MM-DD'),
                    end: end.format('YYYY-MM-DD')
                },
                success: function(response) {
                    var events = response.map(function(exercise) {
                        return {
                            title: exercise.exerciseName,
                            start: exercise.exerciseDate,
                            className: 'exercise-event'
                        };
                    });
                    callback(events);
                }
            });
        },
        eventClick: function(event) {
            window.location.href = '/exercise/detail/' + event.id;
        },
        dayClick: function(date) {
            var formattedDate = date.format('YYYY-MM-DD');
            window.location.href = '/userExercises/' + userId + '/date/' + formattedDate;
        }
    });

    // 뒤로가기 버튼 클릭 이벤트
    $('.back-btn').click(function() {
        history.back();
    });
});