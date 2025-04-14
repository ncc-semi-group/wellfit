let followingList = [];

// 팔로잉 목록 조회
document.addEventListener("DOMContentLoaded", function() {
    const followingElements = document.querySelectorAll('.following-wrapper');
    
    followingElements.forEach((element) => {
        const nickname = element.querySelector('.name').textContent;
        const profileImage = element.querySelector('img').src;
        const myIntro = element.querySelector('.intro').textContent;

        followingList.push({
            nickname: nickname,
            profileImage: profileImage,
            myIntro: myIntro
        });
    });

    updateFollowingList(followingList);
});

// 닉네임으로 팔로잉 검색
function searchFollowingByNickname() {
    const searchQuery = document.querySelector("input").value.toLowerCase();

    const filtered = followingList.filter(user => 
        user.nickname.toLowerCase().includes(searchQuery)
    );

    updateFollowingList(filtered);
}

// 팔로잉 목록 갱신
function updateFollowingList(users) {
    const listContainer = document.querySelector('.list-container');
    listContainer.innerHTML = '';

    users.forEach(user => {
        let imageUrl = user.profileImage;

        // URL 형태가 아니면 /images/ 붙이기
        if (!imageUrl.startsWith('http') && !imageUrl.startsWith('/images/')) {
            imageUrl = `/images/${imageUrl}`;
        }

        const element = document.createElement('div');
        element.classList.add('following-wrapper');
        element.innerHTML = `
            <img src="${imageUrl}" alt="${user.nickname}" />
            <div class="profile">
                <div class="name">${user.nickname}</div>
                <div class="intro">${user.myIntro}</div>
            </div>
        `;
        listContainer.appendChild(element);
    });
}
