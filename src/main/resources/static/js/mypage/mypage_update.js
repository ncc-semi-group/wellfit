function previewImage(input) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();
        reader.onload = function(e) {
            document.getElementById('preview-image').src = e.target.result;
        };
        reader.readAsDataURL(input.files[0]);
    }
}

$(document).ready(function() {
    // 뒤로가기 버튼 클릭 이벤트
    $('.back-btn').click(function() {
        history.back();
    });
});