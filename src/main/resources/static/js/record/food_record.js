$(document).ready(function () {

    const year = $('.title').data('year');
    const month = $('.title').data('month');
    const day = $('.title').data('day');

    // url에서 파라미터 가져오기
    const urlParams = new URLSearchParams(window.location.search);
    const mealType = urlParams.get('meal_type');

    // food-item이 없을 경우
    if ($('.food-item').length === 0) {
        // 이전 페이지 url 가져오기
        const previousUrl = document.referrer;
        // 이전 페이지가 검색 페이지인 경우
        if (previousUrl.includes('/search')) {
            location.href = `/record?year=${year}&month=${month}&day=${day}`;
        } else {
            // 검색 페이지로 이동
            location.href = `/record/search?meal_type=${mealType}`;
        }
    }

    // 영양소 합계 변수
    let totalKcal = 0;
    let totalCarbohydrate = 0;
    let totalSugar = 0;
    let totalProtein = 0;
    let totalFat = 0;
    let totalSaturatedFat = 0;
    let totalTransFat = 0;
    let totalNatrium = 0;
    let totalCholesterol = 0;

    // 초기 영양소 합계 계산
    $.each($('.food-item'), function () {
        // food-item의 자식 요소
        const foodInfo = $(this).find('.food-info');
        totalKcal += parseInt(foodInfo.attr('data-kcal'));
        totalCarbohydrate += parseFloat(foodInfo.attr('data-carbohydrate'));
        totalSugar += parseFloat(foodInfo.attr('data-sugar'));
        totalProtein += parseFloat(foodInfo.attr('data-protein'));
        totalFat += parseFloat(foodInfo.attr('data-fat'));
        totalSaturatedFat += parseFloat(foodInfo.attr('data-saturated_fat'));
        totalTransFat += parseFloat(foodInfo.attr('data-trans_fat'));
        totalNatrium += parseInt(foodInfo.attr('data-natrium'));
        totalCholesterol += parseFloat(foodInfo.attr('data-cholesterol'));
    });

    // 영양소 비율
    let totalNutrition = totalCarbohydrate * 4 + totalProtein * 4 + totalFat * 9;
    let carbohydrateRatio = Math.round((totalCarbohydrate * 4 / totalNutrition) * 100);
    let proteinRatio = Math.round((totalProtein * 4 / totalNutrition) * 100);
    let fatRatio = 100 - carbohydrateRatio - proteinRatio;


    // 초기 정보 표시
    updateInfo();
    drawNutritionChart();


    // 차트 영역 그리기 함수
    function drawNutritionChart() {
        // 영양소 비율에 따라 conic-gradient 값 계산
        const carbsEnd = carbohydrateRatio;
        const proteinEnd = carbsEnd + proteinRatio;
        const fatEnd = proteinEnd + fatRatio;

        // CSS conic-gradient 생성
        const gradient = `conic-gradient(
                    #f4eded 0% ${carbsEnd}%, 
                    #36827f ${carbsEnd}% ${proteinEnd}%, 
                    #f9db6b ${proteinEnd}% ${fatEnd}%
                )`;

        // 파이 차트에 적용
        $('#nutritionChart').css('background', gradient);
    }

    // 표시 될 데이터 업데이트
    function updateInfo() {
        $('.calorie-number').text(totalKcal);
        $('.nutrient-dot.carb-dot').siblings().text(`탄수화물 ${carbohydrateRatio}%`);
        $('.nutrient-dot.protein-dot').siblings().text(`단백질 ${proteinRatio}%`);
        $('.nutrient-dot.fat-dot').siblings().text(`지방 ${fatRatio}%`);
        $('.nutrient-value.carb-val').text(`${Math.round(totalCarbohydrate)}g`);
        $('.nutrient-value.protein-val').text(`${Math.round(totalProtein)}g`);
        $('.nutrient-value.fat-val').text(`${Math.round(totalFat)}g`);

        // 영양소 상세 모달에 데이터 업데이트
        $('#totalCaloriesValue').text(`${totalKcal} kcal`);
        $('#carbValue').text(`${Number.parseFloat(totalCarbohydrate).toFixed(1) % 1 === 0 ? Math.round(totalCarbohydrate) : Number.parseFloat(totalCarbohydrate).toFixed(1)} g`);
        $('#sugarValue').text(`${Number.parseFloat(totalSugar).toFixed(1) % 1 === 0 ? Math.round(totalSugar) : Number.parseFloat(totalSugar).toFixed(1)} g`);
        $('#proteinValue').text(`${Number.parseFloat(totalProtein).toFixed(1) % 1 === 0 ? Math.round(totalProtein) : Number.parseFloat(totalProtein).toFixed(1)} g`);
        $('#fatValue').text(`${Number.parseFloat(totalFat).toFixed(1) % 1 === 0 ? Math.round(totalFat) : Number.parseFloat(totalFat).toFixed(1)} g`);
        $('#saturatedFatValue').text(`${Number.parseFloat(totalSaturatedFat).toFixed(1) % 1 === 0 ? Math.round(totalSaturatedFat) : Number.parseFloat(totalSaturatedFat).toFixed(1)} g`);
        $('#transFatValue').text(`${Number.parseFloat(totalTransFat).toFixed(1) % 1 === 0 ? Math.round(totalTransFat) : Number.parseFloat(totalTransFat).toFixed(1)} g`);
        $('#natriumValue').text(`${totalNatrium} mg`);
        $('#cholesterolValue').text(`${Number.parseFloat(totalCholesterol).toFixed(1) % 1 === 0 ? Math.round(totalCholesterol) : Number.parseFloat(totalCholesterol).toFixed(1)} mg`);

    }

    // 음식 기록 삭제 수행
    function deleteFoodRecord(recordId, kcal) {
        // AJAX 요청
        $.ajax({
            url: '/api/record/delete_item',
            type: 'POST',
            data: {recordId: recordId, kcal: kcal},
            success: function (response) {
                window.showToast(response);
            },
            error: function (error) {
                if (error.status === 401) {
                    alert(error.responseText);
                    window.location.href = '/loginpage';
                    return;
                } else if (error.status === 403) {
                    window.showToast(error.responseText);
                    return;
                }
                window.showToast('식품 삭제에 실패했습니다. 다시 시도해주세요.');
                console.error('Error:', error);
            }
        });
    }

    // 삭제 버튼 클릭 이벤트 처리
    $('.remove-button').click(function () {
        const foodItem = $(this).closest('.food-item');
        const foodInfo = foodItem.find('.food-info');
        const recordId = foodInfo.attr('data-id');
        const foodKcal = parseInt(foodInfo.attr('data-kcal'));

        // 데이터 변경
        totalKcal -= parseInt(foodInfo.attr('data-kcal'));
        totalCarbohydrate -= parseFloat(foodInfo.attr('data-carbohydrate'));
        totalSugar -= parseFloat(foodInfo.attr('data-sugar'));
        totalProtein -= parseFloat(foodInfo.attr('data-protein'));
        totalFat -= parseFloat(foodInfo.attr('data-fat'));
        totalSaturatedFat -= parseFloat(foodInfo.attr('data-saturated_fat'));
        totalTransFat -= parseFloat(foodInfo.attr('data-trans_fat'));
        totalNatrium -= parseInt(foodInfo.attr('data-natrium'));
        totalCholesterol -= parseFloat(foodInfo.attr('data-cholesterol'));

        totalNutrition = totalCarbohydrate * 4 + totalProtein * 4 + totalFat * 9;
        // 데어터 없을 시
        if (totalNutrition <= 0) {
            carbohydrateRatio = 0;
            proteinRatio = 0;
            fatRatio = 0;
        } else {
            carbohydrateRatio = Math.round((totalCarbohydrate * 4 / totalNutrition) * 100);
            proteinRatio = Math.round((totalProtein * 4 / totalNutrition) * 100);
            fatRatio = 100 - carbohydrateRatio - proteinRatio;
        }

        // 음식 항목 삭제
        foodItem.fadeOut(300, function () {
            $(this).remove();
            // 삭제 요청
            deleteFoodRecord(recordId, foodKcal);

            // food-item이 없을 경우
            if ($('.food-item').length === 0) {
                location.href = `/record/search?meal_type=${mealType}`;
            }

            // 화면 업데이트
            updateInfo();
            drawNutritionChart();
        });
    });


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
        location.href = `/record/search?meal_type=${mealType}`;
    });


    // 템플릿 저장 버튼 클릭 이벤트
    $('#templateSubmitButton').click(function () {
        const templateName = $('#templateInput').val();
        // 템플릿 이름이 비어있을 경우
        if (templateName.trim() === '') {
            window.showToast("템플릿 이름을 입력해주세요.");
            return;
        }

        // AJAX 요청
        $.ajax({
            url: '/api/record/add_template',
            type: 'POST',
            data: {
                templateName: templateName,
                kcal: totalKcal
            },
            success: function (response) {
                window.showToast(response);
                // 모달 닫기
                closeModal();
            },
            error: function (error) {
                if (error.status === 401) {
                    alert(error.responseText);
                    window.location.href = '/loginpage';
                    return;
                }
                window.showToast('템플릿 저장에 실패했습니다. 다시 시도해주세요.');
                console.error('Error:', error);
            }
        });
    });

    // 영양소 상세 모달 관련 코드
    const nutritionModal = $('#nutritionModal');
    const templateModal = $('#templateModal');
    const openNutritionModalBtn = $('#openNutritionModal');
    const closeModalBtn = $('.modal-close');

    // 영양소 모달 열기
    openNutritionModalBtn.click(function () {
        // 모달 표시
        nutritionModal.addClass('show');
    });

    // 템플릿 저장 버튼 클릭 이벤트
    $('.template-button').click(function () {
        $('#templateInput').val(''); // 입력 필드 초기화
        // 모달 표시
        templateModal.addClass('show');

    });

    // 모달 닫기 함수
    function closeModal() {
        $('.modal-overlay').addClass('hide');

        setTimeout(() => {
            $('.modal-overlay').removeClass('show hide');
        }, 300);
    }

    // 모달 닫기 (X 버튼)
    closeModalBtn.click(closeModal);

    // 배경 클릭 시 모달 닫기
    $('.modal-overlay').click(function (event) {

        if (event.target === this) {
            closeModal();
        }
    });

});