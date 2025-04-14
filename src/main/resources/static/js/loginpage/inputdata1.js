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
