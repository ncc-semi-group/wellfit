$(document).ready(function () {

    // 운동 관련 합계 변수
    let totalTime = 0;
    let totalKcal = 0;

    // 날짜
    const year = $('.title').data('year');
    const month = $('.title').data('month');
    const day = $('.title').data('day');

    // exercise-item이 없을 경우
    function checkExerciseItem() {
        if ($('.exercise-item').length === 0) {
            // 이전 페이지 url
            const previousUrl = document.referrer;
            // 이전 페이지가 운동 기록 페이지인 경우
            if (previousUrl.includes('/record/exercise_form')) {
                // 기록 페이지로 이동
                location.href = `/record?year=${year}&month=${month}&day=${day}`;
            } else {
                // 운동 기록 페이지로 이동
                location.href = '/record/exercise_form';
            }
        }
    }
    checkExerciseItem();


    // 운동 관련 합계 계산
    $.each($('.exercise-item'), function () {
        // exercise-item의 자식 요소
        const exerciseInfo = $(this).find('.exercise-info');
        totalTime += parseInt(exerciseInfo.attr('data-exercise_time'));
        totalKcal += parseInt(exerciseInfo.attr('data-kcal'));
    });

    // 초기 정보 표시
    updateInfo();

    // 표시 될 데이터 업데이트
    function updateInfo() {
        $('.exercise-value.time-val').text(`${totalTime}분`);
        $('.exercise-value.kcal-val').text(`${totalKcal}kcal`);
    }

    // 뒤로가기 버튼 클릭 이벤트
    $('.back-button').click(function () {
        location.href = `/record?year=${year}&month=${month}&day=${day}`;
    });

    // 완료 버튼 클릭 이벤트
    $('.complete-button').click(function () {
        location.href = `/record?year=${year}&month=${month}&day=${day}`;
    });

    // 추가 버튼 클릭 이벤트
    $('.add-button').click(function () {
        // 운동 추가 페이지로 이동
        location.href = '/record/exercise_form';
    });

    // 삭제 버튼 클릭 이벤트 처리
    $('.remove-button').click(function () {
        const exerciseItem = $(this).closest('.exercise-item');
        const exerciseInfo = exerciseItem.find('.exercise-info');
        const exerciseId = exerciseInfo.attr('data-id');

        // 데이터 변경
        totalTime -= parseInt(exerciseInfo.attr('data-exercise_time'));
        totalKcal -= parseInt(exerciseInfo.attr('data-kcal'));

        // 운동 항목 삭제
        exerciseItem.fadeOut(300, function () {
            $(this).remove();
            // 삭제 요청
            deleteExerciseRecord(exerciseId);
            // exercise-item이 없을 경우

            // 화면 업데이트
            updateInfo();
        });
    });

    function deleteExerciseRecord(exerciseId) {
        // AJAX 요청
        $.ajax({
            url: '/api/record/delete_exercise',
            type: 'POST',
            data: {exerciseId: exerciseId, burnedKcal: totalKcal, exerciseTime: totalTime},
            success: function (response) {
                window.showToast(response);
                checkExerciseItem();
            },
            error: function (xhr, status, error) {
                if (xhr.status === 401) {
                    alert(xhr.responseText);
                    window.location.href = '/loginpage';
                    return;
                } else if (xhr.status === 403) {
                    window.showToast(xhr.responseText);
                    // 2초 후 새로고침
                    setTimeout(function () {
                        location.reload();
                    }, 2000);
                    return;
                }
                window.showToast('운동 기록 삭제를 실패했습니다. 다시 시도해주세요.');
                console.error('Error:', error);
            }
        });

    };
});