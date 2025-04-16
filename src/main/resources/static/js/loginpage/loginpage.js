$(document).ready(function () {
    $('.btn-login').click(function () {
        let email = $('#email').val();
        let password = $('#password').val();

        if (!email || !password) {
            window.showToast("이메일과 비밀번호를 입력해주세요.");
            return;
        }

        $.ajax({
            url: '/api/loginpage/logincheck',
            type: 'POST',
            data: {
                email: email,
                password: password
            },
            success: function (response) {
                window.showToast("로그인 성공!");
                setTimeout(() => {
                    window.location.href = '/mainpage';
                }, 1000);
            },
            error: function (error) {
                if (error.status === 401) {
                    window.showToast(error.responseText);
                } else {
                    window.showToast("로그인 중 문제가 발생했습니다.");
                    console.error(error);
                }
            }
        });
    });
});
