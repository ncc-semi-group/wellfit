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
});