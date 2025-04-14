function goToList() {
    const userId = document.querySelector('input[name="userId"]').value;
    console.log("userId : "+userId);
    if (!userId) {
        alert("유저 아이디를 입력해주세요!");
        return;
    }
    location.href = './list/' + encodeURIComponent(userId);
}