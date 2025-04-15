// DOM이 완전히 로드될 때까지 대기
$(document).ready(function () {

    // 날짜 선택기 값 입력
    const dateHeader = $('.date-header');
    let year = dateHeader.data('year');
    let month = dateHeader.data('month');
    let day = dateHeader.data('day');
    const date = new Date(year, month - 1, day); // month는 0부터 시작하므로 -1
    $('#date-input').val(`${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`);

    // 달력 버튼 클릭 이벤트
    $('#calendar-btn').click(function () {
        // 브라우저 기본 날짜 선택기 직접 제어
        var dateInput = $('#date-input')[0]; // DOM 요소 가져오기

        // 날짜 입력 필드가 date 타입인 경우 showPicker 메서드 사용
        if (dateInput && dateInput.type === 'date' && typeof dateInput.showPicker === 'function') {
            dateInput.showPicker();
        } else {
            // 구형 브라우저나 지원하지 않는 경우 기존 방식 시도
            $('#date-input').click();
        }
    });

    // 날짜 선택기에서 날짜가 변경될 때
    $('#date-input').change(function () {
        // 선택된 날짜 가져오기
        let selectedDate = $(this).val();
        year = selectedDate.split('-')[0];
        month = selectedDate.split('-')[1];
        day = selectedDate.split('-')[2];
        location.href = `/record?year=${year}&month=${month}&day=${day}`;
    });

    // 이전 날짜 버튼 클릭 이벤트
    $('#prev-date').click(function () {
        // 하루 빼기
        date.setDate(date.getDate() - 1);

        // 새로운 날짜로 URL 변경
        year = date.getFullYear();
        month = date.getMonth() + 1; // 월은 0부터 시작하므로 +1
        day = date.getDate();
        location.href = `/record?year=${year}&month=${month}&day=${day}`;
    });

    // 다음 날짜 버튼 클릭 이벤트
    $('#next-date').click(function () {
        // 하루 더하기
        date.setDate(date.getDate() + 1);

        // 새로운 날짜로 URL 변경
        year = date.getFullYear();
        month = date.getMonth() + 1; // 월은 0부터 시작하므로 +1
        day = date.getDate();
        location.href = `/record?year=${year}&month=${month}&day=${day}`;
    });

    // 스크롤에 따라 헤더 표시/숨김 처리하는 JavaScript 코드
    $('.page-header').css('z-index', '1100'); // 헤더의 z-index 설정

    // 필요한 변수 초기화
    let lastScrollTop = 0; // 마지막 스크롤 위치
    const headerHeight = dateHeader.outerHeight(); // 헤더 높이
    let isHeaderVisible = true; // 헤더 표시 상태

    // 스크롤 이벤트 리스너 추가
    $(window).scroll(function () {
        // 현재 스크롤 위치
        const currentScrollTop = $(window).scrollTop();

        // 스크롤 방향 확인 (내려가는 중인지, 올라가는 중인지)
        if (currentScrollTop > lastScrollTop && currentScrollTop > headerHeight) {
            // 아래로 스크롤하는 중이고, 헤더 높이보다 더 내려왔을 때
            if (isHeaderVisible) {
                dateHeader.addClass('date-header-hidden'); // 헤더 숨기기
                isHeaderVisible = false;
            }
        } else {
            // 위로 스크롤하는 중
            if (!isHeaderVisible) {
                dateHeader.removeClass('date-header-hidden'); // 헤더 표시하기
                isHeaderVisible = true;
            }
        }

        // 마지막 스크롤 위치 업데이트
        lastScrollTop = currentScrollTop;

        // 스크롤이 맨 위로 올라갔을 시 requiredReload가 true일 경우 페이지 리로드
        if ($(this).scrollTop() === 0 && requiredReload) {
            location.reload();
        }
    });


    ///////////////////////////////////////////////////////////////////////

    // 치팅데이 설정 버튼 기본 비활성화
    $('.use-button').prop('disabled', true);
    $('.use-button').css('opacity', '0.5');

    // 현재 칼로리 계산
    let currentKcal = 0;
    $('.meal-card').each(function () {
        const kcal = parseInt($(this).find('.meal-calories').text());
        if (!isNaN(kcal)) {
            currentKcal += kcal;
        }
    });
    $('#currentKcal').text(currentKcal);

    // 잔여 칼로리 계산
    let ableKcal = parseInt($('#ableKcal').text());
    let leftoverKcal = ableKcal - currentKcal;
    $('#leftoverKcal').text(leftoverKcal);

    // 유저 목표, 타입 가져오기
    const userTarget = $('.nutrition-section').data('user_target');
    const userType = $('.nutrition-section').data('user_type');

    // 경고 클래스 부여
    if (userType === 'quito') $('#alertCarb').addClass('alert-value');
    else $('#alertFat').addClass('alert-value');


    // 상세보기 버튼과 상세 섹션 가져오기
    const detailBtn = $('#detailBtn');
    const detailSection = $('#detailSection');

    // 상세보기 버튼에 클릭 이벤트 리스너 추가
    detailBtn.click(function () {
        // 애니메이션과 함께 상세 섹션의 가시성 토글
        detailSection.toggleClass('open');

        // 버튼 텍스트 변경
        if (detailSection.hasClass('open')) {
            detailBtn.text('- 상세보기');
        } else {
            detailBtn.text('+ 상세보기');
        }
    });

    // 데이터 속성을 기반으로 프로그레스 바 너비 설정 함수
    function setProgressBarWidths() {
        // 모든 영양소 채우기 요소 가져오기
        const nutritionFills = $('.nutrition-fill');

        // 각 채우기 요소에 대해 너비 백분율 계산
        nutritionFills.each(function () {
            const fill = $(this);
            const current = parseFloat(fill.attr('data-current'));
            const target = parseFloat(fill.attr('data-target'));

            // 백분율 계산 (0-100% 사이로 제한)
            const percentage = Math.min(100, Math.max(0, (current / target) * 100));

            // 너비 설정
            fill.css('width', percentage + '%');

            // 초과 경고
            if ($(this).hasClass('alert-test') && current > target) {
                $(this).parent().siblings().find('span:first').css('color', '#bc3657');
            }

        });

        // 메인 프로그레스 바용 총 칼로리 계산
        const totalCals = ableKcal * 1.1; // 10% 여유를 두기 위해 1.1배
        const targetCals = ableKcal;
        const calPercentage = (currentKcal / totalCals) * 100;

        // 매크로 채우기 요소 가져오기
        const carbFill = $('.fill-carb');
        const proteinFill = $('.fill-protein');
        const fatFill = $('.fill-fat');
        const carbData = $('.nutrition-fill.carb-fill');
        const proteinData = $('.nutrition-fill.protein-fill');
        const fatData = $('.nutrition-fill.fat-fill');

        // 총 칼로리 대비 각 영양소의 개별 백분율 계산
        const carbCurrent = parseFloat(carbData.attr('data-current'));
        const carbPercentage = (carbCurrent * 4 / targetCals) * 100;

        const proteinCurrent = parseFloat(proteinData.attr('data-current'));
        const proteinPercentage = (proteinCurrent * 4 / targetCals) * 100;

        const fatCurrent = parseFloat(fatData.attr('data-current'));
        const fatPercentage = 100 - (carbPercentage + proteinPercentage);

        // 총 칼로리 백분율에 맞게 정규화
        const scaleFactor = calPercentage / 100;

        carbFill.css('width', (carbPercentage * scaleFactor) + '%');
        proteinFill.css('width', (proteinPercentage * scaleFactor) + '%');
        fatFill.css('width', (fatPercentage * scaleFactor) + '%');
    }

    // 프로그레스 바 초기화
    setProgressBarWidths();

    // 칼로리 초과 시 표기 변경
    if (leftoverKcal < 0) {
        $('.calories-info').text(-leftoverKcal + 'kcal 초과 섭취');
    }

    if (userTarget === 'gain') {
        // 체중 증가 목표일 경우
        if (leftoverKcal > ableKcal * 0.1) {
            $('#currentKcal').css('color', '#bc3657');
        }
        $('#targetText').text('증량');
        $('.use-button').text('증량 목표시 비활성화');
    } else {
        // 체중 감소, 유지 목표일 경우
        if (leftoverKcal < ableKcal * -0.1 || currentKcal === 0) {
            $('#currentKcal').css('color', '#bc3657');
            if (currentKcal !== 0) {
                let cheatPoint = parseInt($('#cheatPoint').text()); // 치팅포인트
                if (cheatPoint >= -leftoverKcal) {
                    // 치팅데이 버튼 활성화
                    $('.use-button').prop('disabled', false);
                    $('.use-button').css('opacity', '1');
                } else {
                    // 포인트 부족
                    $('.use-button').text('포인트가 부족합니다');
                }
            } else {
                // 칼로리 입력이 없는 경우
                $('.use-button').text('목표 초과 시 사용 가능');
            }
        } else {
            // 목표 달성중인 경우
            $('.use-button').text('목표 초과 시 사용 가능');
        }

        if (userTarget === 'maintain') {
            $('#targetText').text('유지');
        } else {
            $('#targetText').text('감량');
        }
    }


    // 운동 기록 버튼
    $('.record-button.exercise-button').click(function () {
        if ($('.exercise-time-value').text() === '0분') {
            // 운동 기록이 없는 경우
            location.href = '/record/exercise_form';
        } else {
            location.href = '/record/exercise_detail';
        }
    });


    //////////////////////////


    // 모든 식사 카드 가져오기
    const mealCards = $('.meal-card');

    // 각 카드에 대해 처리
    mealCards.each(function () {
        // 칼로리 값 가져오기
        const kcal = $(this).find('.meal-calories').text();
        const statusIcon = $(this).find('.status-icon');
        const mealType = $(this).attr('data-meal_type'); // 식사 타입 가져오기

        // 칼로리 값에 따라 아이콘 표시
        if (kcal !== '0kcal') {
            // 칼로리가 입력된 경우: 체크 표시
            statusIcon.html('<i class="bi bi-check"></i>');
        } else {
            // 칼로리가 입력되지 않은 경우: 플러스 표시
            statusIcon.html('<i class="bi bi-plus"></i>');
        }

        // 클릭 이벤트 리스너 추가
        $(this).click(function () {
            if (kcal !== '0kcal') {
                location.href = `/record/food_record?meal_type=${mealType}`;
            } else {
                location.href = `/record/search?meal_type=${mealType}`;
            }
        });
    });


    ///////////////////////


    // BMI 마커 위치 설정 함수
    function setBmiMarkerPosition(bmiValue) {
        const marker = $('.bmi-marker');
        const minBmi = 16;
        const maxBmi = 32;

        // BMI 값이 범위를 벗어나는 경우 경계값으로 설정
        let adjustedBmi = bmiValue;
        if (bmiValue < minBmi) {
            adjustedBmi = minBmi;
        } else if (bmiValue > maxBmi) {
            adjustedBmi = maxBmi;
        }

        // 마커 위치 계산 (16~32 범위를 0~100%로 변환)
        const position = ((adjustedBmi - minBmi) / (maxBmi - minBmi)) * 100;

        // 마커 위치 설정
        marker.css('left', position + '%');

        // BMI 텍스트 업데이트
        $('.bmi-info').text('BMI : ' + bmiValue.toFixed(1));
    }

    // 유저 키, 몸무게, 통계 체중 가져오기
    const userHeight = parseFloat($('.weight-info').data('user_height'));
    const userWeight = parseFloat($('.weight-info').data('user_weight'));
    const statisticsWeight = parseFloat($('.weight-info').data('statistics_weight'));
    const goalWeight = parseFloat($('.weight-info').data('goal_weight'));
    let todayWeight = 0;
    if (statisticsWeight === 0) todayWeight = userWeight;
    else todayWeight = statisticsWeight;

    // 현재 체중, 목표 체중 표시
    $('#currentWeight').text(todayWeight);
    $('.goal-info').text('목표 : ' + goalWeight + 'kg');

    // 현재 BMI 설정
    const currentBmi = calculateBMI(todayWeight, userHeight);
    setBmiMarkerPosition(currentBmi);


    // 과거 체중 기록 가져오기
    const pastMonthWeight = parseFloat($('.weight-info').data('past_month_weight'));
    const pastWeekWeight = parseFloat($('.weight-info').data('past_week_weight'));

    // 체중 변화량 계산
    function calculateWeightChange(weight) {
        let weekDiff = weight - pastWeekWeight;
        let monthDiff = weight - pastMonthWeight;

        if (pastWeekWeight !== 0) {
            if (weekDiff > 0) $('#weekDiff').text('+' + weekDiff + 'kg');
            else $('#weekDiff').text(weekDiff + 'kg');
        } else $('#weekDiff').text('-');

        if (pastMonthWeight !== 0) {
            if (monthDiff > 0) $('#monthDiff').text('+' + monthDiff + 'kg');
            else $('#monthDiff').text(monthDiff + 'kg');
        } else $('#monthDiff').text('-');
    }

    calculateWeightChange(todayWeight);


    ///////////////////

    // 체중 기록 모달 관련
    const weightButton = $('.record-button.weight-button');
    const modal = $('#weightModal');
    const closeButton = $('.modal-close');
    const cancelButton = $('#cancelButton');
    const submitButton = $('#submitButton');
    const weightInput = $('#weightInput');

    // 체중 입력 값 검증
    weightInput.on('input', function () {
        const value = parseFloat($(this).val());

        if (value >= 200) {
            // 값이 200을 초과하면 입력 텍스트를 빨간색으로 변경하고 등록 버튼 비활성화
            $(this).css('color', 'red');
            $('#submitButton').prop('disabled', true).addClass('disabled');
            // 버튼 연하게
            $('#submitButton').css('opacity', '0.5');
        } else {
            // 값이 유효하면 정상 색상으로 되돌리고 등록 버튼 활성화
            $(this).css('color', '');
            $('#submitButton').prop('disabled', false).removeClass('disabled');
            $('#submitButton').css('opacity', '1');
        }
    });

    // 조회 페이지가 현재 날짜가 아닐 시 체중 입력 버튼 비활성화
    const today = new Date();
    if (year !== today.getFullYear() || month !== (today.getMonth() + 1) || day !== today.getDate()) {
        weightButton.prop('disabled', true).addClass('disabled');
        weightButton.css('opacity', '0.5');
        weightButton.text('당일 체중만 입력 가능');
    }

    // 모달 열기
    weightButton.click(function () {
        modal.addClass('show');

        // 현재 체중값을 모달 입력창에 설정 (kg 제외)
        const currentWeight = $('.current-weight').text().trim();
        const weightNumberOnly = currentWeight.replace(' kg', '');
        weightInput.val(weightNumberOnly);

        // 초기 값 검증 실행
        weightInput.trigger('input');

        // 입력 필드에 포커스
        setTimeout(() => {
            weightInput.focus();
            weightInput.select();
        }, 300); // 애니메이션 완료 후 포커스
    });

    // 모달 닫기 함수
    function closeModal() {
        // 애니메이션을 위해 hide 클래스 추가
        modal.addClass('hide');

        // 애니메이션 완료 후 show 클래스 제거
        setTimeout(() => {
            modal.removeClass('show hide');
        }, 300); // 애니메이션 시간(0.3초)과 동일하게 설정
    }

    // 모달 닫기 (X 버튼)
    closeButton.click(closeModal);

    // 모달 닫기 (취소 버튼)
    cancelButton.click(closeModal);

    let requiredReload = false;
    // 등록 버튼
    submitButton.click(function () {
        // 입력된 체중값 가져오기 (숫자만, 소숫점 한자리까지)
        const newWeight = parseFloat(weightInput.val()).toFixed(1);

        // 입력값이 비어있거나 200kg 이상인 경우
        if (newWeight === '' && newWeight >= 200) {
            window.showToast('체중을 올바르게 입력해주세요.');
            return;
        }

        // 버튼 비활성화
        $(this).prop('disabled', true);


        $.ajax({
            url: '/api/record/update_weight',
            type: 'POST',
            data: {
                weight: newWeight,
                goalWeight: goalWeight
            },
            success: function (response) {
                window.showToast(response);
                requiredReload = true; // 성공적으로 업데이트된 경우 리로드 플래그 설정

                // 입력된 체중값 반영 (kg 추가)
                $('.current-weight').text(parseFloat(newWeight) + ' kg');

                // BMI 재계산
                const bmi = calculateBMI(parseFloat(newWeight), userHeight);
                $('.bmi-info').text('BMI : ' + bmi.toFixed(1));

                // BMI 마커 업데이트
                setBmiMarkerPosition(bmi);

                // 체중 변화량 재계산
                calculateWeightChange(newWeight);

                // 모달 닫기
                closeModal();
            },
            error: function (xhr, status, error) {
                if (xhr.status === 401) {
                    alert(xhr.responseText);
                    window.location.href = '/loginpage';
                    return;
                }
                window.showToast('체중 기록에 실패했습니다. 다시 시도해주세요.');
                console.error('Error:', error);
            },
            complete: function () {
                // 버튼 활성화
                $(this).prop('disabled', false);
            }
        });
    });

    // 배경 클릭 시 모달 닫기
    modal.click(function (event) {
        if (event.target === this) {
            closeModal();
        }
    });

    // 단순한 BMI 계산 함수 (예시)
    function calculateBMI(weight, heightCm) {
        const heightM = heightCm / 100;
        return weight / (heightM * heightM);
    }

    ///////////////////////////////////////////////

    // 치팅데이 모달 관련
    const showCheatModalBtn = $('#cheatDay');
    const cheatModal = $('#cheatDayModal');
    const closeCheatModalButton = cheatModal.find('.modal-close');
    const useButton = cheatModal.find('.use-button');

    // 모달 열기
    showCheatModalBtn.click(function () {
        cheatModal.addClass('show');
    });

    // 모달 닫기 함수
    function closeCheatModal() {
        // 애니메이션을 위해 hide 클래스 추가
        cheatModal.addClass('hide');

        // 애니메이션 완료 후 show 클래스 제거
        setTimeout(() => {
            cheatModal.removeClass('show hide');
        }, 300); // 애니메이션 시간(0.3초)과 동일하게 설정
    }

    // 모달 닫기 (X 버튼)
    closeCheatModalButton.click(closeCheatModal);

    // 치팅데이 사용 버튼 클릭 시
    useButton.click(function () {
        // 버튼 비활성화
        $(this).prop('disabled', true);

        let cheatPoint = parseInt($('#cheatPoint').text()); // 치팅포인트
        let usingCheatPoint = -leftoverKcal;
        let leftoverCheatPoint = cheatPoint + leftoverKcal;

        $.ajax({
            url: '/api/record/use_cheat_point',
            type: 'POST',
            data: {
                cheatingKcal: usingCheatPoint,
                cheatPoint: leftoverCheatPoint
            },
            success: function (response) {
                window.showToast(response);
                setTimeout(function () {
                    location.reload();
                }, 2000); // 2초 후 페이지 리로드

            }, error: function (xhr, status, error) {
                if (xhr.status === 401) {
                    alert(xhr.responseText);
                    window.location.href = '/loginpage';
                    return;
                }
                window.showToast('포인트 사용에 실패했습니다. 다시 시도해주세요.');
                console.error('Error:', error);

            }, complete: function () {
                // 버튼 활성화
                $(this).prop('disabled', false);
            }
        });

        closeCheatModal();
    });

    // 배경 클릭 시 모달 닫기
    cheatModal.click(function (event) {
        if (event.target === this) {
            closeCheatModal();
        }
    });

    // 차트 아이콘 클릭 시 차트 페이지로 이동
    $('.statistics-button').click(function () {
        location.href = '/record/statistics';
    });

    /////////////////////////////////////////

    // 물음표 아이콘 요소 가져오기
    const questionIcon = $('#cheatDetail');
    const tooltip = $('#cheatDayTooltip');

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
            'left': (iconCenterX - 150) + 'px',
            'top': (iconRect.top - 10 - tooltip.outerHeight()) + 'px'
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
                'left': (iconCenterX - 150) + 'px',
                'top': (iconRect.top - 10 - tooltip.outerHeight()) + 'px'
            });
        }
    });

});