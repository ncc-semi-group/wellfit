$(document).ready(function () {
	$('.btn-signup').click(function () {
		let nickname = $('#nickname').val();
		let email = $('#email').val();
		let password = $('#password').val();

		if (!nickname) {
		    window.showToast("닉네임을 입력해주세요.");
		    return;
		} else if (!email) {
			window.showToast("이메일을 입력해주세요.");
			return;
		} else if (!email) {
			window.showToast("비밀번호를 입력해주세요.");
			return;
		}

		$.ajax({
			url: '/api/loginpage/signup',
			type: 'POST',
			data: {
				nickname: nickname,
                email: email,
                password: password
            },
			success: function(response) {
				window.showToast(response);
				setTimeout(() => {
                    window.location.href = '/welcome';
                }, 1000);
			},
			error: function(err) {
				if (err.status === 400) {
					window.showToast(err.responseText);
				} else {
					window.showToast('회원가입에 실패했습니다. 다시 시도해주세요.');
				}
			}
		});
	});
});
