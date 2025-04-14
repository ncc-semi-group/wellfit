function previewImage(input) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();
        reader.onload = function(e) {
            document.getElementById('preview-image').src = e.target.result;
        };
        reader.readAsDataURL(input.files[0]);
    }
}

// 폼 제출 전에 이미지 파일 검증
document.querySelector('form').addEventListener('submit', function(e) {
    const fileInput = document.getElementById('profile-upload');
    if (fileInput.files.length > 0) {
        const file = fileInput.files[0];
        const validTypes = ['image/jpeg', 'image/png', 'image/gif'];
        
        if (!validTypes.includes(file.type)) {
            e.preventDefault();
            alert('이미지 파일만 업로드 가능합니다. (JPEG, PNG, GIF)');
            return;
        }
        
        if (file.size > 5 * 1024 * 1024) { // 5MB 제한
            e.preventDefault();
            alert('파일 크기는 5MB 이하여야 합니다.');
            return;
        }
    }
});

$(document).ready(function() {
    // 뒤로가기 버튼 클릭 이벤트
    $('.back-btn').click(function() {
        history.back();
    });
});