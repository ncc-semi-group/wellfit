$(document).ready(function () {


    // 탭 전환 기능
    $('.tab').click(function () {
        // 모든 탭에서 active 클래스 제거
        $('.tab').removeClass('active');
        // 클릭한 탭에 active 클래스 추가
        $(this).addClass('active');

        // 검색창 비우기
        $('#search-food-input').val('');

        // 모든 콘텐츠 숨기기
        $('.content').hide();

        // 클릭한 탭에 해당하는 콘텐츠 표시
        let tabId = $(this).attr('id');
        if (tabId === 'tab-favorites') {
            $('#favorites-content').show();
        } else if (tabId === 'tab-templates') {
            $('#templates-content').show();
        } else if (tabId === 'tab-custom') {
            $('#custom-content').show();
        }
    });

    // 초기 탭 설정 (즐겨찾기 탭 활성화)
    $('#tab-favorites').click();

    // 음식 항목 클릭 시 모달 열기 (중복 방지를 위해 off-on 사용)
    $('#search-results, .food-list').off('click', '.add-button').on('click', '.add-button', function () {
        // 음식 정보 가져오기
        let foodItem = $(this).parent().parent();
        let productName = foodItem.attr('data-name');
        let brandName = foodItem.attr('data-manufacturer_name');
        let kcal = foodItem.attr('data-kcal');
        let servingSize = foodItem.attr('data-serving_size');
        let serving = foodItem.attr('data-serving');
        let standard = foodItem.attr('data-standard');
        let defaultKcal = foodItem.attr('data-default_kcal');
        let carbohydrate = foodItem.attr('data-carbohydrate');
        let protein = foodItem.attr('data-protein');
        let fat = foodItem.attr('data-fat');
        let defaultCarbohydrate = Number(carbohydrate * (defaultKcal / kcal)).toFixed(1).replace(/\.0$/, '');
        let defaultProtein = Number(protein * (defaultKcal / kcal)).toFixed(1).replace(/\.0$/, '');
        let defaultFat = Number(fat * (defaultKcal / kcal)).toFixed(1).replace(/\.0$/, '');

        // 모달 정보 업데이트 - 제품명만 modal-title에 넣고 브랜드는 별도 span으로
        $('#foodAddModal .modal-title').html(productName);
        if (brandName) {
            $('#foodAddModal .modal-title').append(' <span class="brand-name">' + brandName + '</span>');
        }
        $('#foodAddModal .total-calories').text(defaultKcal + 'kcal');
        $('#foodAddModal #serving-size').text(serving);
        $('#foodAddModal #serving-standard').text(standard);
        $('#foodAddModal .nutrition-value.carb').text(defaultCarbohydrate + 'g');
        $('#foodAddModal .nutrition-value.protein').text(defaultProtein + 'g');
        $('#foodAddModal .nutrition-value.fat').text(defaultFat + 'g');
        $('.quantity-value').val(1);
        $('.serving-option').removeClass('active');
        $('#serving-size').addClass('active');

        // 데이터셋 추가
        let servingInfo = $('.serving-info');

        // 기존 데이터 속성 모두 제거
        servingInfo.removeData();

        servingInfo.attr('data-serving_size', servingSize);
        servingInfo.attr('data-kcal', kcal);
        servingInfo.attr('data-default_kcal', defaultKcal);
        servingInfo.attr('data-carbohydrate', carbohydrate);
        servingInfo.attr('data-protein', protein);
        servingInfo.attr('data-fat', fat);
        servingInfo.attr('data-id', foodItem.attr('data-id'));
        servingInfo.attr('data-food_type', foodItem.attr('data-food_type'));


        // 음식 상세 모달 내부의 플러스 아이콘 클릭 이벤트를 위해 별도 변수에 foodItem 저장
        let currentDetailFood;

        // 플러스 아이콘 클릭 시 이벤트는 한 번만 바인딩 (중복 방지)
        $('.detail-button').off('click').on('click', function () {
            // 모달 닫기
            closeModal();

            // 추가 모달 열기
            setTimeout(function () {
                // 저장해둔 현재 음식 아이템에서 add-button 찾아 클릭
                if (currentDetailFood) {
                    currentDetailFood.find('.food-info').trigger('click');
                }
            }, 320); // 모달 닫힘 애니메이션 후 실행
        });

        // 현재 선택된 음식 아이템 저장
        currentDetailFood = foodItem;

        // 음식 추가 (이벤트 핸들러 중복 등록 방지)
        $(document).off('click', '.add-button-large').on('click', '.add-button-large', function () {
            // 중복 클릭 방지
            $(this).prop('disabled', true);

            let servingInfo = $('.serving-info');
            let foodId = servingInfo.attr('data-id');
            let servingSize = servingInfo.attr('data-serving_size');
            let foodType = servingInfo.attr('data-food_type') === 'user' ? 'user' : 'system';
            let amount = 0;
            let serving = 0;
            let kcal = parseInt($('.total-calories').text().replace('kcal', '').trim());

            if ($('#serving-size').hasClass('active')) {
                serving = parseInt($('.quantity-value').val());
                amount = serving * servingSize;
            } else {
                amount = parseInt($('.quantity-value').val());
            }

            $.ajax({
                url: '/api/record/add_item',
                type: 'POST',
                data: {
                    foodId: foodId,
                    foodType: foodType,
                    amount: amount,
                    serving: serving,
                    kcal: kcal
                },
                success: function (response) {
                    window.showToast('음식이 추가되었습니다.');
                    console.log(response);
                    // 모달 닫기
                    closeModal();
                    // 음식 개수 업데이트
                    updateFoodCount();
                },
                error: function (xhr, status, error) {
                    if (xhr.status === 401) {
                        alert(xhr.responseText);
                        window.location.href = '/loginpage';
                        return;
                    } else if (xhr.status === 403) {
                        window.showToast(xhr.responseText);
                        // 모달 닫기
                        closeModal();
                        return;
                    }
                    window.showToast('음식 추가 중 오류가 발생했습니다. 다시 시도해주세요.');
                    console.error('음식 추가 API 오류:', status, error);
                    console.log('응답 상태:', xhr.status);
                },
                complete: function () {
                    // 요청 완료 후 버튼 다시 활성화
                    $('.add-button-large').prop('disabled', false);
                }
            });
        });


        // 모달 표시
        $('#foodAddModal').addClass('show');
    });

    // 수량 감소 버튼
    $('.quantity-btn.decrease').click(function () {
        let quantity = parseInt($('.quantity-value').val());
        if ($('#serving-size').hasClass('active') && quantity > 1) {
            $('.quantity-value').val(quantity - 1).trigger('change');
        } else if (quantity > 10) {
            $('.quantity-value').val(quantity - 10).trigger('change');
        }
    });

    // 수량 증가 버튼
    $('.quantity-btn.increase').click(function () {
        let quantity = parseInt($('.quantity-value').val());
        if ($('#serving-size').hasClass('active')) {
            $('.quantity-value').val(quantity + 1).trigger('change');
        } else {
            $('.quantity-value').val(quantity + 10).trigger('change');
        }

    });

    // 수량 입력 필드 변경 시
    $('.quantity-value').on('change input', function () {
        let servingInfo = $('.serving-info');

        let kcal = parseInt(servingInfo.attr('data-kcal'));
        let defaultKcal = parseInt(servingInfo.attr('data-default_kcal'));
        let carbohydrate = parseInt(servingInfo.attr('data-carbohydrate'));
        let protein = parseInt(servingInfo.attr('data-protein'));
        let fat = parseInt(servingInfo.attr('data-fat'));
        let quantityValue = $(this).val() === '' ? 0 : parseInt($(this).val());

        let totalKcal;
        if ($('#serving-size').hasClass('active')) {
            totalKcal = quantityValue * defaultKcal;
        } else {
            totalKcal = Math.round(quantityValue / 100 * kcal);
        }
        let totalCarbohydrate = Number(carbohydrate * (totalKcal / kcal)).toFixed(1).replace(/\.0$/, '');
        let totalProtein = Number(protein * (totalKcal / kcal)).toFixed(1).replace(/\.0$/, '');
        let totalFat = Number(fat * (totalKcal / kcal)).toFixed(1).replace(/\.0$/, '');

        $('#foodAddModal .total-calories').text(totalKcal + 'kcal');
        $('#foodAddModal .nutrition-value.carb').text(totalCarbohydrate + 'g');
        $('#foodAddModal .nutrition-value.protein').text(totalProtein + 'g');
        $('#foodAddModal .nutrition-value.fat').text(totalFat + 'g');
    })

    // 인분/g 선택 옵션
    $('.serving-option').click(function () {
        $('.serving-option').removeClass('active');
        $(this).addClass('active');
        if ($('#serving-size').hasClass('active')) {
            $('.quantity-value').val(1).trigger('change');
        } else {
            let servingSize = $(this).parent().attr('data-serving_size');
            $('.quantity-value').val(servingSize).trigger('change');
        }
    });


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


    // 음식 상세 모달 열기
    $('#search-results, .food-list').off('click', '.food-info').on('click', '.food-info', function () {
        let foodItem = $(this).closest('.food-item');
        let productName = foodItem.attr('data-name');
        let brandName = foodItem.attr('data-manufacturer_name');

        // 모달 정보 업데이트 - 제품명만 modal-title에 넣고 브랜드는 별도 span으로
        $('#foodDetailModal .modal-title').html(productName);
        if (brandName) {
            $('#foodDetailModal .modal-title').append(' <span class="brand-name">' + brandName + '</span>');
        }

        let servingSize = foodItem.attr('data-serving_size');
        let serving = foodItem.attr('data-serving');
        let detailKcal = Math.round(foodItem.attr('data-kcal') * (servingSize / 100));
        let detailFat = Number(foodItem.attr('data-fat') * (servingSize / 100))
            .toFixed(1).replace(/\.0$/, '');
        let detailSaturatedFat = Number(foodItem.attr('data-saturated_fat') * (servingSize / 100))
            .toFixed(1).replace(/\.0$/, '');
        let detailTransFat = Number(foodItem.attr('data-trans_fat') * (servingSize / 100))
            .toFixed(1).replace(/\.0$/, '');
        let detailCholesterol = Number(foodItem.attr('data-cholesterol') * (servingSize / 100))
            .toFixed(1).replace(/\.0$/, '');
        let detailNatrium = Math.round(foodItem.attr('data-natrium') * (servingSize / 100));
        let detailCarbohydrate = Number(foodItem.attr('data-carbohydrate') * (servingSize / 100))
            .toFixed(1).replace(/\.0$/, '');
        let detailSugar = Number(foodItem.attr('data-sugar') * (servingSize / 100))
            .toFixed(1).replace(/\.0$/, '');
        let detailProtein = Number(foodItem.attr('data-protein') * (servingSize / 100))
            .toFixed(1).replace(/\.0$/, '');

        let detailFatP = Math.round(detailFat / 54 * 100);
        let detailSaturatedFatP = Math.round(detailSaturatedFat / 15 * 100);
        let detailCholesterolP = Math.round(detailCholesterol / 300 * 100);
        let detailNatriumP = Math.round(detailNatrium / 2000 * 100);
        let detailCarbohydrateP = Math.round(detailCarbohydrate / 324 * 100);
        let detailSugarP = Math.round(detailSugar / 100 * 100);
        let detailProteinP = Math.round(detailProtein / 55 * 100);

        $('#foodDetailModal .detail-serving').text('1' + serving);
        $('#foodDetailModal .detail-kcal').text(detailKcal + 'kcal');
        $('#foodDetailModal .detail-fat').text(detailFat);
        $('#foodDetailModal .detail-fat-p').text(detailFatP);
        $('#foodDetailModal .detail-saturated-fat').text(detailSaturatedFat);
        $('#foodDetailModal .detail-saturated-fat-p').text(detailSaturatedFatP);
        $('#foodDetailModal .detail-trans-fat').text(detailTransFat);
        $('#foodDetailModal .detail-cholesterol').text(detailCholesterol);
        $('#foodDetailModal .detail-cholesterol-p').text(detailCholesterolP);
        $('#foodDetailModal .detail-natrium').text(detailNatrium);
        $('#foodDetailModal .detail-natrium-p').text(detailNatriumP);
        $('#foodDetailModal .detail-carbohydrate').text(detailCarbohydrate);
        $('#foodDetailModal .detail-carbohydrate-p').text(detailCarbohydrateP);
        $('#foodDetailModal .detail-sugar').text(detailSugar);
        $('#foodDetailModal .detail-sugar-p').text(detailSugarP);
        $('#foodDetailModal .detail-protein').text(detailProtein);
        $('#foodDetailModal .detail-protein-p').text(detailProteinP);


        let foodId = foodItem.attr('data-id');
        let foodType = foodItem.attr('data-food_type') === 'user' ? 'user' : 'system';

        $('#foodDetailModal .header-icons .bi-pencil-square').show();
        // 직접 등록 음식 아닐 경우 수정 버튼 숨기기
        if (foodType !== 'user') {
            $('#foodDetailModal .header-icons .bi-pencil-square').hide();
        }

        // 즐겨찾기 아이콘 업데이트
        $.ajax({
            url: '/api/record/is_favorite',
            type: 'POST',
            data: {
                foodId: foodId,
                foodType: foodType
            },
            success: function (data) {
                if (data === true) {
                    $('#foodDetailModal .favorite-icon').html('<i class="bi bi-star-fill"></i>');
                } else {
                    $('#foodDetailModal .favorite-icon').html('<i class="bi bi-star"></i>');
                }
            }, error: function (xhr, status, error) {
                if (xhr.status === 401) {
                    alert(xhr.responseText);
                    window.location.href = '/loginpage';
                    return;
                }
                console.error('즐겨찾기 상태 조회 API 오류:', status, error);
                console.log('응답 상태:', xhr.status);
            }
        });

        // 즐겨찾기 아이콘 클릭 시
        $('#foodDetailModal .favorite-icon').off('click').on('click', function () {
            // 이미 처리 중인 경우 클릭 방지
            if ($(this).hasClass('processing')) {
                return;
            }

            let favoriteIcon = $(this);
            let isFavorite = favoriteIcon.find('.bi').hasClass('bi-star-fill');

            // 처리 중임을 표시하고 스타일 변경
            favoriteIcon.addClass('processing').css('opacity', '0.5');

            $.ajax({
                url: '/api/record/favorite',
                type: 'POST',
                data: {
                    foodId: foodId,
                    foodType: foodType,
                    isFavorite: isFavorite
                },
                success: function (response) {
                    console.log(response);
                    if (isFavorite) {
                        window.showToast('즐겨찾기에서 삭제되었습니다.');
                        favoriteIcon.html('<i class="bi bi-star"></i>');
                    } else {
                        window.showToast('즐겨찾기에 추가되었습니다.');
                        favoriteIcon.html('<i class="bi bi-star-fill"></i>');
                    }
                },
                error: function (xhr, status, error) {
                    if (xhr.status === 401) {
                        alert(xhr.responseText);
                        window.location.href = '/loginpage';
                        return;
                    }
                    // 콘솔에 자세한 오류 정보 기록
                    console.error('즐겨찾기 추가 API 오류:', status, error);
                    console.log('응답 상태:', xhr.status);
                    // 오류 발생 시 사용자에게 알림
                    alert('즐겨찾기 처리 중 오류가 발생했습니다.');
                },
                complete: function () {
                    // 처리 완료 후 상태 및 스타일 복원
                    favoriteIcon.removeClass('processing').css('opacity', '1');
                }
            });
        });

        // 수정 버튼 클릭 시
        $('#foodDetailModal .header-icons .bi-pencil-square').on('click', function () {
            location.href = `/record/add_form?type=edit&food_id=${foodId}`;
        });

        // 음식 상세 모달 내부의 플러스 아이콘 클릭 이벤트를 위해 별도 변수에 foodItem 저장
        let currentDetailFood;

        // 플러스 아이콘 클릭 시 이벤트는 한 번만 바인딩 (중복 방지)
        $('.plus-icon').off('click').on('click', function () {
            // 모달 닫기
            closeModal();

            // 추가 모달 열기
            setTimeout(function () {
                // 저장해둔 현재 음식 아이템에서 add-button 찾아 클릭
                if (currentDetailFood) {
                    currentDetailFood.find('.add-button').trigger('click');
                }
            }, 320); // 모달 닫힘 애니메이션 후 실행
        });

        // 현재 선택된 음식 아이템 저장
        currentDetailFood = foodItem;
        // 모달 표시
        $('#foodDetailModal').addClass('show');
    });


    // 뒤로 가기 버튼
    $('.back-arrow').click(function () {
        const queryString = window.location.href.split('?')[1];
        location.href = `/record/food_record?${queryString}`;
    });

    // 등록 버튼
    $('.register-button').click(function () {
        location.href = '/record/add_form?type=add';
    });

    // 하단 버튼
    $('.bottom-button').click(function () {
        // 쿼리 스트링 가져오기
        const queryString = window.location.href.split('?')[1];
        location.href = `/record/food_record?${queryString}`;
    });

    // 템플릿 아이템 클릭 시
    $('.template-item').click(function () {
        let templateId = $(this).attr('data-id');
        let name = $(this).find('.food-name').text();
        location.href = `/record/template_detail?template_id=${templateId}&name=${name}`;
    });


    // 음식 갯수 카운트 갱신
    function updateFoodCount() {
        $.ajax({
            url: '/api/record/get_items_count',
            type: 'POST',
            success: function (data) {
                $('.bottom-button').text(data);
            },
            error: function (xhr, status, error) {
                if (xhr.status === 401) {
                    alert(xhr.responseText);
                    window.location.href = '/loginpage';
                    return;
                }
                console.error('음식 개수 조회 API 오류:', error);
            }
        });
    }

    // 현재 페이지 번호를 전역 변수로 관리
    let currentPageNo = 0;
    let lastQuery = '';

    // 검색창에 입력 시 (실시간 검색, 검색창에 두글자 이상 입력하고, 500ms 후에 검색)
    let searchTimeout;
    $('#search-food-input').on('input', function () {
        clearTimeout(searchTimeout);
        let query = $(this).val();
        if (query.length >= 2) {
            searchTimeout = setTimeout(function () {
                // 모든 콘텐츠 숨기기
                $('.content').hide();
                // 검색 결과 콘텐츠만 표시
                $('#search-content').show();

                currentPageNo = 0; // 새 검색어일 경우 페이지 번호 초기화
                lastQuery = query; // 마지막 검색어 저장
                countFood(query); // 음식 개수 조회 함수 호출
                $('#search-results').empty(); // 검색 결과 초기화
                searchFood(query, currentPageNo); // 검색 함수 호출
            }, 500);
        } else {
            $('#search-results').empty(); // 두 글자 미만일 경우 결과 비우기
            $("#search-content .total-count").text("두글자 이상 입력해주세요.");
        }
    });


    function searchFood(query, pageNo) {

        $.ajax({
            url: '/api/foods/search',
            type: 'GET',
            data: {keyword: query, pageNo: pageNo},
            success: function (data) {
                let html = '';
                $.each(data, function (i, e) {
                    // null 체크
                    let manufacturerName = e.manufacturerName != null ? e.manufacturerName : '';
                    let standard = e.standard === '100g' ? 'g' : 'ml';
                    let servingSize;
                    if (e.servingSize != null && e.servingSize === 0) servingSize = e.servingSize;
                    else if (e.weight != null && e.weight <= 350) servingSize = e.weight;
                    else servingSize = 100;
                    let serving = `인분 (${servingSize}${standard})`;

                    // 칼로리 정수로
                    let kcal = servingSize === 100 ? e.kcal : Math.round(e.kcal * (servingSize / 100));
                    html += `
                    <div class="food-item"
                        data-id="${e.id}"
                        data-name="${e.name}"
                        data-manufacturer_name="${manufacturerName}"
                        data-kcal="${e.kcal}"
                        data-default_kcal="${kcal}"
                        data-carbohydrate="${e.carbohydrate}"
                        data-protein="${e.protein}"
                        data-fat="${e.fat}"
                        data-standard="${standard}"
                        data-serving_size="${servingSize}"
                        data-serving="${serving}"
                        data-sugar="${e.sugar}"
                        data-saturated_fat="${e.saturatedFat}"
                        data-trans_fat="${e.transFat}"
                        data-cholesterol="${e.cholesterol}"
                        data-natrium="${e.natrium}">
                        <div class="food-info">
                            <div class="product-container">
                                <span class="product-name">${e.name}</span>
                                <span class="brand-name">${manufacturerName}</span>
                            </div>
                            <div class="food-serving">1${serving}</div>
                        </div>
                        <div class="food-actions">
                            <div class="food-calories">${kcal}kcal</div>
                            <div class="add-button">
                                <i class="bi bi-plus"></i>
                            </div>
                        </div>
                    </div>
                    `;
                });


                if (pageNo === 0) {
                    // 첫 페이지인 경우 결과를 대체
                    $('#search-results').append(html);
                } else {
                    // 추가 페이지인 경우 결과를 아래에 추가
                    $('#search-results').append(html);
                }
                isSearchLoading = false; // 로딩 완료

            },
            error: function () {
                // 로딩 인디케이터 제거
                $('#loading').remove();
                console.error('검색 중 오류 발생');

                // 로딩 완료 상태로 변경
                isSearchLoading = false;
            }
        });
    }

    // 검색 결과 개수 조회
    function countFood(query) {
        $.ajax({
            url: '/api/foods/count',
            type: 'GET',
            data: {keyword: query},
            success: function (data) {
                $("#search-content .total-count").text("총 " + data + "개");
            },
            error: function () {
                console.error('음식 개수 조회 중 오류 발생');
            }
        });
    }


    let isSearchLoading = false; // 로딩 중 여부

    // 스크롤 이벤트 처리 - 페이지 하단에 도달했을 때 다음 페이지 로드
    $(window).scroll(function () {
        if (isSearchLoading) return; // 로딩 중이면 무시

        // 창의 하단 위치
        let windowBottom = $(window).scrollTop() + $(window).height();
        // 문서의 전체 높이
        let documentHeight = $(document).height();

        // 스크롤이 문서 하단에 가까워졌을 때
        if (windowBottom >= documentHeight * 0.9 && lastQuery.length >= 2) {
            // 로딩 중이 아닐 때만 실행
            isSearchLoading = true;
            currentPageNo++; // 페이지 번호 증가
            searchFood(lastQuery, currentPageNo); // 다음 페이지 검색
        }

    });

});