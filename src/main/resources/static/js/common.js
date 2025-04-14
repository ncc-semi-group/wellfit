/* 공통 스크립트 작성 */
$(document).ready(function() {

    // 로그인 검증
    // 페이지가 signuppage 혹은 loginpage가 아닐 경우에만 로그인 검증을 수행

    if (!window.location.href.includes('signuppage') && !window.location.href.includes('loginpage')) {
        // 로그인 검증
        $.ajax({
            url: '/api/verification',
            type: 'POST',
            async: false,
            success: function (response) {
                console.log('로그인 검증 성공:', response);
            },
            error: function(xhr, status, error) {
                console.error('Login verification failed:', error);
                alert('로그인 후 이용해주세요.');
                window.location.href = '/loginpage';
            }
        });
    }

    // 토스트 메시지 표시 함수
    window.showToast = function(message, duration = 3000) {
        const toastMessage = $('#toast-message');
        const toastText = $('#toast-text');

        // 메시지 설정
        toastText.text(message);

        // 토스트 표시
        toastMessage.addClass('show');

        // 지정된 시간 후 토스트 숨기기
        setTimeout(() => {
            toastMessage.removeClass('show');
        }, duration);
    }

});