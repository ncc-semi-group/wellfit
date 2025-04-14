$(document).ready(function () {

    // 쿼리 스트링에서 templateId 가져오기
    const urlParams = new URLSearchParams(window.location.search);
    const templateId = parseInt(urlParams.get('template_id'));

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
                    white 0% ${carbsEnd}%, 
                    #739072 ${carbsEnd}% ${proteinEnd}%, 
                    #FBE086 ${proteinEnd}% ${fatEnd}%
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

    // 뒤로가기 버튼 클릭 이벤트
    $('.back-button').click(function () {
        history.back();
    });

    // 추가하기 버튼 클릭 이벤트
    $('.add-button').click(function () {
        // 버튼 비활성화
        $(this).prop('disabled', true);


        $.ajax({
            url: '/api/record/add_template_food',
            type: 'POST',
            data: {templateId: templateId, kcal: totalKcal},
            success: function (response) {
                // 성공적으로 추가된 경우
                window.showToast(response);
                setTimeout(() => {
                    // 이전 페이지로 돌아간 후 새로고침
                    window.location.replace(document.referrer); // 이전 페이지로 돌아가기
                }, 1000);
            },
            error: function (error) {
                if (error.status === 401) {
                    alert(error.responseText);
                    window.location.href = '/loginpage';
                    return;
                } else if (error.status === 403) {
                    window.showToast(error.responseText);
                    // 버튼 활성화
                    $(this).prop('disabled', false);
                    return;
                }
                // 오류 발생 시
                window.showToast('식품 추가에 실패했습니다. 다시 시도해주세요.');
                console.error('Error adding food:', error);

                // 버튼 활성화
                $(this).prop('disabled', false);
            }
        });
    });


    // 템플릿 삭제 버튼 클릭 이벤트
    $('.template-button').click(function () {
        if (!confirm("템플릿을 삭제하시겠습니까?")) return;

        // 버튼 비활성화
        $(this).prop('disabled', true);

        $.ajax({
            url: '/api/record/delete_template',
            type: 'POST',
            data: {templateId: templateId},
            success: function (response) {
                // 성공적으로 삭제된 경우
                window.showToast(response);
                setTimeout(() => {
                    // 이전 페이지로 돌아간 후 새로고침
                    window.location.replace(document.referrer); // 이전 페이지로 돌아가기
                }, 1000);
            },
            error: function (error) {
                // 오류 발생 시
                window.showToast('템플릿 삭제에 실패했습니다. 다시 시도해주세요.');
                console.error('Error deleting template:', error);

                // 버튼 활성화
                $(this).prop('disabled', false);
            }
        });
    });

    // 영양소 상세 모달 관련 코드
    const nutritionModal = $('#nutritionModal');
    const openNutritionModalBtn = $('#openNutritionModal');
    const closeModalBtn = $('.modal-close');

    // 영양소 모달 열기
    openNutritionModalBtn.click(function () {
        // 모달 표시
        nutritionModal.addClass('show');
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