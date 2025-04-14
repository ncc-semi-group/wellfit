function goToList() {
    location.href = '/chat';
}
$(document).ready(function () {
    // input[type="file"] 변경 감지
    $('input[type="file"][name="chatroomImage"]').on('change', function (event) {
        const file = event.target.files[0];
        if (file && file.type.startsWith("image/")) {
            const reader = new FileReader();
            reader.onload = function (e) {
                $('.preview').attr('src', e.target.result);
            };
            reader.readAsDataURL(file);
        } else {
            $('.preview').attr('src', ''); // 이미지 파일이 아닌 경우 초기화
        }
    });
    const preview = document.querySelector('.preview');
    const imgInput = document.getElementById('imgInput');

    // 클릭 시 파일 선택창 열기
    preview.addEventListener('click', () => {
        imgInput.click();
    });

    // 파일 선택 시 이미지 미리보기
    imgInput.addEventListener('change', (event) => {
        const file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = e => {
                preview.src = e.target.result;
            };
            reader.readAsDataURL(file);
        }
    });

});