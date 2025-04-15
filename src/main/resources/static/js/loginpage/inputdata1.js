const activityDescriptions = {
	"Sedentary": {
		main: "거의 앉아서 생활",
		sub: "(사무직, 재택근무 등)"
	},
	"Lightly Active": {
		main: "앉아 있지만 가벼운 이동 포함",
		sub: "(출퇴근, 가벼운 집안일)"
	},
	"Moderately Active": {
		main: "서서 일하거나 자주 걸음",
		sub: "(교사, 매장 직원 등)"
	},
	"Very Active": {
		main: "육체노동 또는 하루 종일 움직임",
		sub: "(건설 노동자, 배달원 등)"
	}
};

document.querySelectorAll('input[name="activityLevel"]').forEach((radio) => {
	radio.addEventListener('change', () => {
		const selected = activityDescriptions[radio.id];
		document.getElementById('activity-main').textContent = selected.main;
		document.getElementById('activity-sub').textContent = selected.sub;
	});
});

document.querySelectorAll('.weight-input').forEach(input => {
    input.addEventListener('input', () => {
        const value = input.value;
        if (value.includes('.')) {
            const [intPart, decimalPart] = value.split('.');
            if (decimalPart.length > 1) {
                input.value = `${intPart}.${decimalPart[0]}`;  // 소수점 한 자리까지만 유지
            }
        }
    });
});

document.querySelector('form').addEventListener('submit', function (e) {
    const gender = document.querySelector('input[name="gender"]:checked');
    const age = document.querySelector('input[name="age"]');
    const height = document.querySelector('input[name="height"]');
    const currentWeight = document.querySelector('input[name="currentWeight"]');
    const goalWeight = document.querySelector('input[name="goalWeight"]');
    const activity = document.querySelector('input[name="activityLevel"]:checked');

    // 유효성 검사
    if (!gender || !age.value || !height.value || !currentWeight.value || !goalWeight.value || !activity) {
        e.preventDefault(); // 폼 전송 막기
        window.showToast("모든 필드를 입력해주세요.");
        return;
    }

    // 숫자 유효성 검사 (예: 음수 불가, 나이 정수, 체중 실수 등)
    if (parseInt(age.value) <= 0 || parseFloat(height.value) <= 0 || parseFloat(currentWeight.value) <= 0 || parseFloat(goalWeight.value) <= 0) {
        e.preventDefault();
        window.showToast("입력값은 0보다 커야 합니다.");
        return;
    }
});
