let friendsList = [];

// 친구 목록 조회
document.addEventListener("DOMContentLoaded", function() {
    const friendElements = document.querySelectorAll('.friend-wrapper');
    
    friendElements.forEach((friendElement) => {
        const nickname = friendElement.querySelector('.name').textContent;
        const profileImage = friendElement.querySelector('img').src;
        const myIntro = friendElement.querySelector('.intro').textContent;

        friendsList.push({
            nickname: nickname,
            profileImage: profileImage,
            myIntro: myIntro
        });
    });

    updateFriendList(friendsList);
});

// 닉네임으로 친구 검색
function searchFriendByNickname() {
    const searchQuery = document.querySelector("input").value.toLowerCase();

    const filteredFriends = friendsList.filter(friend => 
        friend.nickname.toLowerCase().includes(searchQuery)
    );

    updateFriendList(filteredFriends);
}

// 친구 목록 갱신
function updateFriendList(friends) {
    const listContainer = document.querySelector('.list-container');
    listContainer.innerHTML = '';

    friends.forEach(friend => {
        const friendElement = document.createElement('div');
        friendElement.classList.add('friend-wrapper');
        friendElement.innerHTML = `
		<img src="${friend.profileImage}" alt="${friend.nickname}" />
            <div class="profile">
                <div class="name">${friend.nickname}</div>
                <div class="intro">${friend.myIntro}</div>
            </div>
        `;
        listContainer.appendChild(friendElement);
    });
}
	