const activityDescriptions = {
	"very-low": {
		main: "거의 앉아서 생활",
		sub: "(사무직, 재택근무 등)"
	},
	"low": {
		main: "앉아 있지만 가벼운 이동 포함",
		sub: "(출퇴근, 가벼운 집안일)"
	},
	"medium": {
		main: "서서 일하거나 자주 걸음",
		sub: "(교사, 매장 직원 등)"
	},
	"high": {
		main: "육체노동 또는 하루 종일 움직임",
		sub: "(건설 노동자, 배달원 등)"
	}
};

document.querySelectorAll('input[name="activity"]').forEach((radio) => {
	radio.addEventListener('change', () => {
		const selected = activityDescriptions[radio.id];
		document.getElementById('activity-main').textContent = selected.main;
		document.getElementById('activity-sub').textContent = selected.sub;
	});
});
