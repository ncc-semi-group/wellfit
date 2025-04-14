// DOM이 완전히 로드될 때까지 대기
$(document).ready(function() {

    // 날짜 선택기 값 입력
    const dateHeader = $('.date-header');
    let year = dateHeader.data('year');
    let month = dateHeader.data('month');
    let day = dateHeader.data('day');
    const date = new Date(year, month - 1, day); // month는 0부터 시작하므로 -1
    $('#date-input').val(`${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`);

    // 달력 버튼 클릭 이벤트
    $('#calendar-btn').click(function() {
        // 브라우저 기본 날짜 선택기 직접 제어
        var dateInput = $('#date-input')[0]; // DOM 요소 가져오기

        // 날짜 입력 필드가 date 타입인 경우 showPicker 메서드 사용
        if (dateInput && dateInput.type === 'date' && typeof dateInput.showPicker === 'function') {
            dateInput.showPicker();
            // $('#date-input').click();
        } else {
            // 구형 브라우저나 지원하지 않는 경우 기존 방식 시도
            $('#date-input').click();
        }
    });

    // 날짜 선택기에서 날짜가 변경될 때
    $('#date-input').change(function() {
        // 선택된 날짜 가져오기
        let selectedDate = $(this).val();
        year = selectedDate.split('-')[0];
        month = selectedDate.split('-')[1];
        day = selectedDate.split('-')[2];
        location.href = `/record?year=${year}&month=${month}&day=${day}`;
    });

    // 이전 날짜 버튼 클릭 이벤트
    $('#prev-date').click(function() {
        // 하루 빼기
        date.setDate(date.getDate() - 1);

        // 새로운 날짜로 URL 변경
        year = date.getFullYear();
        month = date.getMonth() + 1; // 월은 0부터 시작하므로 +1
        day = date.getDate();
        location.href = `/record?year=${year}&month=${month}&day=${day}`;
    });

    // 다음 날짜 버튼 클릭 이벤트
    $('#next-date').click(function() {
        // 하루 더하기
        date.setDate(date.getDate() + 1);

        // 새로운 날짜로 URL 변경
        year = date.getFullYear();
        month = date.getMonth() + 1; // 월은 0부터 시작하므로 +1
        day = date.getDate();
        location.href = `/record?year=${year}&month=${month}&day=${day}`;
    });





    // 상세보기 버튼과 상세 섹션 가져오기
    const detailBtn = $('#detailBtn');
    const detailSection = $('#detailSection');

    // 상세보기 버튼에 클릭 이벤트 리스너 추가
    detailBtn.click(function() {
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
        nutritionFills.each(function() {
            const fill = $(this);
            const current = parseFloat(fill.attr('data-current'));
            const target = parseFloat(fill.attr('data-target'));

            // 백분율 계산 (0-100% 사이로 제한)
            const percentage = Math.min(100, Math.max(0, (current / target) * 100));

            // 너비 설정
            fill.css('width', percentage + '%');
        });

        // 메인 프로그레스 바용 총 칼로리 계산
        const totalCals = 2170;
        const currentCals = 1545;
        const calPercentage = (currentCals / totalCals) * 100;

        // 매크로 채우기 요소 가져오기
        const carbFill = $('.fill-carb');
        const proteinFill = $('.fill-protein');
        const fatFill = $('.fill-fat');

        // 총 칼로리 대비 각 영양소의 개별 백분율 계산
        const carbCurrent = parseFloat(carbFill.attr('data-current'));
        const carbTarget = parseFloat(carbFill.attr('data-target'));
        const carbPercentage = (carbCurrent / carbTarget) * (carbTarget / totalCals) * 100;

        const proteinCurrent = parseFloat(proteinFill.attr('data-current'));
        const proteinTarget = parseFloat(proteinFill.attr('data-target'));
        const proteinPercentage = (proteinCurrent / proteinTarget) * (proteinTarget / totalCals) * 100;

        const fatCurrent = parseFloat(fatFill.attr('data-current'));
        const fatTarget = parseFloat(fatFill.attr('data-target'));
        const fatPercentage = (fatCurrent / fatTarget) * (fatTarget / totalCals) * 100;

        // 총 칼로리 백분율에 맞게 정규화
        const totalPercentage = carbPercentage + proteinPercentage + fatPercentage;
        const scaleFactor = calPercentage / totalPercentage;

        carbFill.css('width', (carbPercentage * scaleFactor) + '%');
        proteinFill.css('width', (proteinPercentage * scaleFactor) + '%');
        fatFill.css('width', (fatPercentage * scaleFactor) + '%');
    }

    // 프로그레스 바 초기화
    setProgressBarWidths();


    //////////////////////////


    // 모든 식사 카드 가져오기
    const mealCards = $('.meal-card');

    // 각 카드에 대해 처리
    mealCards.each(function() {
        // 칼로리 값 가져오기
        const kcal = $(this).find('.meal-calories').text();
        const statusIcon = $(this).find('.status-icon');
        const mealType = $(this).data('meal_type'); // 식사 타입 가져오기

        // 칼로리 값에 따라 아이콘 표시
        if (kcal !== '0kcal') {
            // 칼로리가 입력된 경우: 체크 표시
            statusIcon.html('<i class="bi bi-check"></i>');
        } else {
            // 칼로리가 입력되지 않은 경우: 플러스 표시
            statusIcon.html('<i class="bi bi-plus"></i>');
        }

        // 클릭 이벤트 리스너 추가
        $(this).click(function() {
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

    // 현재 BMI 설정 (예시: 23.5)
    const currentBmi = 23.5;
    setBmiMarkerPosition(currentBmi);


    ///////////////////

    // 체중 기록 모달 관련
    const recordButton = $('.record-button');
    const modal = $('#weightModal');
    const closeButton = $('.modal-close');
    const cancelButton = $('#cancelButton');
    const submitButton = $('#submitButton');
    const weightInput = $('#weightInput');

    // 체중 입력 값 검증
    weightInput.on('input', function() {
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

    // 모달 열기
    recordButton.click(function() {
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

    // 모달 닫기 (등록 버튼)
    submitButton.click(function() {
        // 입력된 체중값 가져오기 (숫자만)
        const newWeight = weightInput.val();
        console.log('새 체중 등록:', newWeight);

        // 입력된 체중값 반영 (kg 추가)
        $('.current-weight').text(newWeight + ' kg');

        // BMI 재계산 (예시)
        const bmi = calculateBMI(parseFloat(newWeight), 175); // 키 175cm 가정
        $('.bmi-info').text('BMI : ' + bmi.toFixed(1));

        // BMI 마커 업데이트
        setBmiMarkerPosition(bmi);

        // 모달 닫기
        closeModal();
    });

    // 배경 클릭 시 모달 닫기
    modal.click(function(event) {
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
    showCheatModalBtn.click(function() {
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

    // 모달 닫기 (사용 버튼)
    useButton.click(function() {
        console.log('포인트 사용 완료');
        alert('치팅데이 설정이 완료되었습니다!');
        closeCheatModal();
    });

    // 배경 클릭 시 모달 닫기
    cheatModal.click(function(event) {
        if (event.target === this) {
            closeCheatModal();
        }
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
    questionIcon.click(function(event) {
        event.stopPropagation();

        if (tooltip.hasClass('show')) {
            hideTooltip();
        } else {
            showTooltip();
        }
    });

    // 문서 클릭 시 툴팁 닫기
    $(document).click(function(event) {
        if (!questionIcon.is(event.target) && questionIcon.has(event.target).length === 0 && tooltip.hasClass('show')) {
            hideTooltip();
        }
    });

    // 창 크기 변경 시 위치 재조정
    $(window).resize(function() {
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