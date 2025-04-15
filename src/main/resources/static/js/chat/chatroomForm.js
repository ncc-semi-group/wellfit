function goToList() {
    location.href = '/chat';
}

$(document).ready(function () {
    const preview = $('.preview');
    const imgInput = $('#imgInput');

    // 파일 선택 시 이미지 미리보기
    imgInput.on('change', function (event) {
        const file = event.target.files[0];
        if (file && file.type.startsWith("image/")) {
            const reader = new FileReader();
            reader.onload = function (e) {
                preview.attr('src', e.target.result);
            };
            reader.readAsDataURL(file);
        } else {
            preview.attr('src', 'https://kr.object.ncloudstorage.com/wellfit/image/noimage.jpeg');
        }
    });

    // 이미지 클릭 시 파일 선택창 열기
    preview.on('click', function () {
        imgInput.click();
    });
});