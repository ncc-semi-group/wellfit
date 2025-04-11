document.addEventListener("DOMContentLoaded", function () {
	const labels = document.querySelectorAll(".value-select");
	const descEl = document.getElementById("mealDesc");

	labels.forEach(label => {
		label.addEventListener("click", () => {
			const desc = label.getAttribute("data-desc");
			descEl.textContent = desc;
		});
	});
});

const presetRatios = {
	common: { carbs: 57, protein: 18, fat: 25 },
	health: { carbs: 50, protein: 30, fat: 20 },
	quito: { carbs: 60, protein: 15, fat: 25 },
	vegan: { carbs: 8, protein: 22, fat: 70 }
};

const inputs = {
	carbs: document.getElementById('carbs'),
	protein: document.getElementById('protein'),
	fat: document.getElementById('fat')
};

document.querySelectorAll('.value-select-radio').forEach(radio => {
	radio.addEventListener('change', () => {
		const selected = document.querySelector('.value-select-radio:checked').id;
		const { carbs, protein, fat } = presetRatios[selected];

		inputs.carbs.value = carbs;
		inputs.protein.value = protein;
		inputs.fat.value = fat;
	});
});
