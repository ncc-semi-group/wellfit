$(document).ready(function () {

    // 모달 표시하기
    $('.exercise-item').click(function () {
        $('.intensity-input-container').show();
        $('.intensity-unit-container').show();

        // 선택값 초기화
        $.each($('.intensity-button'), function (i, e) {
            if ($(e).hasClass('active')) $(e).removeClass('active');
        });
        $('#defaultActive').addClass('active');

        let exerciseName = '';
        let checkFree = '';
        const weight = parseFloat($('.exercise-list').data('weight'));
        let exerciseMETs = 0;
        let finalMETs =  0;
        exerciseName = $(this).find('.exercise-name').text();
        // 운동 이름 표시
        $('#exerciseName').text(exerciseName);

        checkFree = $(this).find('.met-text').text();
        if (checkFree !== '?') {
            // intensity-input-container 요소 숨기기
            $('.intensity-input-container').hide();
            exerciseMETs = parseFloat(checkFree).toFixed(1);
            $('.intensity-unit').text(exerciseMETs);
        } else {
            // intensity-unit-container 요소 숨기기
            $('.intensity-unit-container').hide();
            exerciseMETs = 3.0;
        }
        // 운동 시간 기본값 설정
        $('.time-input').val(30);

        // 예상 소모 칼로리 계산하여 표시
        let exerciseKcal = calculateKcal(exerciseMETs, weight);


        // 강도 버튼 클릭 이벤트
        const intensityButtons = $('.intensity-button');
        intensityButtons.off('click').on('click', function() {
            // 현재 활성화된 버튼 비활성화
            intensityButtons.removeClass('active').css('transform', 'scale(1)');

            // 클릭한 버튼 활성화
            $(this).addClass('active');

            // mets 재반영
            if (checkFree !== '?') {
                finalMETs = parseFloat($(this).attr('data-activity')) * exerciseMETs;
            } else {
                finalMETs = parseFloat($(this).attr('data-activity')) * $('.intensity-input').val();
            }

            // 소모 칼로리 재계산하여 반영
            exerciseKcal = calculateKcal(finalMETs, weight);
        });


        // 입력 값이 변경될 때 이벤트
        $('.time-input').off('input').on('input', function() {
            // 숫자만 입력 가능하도록
            let value = $(this).val().replace(/[^0-9]/g, '');

            // 빈 값이면 1로 설정
            if (value === '') {
                value = '1';
            }

            // 최대 600분으로 제한
            if (parseInt(value) > 600) {
                value = '600';
            }

            // 0 제거 (01 -> 1)
            if (value.length > 1 && value[0] === '0') {
                value = value.replace(/^0+/, '');
            }

            $(this).val(value);

            // 운동 강도 별 met 값 수정
            $.each($('.intensity-button'), function (i, e) {
                if ($(e).hasClass('active')) {
                    if (checkFree !== '?') {
                        finalMETs = parseFloat($(e).attr('data-activity')) * exerciseMETs;
                    } else {
                        finalMETs = parseFloat($(e).attr('data-activity')) * $('.intensity-input').val();
                    }
                }
            });

            // 소모 칼로리 재계산
            exerciseKcal = calculateKcal(finalMETs, weight);
        });

        // 강도 입력 필드 처리
        const intensityInput = $('.intensity-input');
        const decreaseBtn = $('.intensity-control.decrease');
        const increaseBtn = $('.intensity-control.increase');

        // 증가 버튼 클릭
        increaseBtn.off('click').on('click', function() {
            let currentValue = parseFloat(intensityInput.val());
            currentValue = Math.min(12.0, currentValue + 0.5);
            intensityInput.val(currentValue.toFixed(1));
            // 입력 변경 이벤트 발생시키기
            intensityInput.trigger('input');
        });

        // 감소 버튼 클릭
        decreaseBtn.off('click').on('click', function() {
            let currentValue = parseFloat(intensityInput.val());
            currentValue = Math.max(0.1, currentValue - 0.5);
            intensityInput.val(currentValue.toFixed(1));
            // 입력 변경 이벤트 발생시키기
            intensityInput.trigger('input');
        });

        // 입력 값이 변경될 때 이벤트
        intensityInput.off('input').on('input', function() {
            // 숫자와 소수점만 입력 가능하도록
            let value = $(this).val().replace(/[^0-9.]/g, '');

            // 소수점 첫째 자리까지만 허용
            if (value.includes('.')) {
                const parts = value.split('.');
                if (parts[1].length > 1) {
                    value = parts[0] + '.' + parts[1].substring(0, 1);
                }
            }

            $(this).val(value);

            // 운동 강도 별 met 값 수정
            $.each($('.intensity-button'), function (i, e) {
                if ($(e).hasClass('active')) {
                    finalMETs = parseFloat($(e).attr('data-activity')) * value;
                }
            });

            // 소모 칼로리 재계산
            exerciseKcal = calculateKcal(finalMETs, weight);
        });

        // 입력 값 변경이 끝났을 떄
        intensityInput.off('change').on('change', function() {
            let value = parseFloat($(this).val());
            if (isNaN(value) || value < 0.1) {
                $(this).val('3.0');
                value = 3.0;
            } else if (value > 12.0) {
                $(this).val('12.0');
                value = 12.0;
            }

            // 운동 강도 별 met 값 수정
            $.each($('.intensity-button'), function (i, e) {
                if ($(e).hasClass('active')) {
                    finalMETs = parseFloat($(e).attr('data-activity')) * value;
                }
            });

            // 소모 칼로리 재계산
            exerciseKcal = calculateKcal(finalMETs, weight);
        });

        // 포커스를 잃었을 때 소수점 형식 맞추기 (3 -> 3.0)
        intensityInput.off('blur').on('blur', function() {
            let value = $(this).val();
            if (!value.includes('.')) {
                value = parseFloat(value).toFixed(1);
            } else {
                const parts = value.split('.');
                if (parts[1].length === 0) {
                    value = parts[0] + '.0';
                }
            }
            $(this).val(value);
        });

        // 모달 추가하기 버튼 클릭 이벤트
        $('.add-button').off('click').on('click', function () {
            // 버튼 비활성화
            $(this).prop('disabled', true);

            // 운동 강도 별 met 값 수정
            let intensity = 2;
            $.each($('.intensity-button'), function (i, e) {
                if ($(e).hasClass('active')) {
                    if ($(e).attr('data-activity') === '0.8') intensity = 1;
                    else if ($(e).attr('data-activity') === '1.0') intensity = 2;
                    else intensity = 3;
                }
            });

            let exerciseRecord = {
                exerciseType: exerciseName,
                exerciseLevel: intensity,
                burnedKcal: exerciseKcal,
                exerciseTime: $('.time-input').val()
            }
            $.ajax({
                url: '/api/record/add_exercise',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(exerciseRecord),
                success: function (response) {
                    // 성공 메시지 표시
                    window.showToast(response);
                    // 갯수 업데이트
                    updateExerciseCount();
                },
                error: function (error) {
                    if (error.status === 401) {
                        alert(error.responseText);
                        window.location.href = '/loginpage';
                    } else if (error.status === 403) {
                        window.showToast(error.responseText);
                    }
                    console.log(error);
                },
                complete: function () {
                    // 버튼 활성화
                    $('.add-button').prop('disabled', false);
                }
            });

            // 모달 닫기
            closeModal();
        });


        // 모달 표시
        $('#exerciseAddModal').addClass('show');
    });

    // 예상 소모 칼로리 계산
    function calculateKcal(exerciseMETs, weight) {
        const exerciseTime = $('.time-input').val();
        // METs, 체중(kg), 운동 시간(분)을 사용하여 칼로리 계산
        const totalKcal = Math.floor(3.5 * exerciseMETs * weight * exerciseTime / 200);
        // 예상 소모 칼로리 표시
        $('#kcalValue').text(totalKcal);
        return totalKcal;
    }

    // 등록 운동 갯수 업데이트
    function updateExerciseCount() {
        $.ajax({
            url: '/api/record/exercise_count',
            type: 'POST',
            success: function (response) {
                $('.bottom-button').text(response);
            },
            error: function (error) {
                if (error.status === 401) {
                    alert(error.responseText);
                    window.location.href = '/loginpage';
                }
                console.log(error);
            }
        });
    }
    updateExerciseCount();



    // 모달 닫기 함수
    function closeModal() {
        // 애니메이션을 위해 hide 클래스 추가
        $('.modal-overlay').addClass('hide');

        // 애니메이션 완료 후 클래스 제거
        setTimeout(function () {
            $('.modal-overlay').removeClass('show hide');
        }, 300);
    }

    // 모달 배경 클릭 시 닫기
    $('.modal-overlay').click(function (e) {
        if (e.target === this) {
            closeModal();
        }
    });

    // 모달 닫기 버튼 클릭 시 닫기
    $('.close-button').click(function () {
        closeModal();
    });

    // 이전 버튼 클릭 시, 아래 버튼 클릭 시
    $('.back-arrow').click(function () {
        location.href = '/record/exercise_detail';
    });
    $('.bottom-button').click(function () {
        location.href = '/record/exercise_detail';
    });


    /////////////////////////////////////////

    // 물음표 아이콘 요소 가져오기
    const questionIcon = $('#metDetail');
    const tooltip = $('#metTooltip');

    // 페이지 로드 시 툴팁 초기화
    tooltip.css({
        'opacity': '0',
        'display': 'none'
    });

    // 툴팁 숨기기 함수
    function hideTooltip() {
        tooltip.css('opacity', '0');

        // 애니메이션이 완료된 후 display 속성 변경
        setTimeout(() => {
            tooltip.css('display', 'none');
            tooltip.removeClass('show');
        }, 300); // transition 지속 시간과 일치시킴
    }

    // 툴팁 표시하기 함수
    function showTooltip() {
        // 아이콘 위치 가져오기
        const iconRect = questionIcon[0].getBoundingClientRect();
        const iconCenterX = iconRect.left + (iconRect.width / 2);

        // 툴팁 위치 먼저 설정 (아직 화면에 표시되기 전)
        tooltip.css({
            'opacity': '0',
            'display': 'block'
        });

        // 크기 계산을 위한 강제 리플로우
        void tooltip[0].offsetHeight;

        // 위치 설정
        tooltip.css({
            'left': (iconCenterX - 100) + 'px',
            'top': (iconRect.top + 35) + 'px'
        });

        tooltip.addClass('show');

        // 다음 프레임에서 opacity 변경 (중요: 애니메이션을 위해)
        window.requestAnimationFrame(() => {
            window.requestAnimationFrame(() => {
                tooltip.css('opacity', '1');
            });
        });
    }

    // 아이콘 클릭 이벤트
    questionIcon.click(function (event) {
        event.stopPropagation();

        if (tooltip.hasClass('show')) {
            hideTooltip();
        } else {
            showTooltip();
        }
    });

    // 문서 클릭 시 툴팁 닫기
    $(document).click(function (event) {
        if (!questionIcon.is(event.target) && questionIcon.has(event.target).length === 0 && tooltip.hasClass('show')) {
            hideTooltip();
        }
    });

    // 창 크기 변경 시 위치 재조정
    $(window).resize(function () {
        if (tooltip.hasClass('show')) {
            const iconRect = questionIcon[0].getBoundingClientRect();
            const iconCenterX = iconRect.left + (iconRect.width / 2);

            tooltip.css({
                'left': (iconCenterX - 100) + 'px',
                'top': (iconRect.top + 35) + 'px'
            });
        }
    });
});