document.addEventListener("DOMContentLoaded", function () {
	const labels = document.querySelectorAll(".value-select");
	const descEl = document.getElementById("mealDesc");

	const presetRatios = {
		common: { carbohydrate: 57, protein: 18, fat: 25 },
		health: { carbohydrate: 50, protein: 30, fat: 20 },
		quito: { carbohydrate: 60, protein: 15, fat: 25 },
		vegan: { carbohydrate: 8, protein: 22, fat: 70 }
	};

	const inputs = {
		carbohydrate: document.getElementById('carbohydrate'),
		protein: document.getElementById('protein'),
		fat: document.getElementById('fat')
	};

	labels.forEach(label => {
		label.addEventListener("click", () => {
			const desc = label.getAttribute("data-desc");
			descEl.textContent = desc;
		});
	});

	document.querySelectorAll('.value-select-radio').forEach(radio => {
		radio.addEventListener('change', () => {
			const selected = document.querySelector('.value-select-radio:checked').id;
			const { carbohydrate, protein, fat } = presetRatios[selected];

			inputs.carbohydrate.value = carbohydrate;
			inputs.protein.value = protein;
			inputs.fat.value = fat;

			updateMacros(); // 비율 바뀌면 계산도 바로
		});
	});

	// 최초 로드 시 계산
	updateMacros();
});

function updateMacros() {
	const totalCalories = Number(document.getElementById('calorieHolder').dataset.calories);

	const carbRatio = parseFloat(document.getElementById('carbohydrate').value) || 0;
	const proteinRatio = parseFloat(document.getElementById('protein').value) || 0;
	const fatRatio = parseFloat(document.getElementById('fat').value) || 0;

	const carbCal = totalCalories * (carbRatio / 100);
	const proteinCal = totalCalories * (proteinRatio / 100);
	const fatCal = totalCalories - carbCal - proteinCal;

	const carbGram = Math.round((carbCal / 4) * 10) / 10;
	const proteinGram = Math.round((proteinCal / 4) * 10) / 10;
	const fatGram = Math.round((fatCal / 9) * 10) / 10;

	document.getElementById('carbCal').innerText = `${Math.round(carbCal)} Kcal`;
	document.getElementById('proteinCal').innerText = `${Math.round(proteinCal)} Kcal`;
	document.getElementById('fatCal').innerText = `${Math.round(fatCal)} Kcal`;

	document.getElementById('carbGram').innerText = `${carbGram} g`;
	document.getElementById('proteinGram').innerText = `${proteinGram} g`;
	document.getElementById('fatGram').innerText = `${fatGram} g`;
}
