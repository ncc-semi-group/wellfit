$(document).ready(function () {
    // 단위 선택 처리
    $('.unit-button').click(function () {
        $('.unit-button').removeClass('selected');
        $(this).addClass('selected');

        const unit = $(this).text();

        // "내용량"과 "1회 섭취량"의 단위만 업데이트
        $('.nutrition-row').each(function () {
            const label = $(this).find('label').text().trim();
            if (label.includes('내용량') || label.includes('1회 섭취량')) {
                $(this).find('.unit').text(unit);
            }
        });
    });


    // 소숫점 입력 가능 숫자 입력 필드에 숫자와 소수점만 입력 가능하도록 처리
    $('.decimal-input').on('input', function() {
        $(this).val($(this).val().replace(/[^0-9.]/g, ''));
    });

    // 숫자 입력 필드에 숫자만 입력 가능하도록 처리
    $('.integer-input').on('input', function () {
        $(this).val($(this).val().replace(/[^0-9]/g, ''));
    });


    // 뒤로 가기 버튼
    $('.back-button').click(function () {
        history.back();
    });

    // 저장 버튼 기능 (유효성 검사 및 빈 필드 처리)
    $('.save-button').click(function () {

        // 필수 필드 유효성 검사
        let isValid = true;
        $('[required]').each(function () {
            if ($(this).val() === '') {
                isValid = false;
                // 여기에 시각적 표시를 추가할 수 있음
            }
        });

        if (!isValid) {
            window.showToast('필수 항목을 모두 입력하세요.');
            return;
        }
        // 제출 전 빈 필드를 "0"으로 설정
        $('input[type="number"]').each(function () {
            if ($(this).val() === '') {
                $(this).val('0');
            }
        });

        // 폼 제출 진행
        $(this).prop('disabled', true); // 버튼 비활성화
        addForm();

        // 폼 처리 후 폼 초기화
        // 여기서는 빈 필드를 다시 비워서 placeholder가 보이도록
        $('input[type="number"]').each(function () {
            if ($(this).val() === '0') {
                $(this).val('');
            }
        });
    });

    // 삭제 버튼 기능
    $('.delete-button').click(function () {
        if(!confirm('삭제한 음식에 대해선 식단 정보를 수정할 수 없게 됩니다. 정말 삭제하시겠습니까?')) return;
        $(this).prop('disabled', true); // 버튼 비활성화

        let foodId = $('#food-id').val();


        // AJAX 요청
        $.ajax({
            url: '/api/record/delete_food',
            type: 'POST',
            data: {foodId: foodId},
            success: function (response) {
                window.showToast(response);
                setTimeout(() => {
                    // 이전 페이지로 돌아간 후 새로고침
                    window.location.replace(document.referrer);
                }, 1000);
            },
            error: function (xhr, status, error) {
                if (xhr.status === 401) {
                    alert(xhr.responseText);
                    window.location.href = '/loginpage';
                    return;
                } else if (xhr.status === 400) {
                    window.showToast(xhr.responseText);
                } else {
                    window.showToast('식품 삭제에 실패했습니다. 다시 시도해주세요.');
                    console.error('Error:', error);
                }
                $('.delete-button').prop('disabled', false); // 버튼 활성화
            }
        });
    });

    // 수정 버튼 기능
    $('.update-button').click(function () {
        // 버튼 더 눌러도 안되게
        $(this).prop('disabled', true);
        updateForm();

    });

    // 초기화 버튼 기능
    $('.reset-button').click(function () {
        // 모든 입력 필드 초기화
        $('input[type="text"], input[type="number"]').val('');
        $('.unit-button').removeClass('selected');
        $('.unit-button:first').addClass('selected'); // 첫 번째 단위 버튼 선택
    });

    // 폼 제출 함수
    function addForm() {
        let formWeight = $('#input-weight').val();
        let formServingSize = $('#input-serving-size').val();
        if (formServingSize == 0 || formServingSize == null) formServingSize = Math.floor(formWeight);
        let divider = formServingSize === Math.floor(formWeight) ? formWeight : formServingSize;

        // 폼 데이터 수집
        const formData = {
            name: $('#food-name').val(),
            manufacturerName: $('#brand').val(),
            standard: '100' + $('.unit-button.selected').text(),
            weight: formWeight,
            servingSize: formServingSize,
            kcal: Math.round(100 * $('#input-kcal').val() / divider),
            carbohydrate: 100 * $('#input-carbohydrate').val() / divider,
            sugar: 100 * $('#input-sugar').val() / divider,
            protein: 100 * $('#input-protein').val() / divider,
            fat: 100 * $('#input-fat').val() / divider,
            saturatedFat: 100 * $('#input-saturated-fat').val() / divider,
            transFat: 100 * $('#input-trans-fat').val() / divider,
            natrium: Math.round(100 * $('#input-natrium').val() / divider),
            cholesterol: 100 * $('#input-cholesterol').val() / divider
        };


        // AJAX 요청
        $.ajax({
            url: '/api/record/add_food',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function (response) {
                window.showToast(response);
                setTimeout(() => {
                    // 이전 페이지로 돌아간 후 새로고침
                    window.location.replace(document.referrer);
                }, 1000);
            },
            error: function (xhr, status, error) {
                if (xhr.status === 401) {
                    alert(xhr.responseText);
                    window.location.href = '/loginpage';
                    return;
                }
                window.showToast('식품 추가에 실패했습니다. 다시 시도해주세요.');
                console.error('Error:', error);
                $('.save-button').prop('disabled', false); // 버튼 활성화
            }
        });
    }

    // 업데이트 함수
    function updateForm() {
        let formWeight = $('#input-weight').val();
        let formServingSize = $('#input-serving-size').val();
        if (formServingSize == 0 || formServingSize == null) formServingSize = Math.floor(formWeight);
        let divider = formServingSize === Math.floor(formWeight) ? formWeight : formServingSize;

        // 폼 데이터 수집
        const formData = {
            id: $('#food-id').val(),
            name: $('#food-name').val(),
            manufacturerName: $('#brand').val(),
            standard: '100' + $('.unit-button.selected').text(),
            weight: formWeight,
            servingSize: formServingSize,
            kcal: Math.round(100 * $('#input-kcal').val() / divider),
            carbohydrate: 100 * $('#input-carbohydrate').val() / divider,
            sugar: 100 * $('#input-sugar').val() / divider,
            protein: 100 * $('#input-protein').val() / divider,
            fat: 100 * $('#input-fat').val() / divider,
            saturatedFat: 100 * $('#input-saturated-fat').val() / divider,
            transFat: 100 * $('#input-trans-fat').val() / divider,
            natrium: Math.round(100 * $('#input-natrium').val() / divider),
            cholesterol: 100 * $('#input-cholesterol').val() / divider
        };


        // AJAX 요청
        $.ajax({
            url: '/api/record/update_food',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function (response) {
                window.showToast(response);
                setTimeout(() => {
                    // 이전 페이지로 돌아간 후 새로고침
                    window.location.replace(document.referrer);
                }, 1000);
            },
            error: function (xhr, status, error) {
                if (xhr.status === 401) {
                    alert(xhr.responseText);
                    window.location.href = '/loginpage';
                    return;
                }
                window.showToast('정보 수정에 실패했습니다. 다시 시도해주세요.');
                console.error('Error:', error);
                $('.update-button').prop('disabled', false); // 버튼 활성화
            }
        });
    }


    ////////////////////////////////

    // 툴팁 관련
    // 물음표 아이콘 요소 가져오기
    const questionIcon = $('#serving-detail');
    const tooltip = $('#serving-tooltip');

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


    // 스크롤 시 툴팁 닫기
    $(window).scroll(function () {
        if (tooltip.hasClass('show')) {
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
                'top': (iconRect.top - 10 - tooltip.outerHeight()) + 'px'
            });
        }
    });
});