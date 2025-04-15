$(document).ready(function() {
    // 현재 선택된 기간
    let currentPeriod = 'daily';

    // 현재 체중, 목표 체중 가져오기
    let currentWeight = parseFloat($('.goal-info').data('current_weight'));
    $('#currentWeight').text('목표 ' + currentWeight + ' kg');
    let targetWeight = parseFloat($('.goal-info').data('target_weight'));
    let targetLeft = Math.abs(currentWeight - targetWeight);
    if (targetLeft <= 1) {
        $('#targetLeft').text('목표 달성!');
        $('#targetEmoji').text('🏅');
    } else {
        $('#targetLeft').text('목표까지 ' + targetLeft + ' kg');
        $('#targetEmoji').text('🚀');
    }

    // Chart.js 기본 설정
    Chart.defaults.font.family = 'Arial';
    Chart.defaults.font.size = 12;

    // 차트 객체 저장 변수
    let weightChart, intakeChart, burnedChart;

    // 모든 차트 생성 함수
    function createAllCharts() {
        createChart('weightChart', '체중', '#4a7aff');
        createChart('intakeChart', '섭취 칼로리', '#ff6b6b');
        createChart('burnedChart', '소모 칼로리', '#51cf66');
    }

    // 차트 생성 함수
    function createChart(chartId, label, color, chartType = 'line') {
        const ctx = document.getElementById(chartId).getContext('2d');

        // 데이터와 설정
        const data = {
            labels: [],  // 초기 라벨은 빈 배열
            datasets: [{
                label: label,
                data: [],  // 초기 데이터는 빈 배열
                // 공통 속성
                backgroundColor: chartType === 'bar' ? color : color,
                borderColor: color,
                // 차트 타입별 속성
                ...(chartType === 'line' ? {
                    fill: false,
                    tension: 0.2,  // 곡선 부드러움 정도
                    pointBackgroundColor: color,
                    pointBorderColor: '#fff',
                    pointBorderWidth: 2,
                    pointRadius: 4,
                    pointHoverRadius: 6,
                } : {
                    // 막대 차트 속성
                    borderWidth: 1,
                    borderRadius: 4,
                    maxBarThickness: 25,
                    barPercentage: 0.7
                }),
                datalabels: {
                    color: '#333',
                    anchor: chartType === 'line' ? 'end' : 'top',
                    align: 'top',
                    formatter: function(value) {
                        return value;
                    }
                }
            }]
        };
        // 차트 옵션
        const options = {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    enabled: true,
                    displayColors: false,
                    backgroundColor: 'rgba(0, 0, 0, 0.7)',
                    titleFont: {
                        size: 12
                    },
                    bodyFont: {
                        size: 12
                    },
                    callbacks: {
                        label: function(context) {
                            let label = '';
                            if (context.dataset.label) {
                                label += context.dataset.label + ': ';
                            }
                            if (context.parsed.y !== null) {
                                label += context.parsed.y;
                            }
                            return label;
                        }
                    }
                },
                datalabels: {
                    display: true,
                    font: {
                        size: 10,
                        weight: 'bold'
                    }
                }
            },
            scales: {
                x: {
                    grid: {
                        display: false
                    },
                    ticks: {
                        font: {
                            size: 10
                        }
                    }
                },
                y: {
                    display: false,  // y축 숨기기
                    grid: {
                        display: false  // y축 그리드 숨기기
                    },
                    // 차트의 위아래 여백을 늘려 데이터가 잘리지 않도록 설정
                    suggestedMin: function(context) {
                        const min = Math.min(...context.chart.data.datasets[0].data);
                        return min * 0.95;  // 최소값보다 약간 작게 설정하여 여백 확보
                    },
                    suggestedMax: function(context) {
                        const max = Math.max(...context.chart.data.datasets[0].data);
                        return max * 1.05;  // 최대값보다 약간 크게 설정하여 여백 확보
                    }
                }
            },
            // 차트 전체 여백 설정
            layout: {
                padding: {
                    top: 20,  // 위쪽 여백 추가
                    bottom: 5  // 아래쪽 여백 추가
                }
            }
        };
        // 차트 생성
        const chart = new Chart(ctx, {
            type: chartType,
            data: data,
            options: options,
            plugins: [{
                id: 'customLabels',
                afterDatasetsDraw: function(chart) {
                    const ctx = chart.ctx;
                    ctx.save();
                    ctx.textAlign = 'center';
                    ctx.textBaseline = 'bottom';
                    ctx.font = 'bold 10px Arial';

                    chart.data.datasets.forEach(function(dataset, datasetIndex) {
                        const meta = chart.getDatasetMeta(datasetIndex);
                        if (!meta.hidden) {
                            meta.data.forEach(function(element, index) {
                                const value = dataset.data[index];
                                // null이 아닌 데이터에만 라벨 표시
                                if (value !== null && value !== undefined) {
                                    ctx.fillStyle = '#333';
                                    // 차트 타입에 따라 데이터 라벨 위치 조정
                                    const yOffset = chart.config.type === 'bar' ? -5 : -12;
                                    ctx.fillText(value, element.x, element.y + yOffset);
                                }
                            });
                        }
                    });
                    ctx.restore();
                }
            }]
        });

        // 차트 객체 반환
        return chart;
    }


    let statisticsData;
    fetchDataFromDB();
    console.log(statisticsData);

    // 데이터베이스에서 데이터를 가져오는 함수
    function fetchDataFromDB() {
        $.ajax({
            url: '/api/record/getStatistics',
            method: 'POST',
            async: false,  // 동기식으로 데이터 가져오기
            success: function(response) {
                // json 데이터 파싱
                statisticsData = JSON.parse(response);
            },
            error: function(error) {
                console.error('데이터 가져오기 실패:', error);
            }
        });
    }

    // 차트 데이터 업데이트 함수
    function updateChartData(period) {
        const data = statisticsData[period];

        // 체중 차트 업데이트 (범위를 좁게 설정하여 변화를 강조)
        updateChart(weightChart, data.dates, data.weights);

        // 섭취 칼로리 차트 업데이트
        updateChart(intakeChart, data.dates, data.intake);

        // 소모 칼로리 차트 업데이트
        updateChart(burnedChart, data.dates, data.burned);
    }
    // 개별 차트 업데이트 함수
    function updateChart(chart, labels, data) {
        chart.data.labels = labels;
        chart.data.datasets[0].data = data;

        // 누락된 데이터 처리 (보간 또는 이전 값 사용)
        handleMissingData(chart.data.datasets[0].data, chart.config.type);

        // 체중 차트인 경우, y축 범위를 조정하여 변화를 더 뚜렷하게 표시
        if (chart === weightChart) {
            // 체중 데이터의 최소/최대값 계산
            const weightData = chart.data.datasets[0].data;
            const min = Math.min(...weightData);
            const max = Math.max(...weightData);
            const range = max - min;

            // 좁은 범위로 y축 설정 (변화가 더 뚜렷하게 보이도록)
            // 최소값에서 약간 아래로, 최대값에서 약간 위로 설정
            chart.options.scales.y.suggestedMin = min - (range * 0.1);
            chart.options.scales.y.suggestedMax = max + (range * 0.1);
        }

        chart.update();
    }

function handleMissingData(dataArray, chartType) {
    for (let i = 0; i < dataArray.length; i++) {
        // 데이터가 0인 경우 null로 변경 (차트에 표시하지 않음)
        if (dataArray[i] === 0) {
            dataArray[i] = null;
        }
    }

    // 라인 차트에서만 null 데이터 보간 처리
    if (chartType === 'line') {
        for (let i = 0; i < dataArray.length; i++) {
            if (dataArray[i] === null) {
                // 앞뒤로 유효한 값이 있는 경우만 보간
                let prevValue = null;
                let nextValue = null;
                let prevIndex = -1;
                let nextIndex = -1;

                // 이전 유효한 값 찾기
                for (let j = i - 1; j >= 0; j--) {
                    if (dataArray[j] !== null) {
                        prevValue = dataArray[j];
                        prevIndex = j;
                        break;
                    }
                }

                // 다음 유효한 값 찾기
                for (let j = i + 1; j < dataArray.length; j++) {
                    if (dataArray[j] !== null) {
                        nextValue = dataArray[j];
                        nextIndex = j;
                        break;
                    }
                }

                // 앞뒤로 값이 모두 있을 때만 보간 처리
                if (prevValue !== null && nextValue !== null) {
                    // 앞뒤 값 사이의 위치에 따라 비례 계산으로 보간
                    const totalGap = nextIndex - prevIndex;
                    const position = i - prevIndex;
                    const ratio = position / totalGap;
                    dataArray[i] = prevValue + (nextValue - prevValue) * ratio;
                    // 소수점 한 자리로 반올림
                    dataArray[i] = Math.round(dataArray[i] * 10) / 10;
                }
            }
        }
    }
}


    // 초기 차트 생성
    weightChart = createChart('weightChart', '체중', '#4a7aff', 'line');
    intakeChart = createChart('intakeChart', '섭취량', '#ff6b6b', 'bar');
    burnedChart = createChart('burnedChart', '소모량', '#51cf66', 'bar');

    // 초기 데이터 설정
    updateChartData(currentPeriod);

    // 탭 버튼 클릭 이벤트
    $('.tab-button').click(function() {
        // 이전 활성 버튼 비활성화
        $('.tab-button').removeClass('active');

        // 클릭한 버튼 활성화
        $(this).addClass('active');

        // 현재 기간 설정
        currentPeriod = $(this).data('period');

        // 차트 데이터 업데이트
        updateChartData(currentPeriod);

        // 정보 표시 업데이트
        updateInfo(currentPeriod);

    });


    // 정보 표시 업데이트 함수
    function updateInfo(currentPeriod) {
        // 정보 표시 업데이트
        let eatKcal = $('#eatKcal');
        let burnedKcal = $('#burnedKcal');
        let infoText = $('#infoText');
        if (currentPeriod === 'daily') {
            eatKcal.text((statisticsData.daily.intake[6] == null ? 0 : statisticsData.daily.intake[6]) + ' kcal');
            burnedKcal.text((statisticsData.daily.burned[6] == null ? 0 : statisticsData.daily.burned[6]) + ' kcal');
            infoText.text('오늘 하루 동안');
        } else if (currentPeriod === 'weekly') {
            eatKcal.text((statisticsData.weekly.intake[6] == null ? 0 : statisticsData.weekly.intake[6]) + ' kcal');
            burnedKcal.text((statisticsData.weekly.burned[6] == null ? 0 : statisticsData.weekly.burned[6]) + ' kcal');
            infoText.text('일주일 동안 평균');
        } else if (currentPeriod === 'monthly') {
            eatKcal.text((statisticsData.monthly.intake[6] == null ? 0 : statisticsData.monthly.intake[6]) + ' kcal');
            burnedKcal.text((statisticsData.monthly.burned[6] == null ? 0 : statisticsData.monthly.burned[6]) + ' kcal');
            infoText.text('한 달 동안 평균');
        }
    }

    // 초기 정보 표시 업데이트
    updateInfo(currentPeriod);
});