let followingList = [];

// 팔로잉 목록 조회
document.addEventListener("DOMContentLoaded", function() {
    const followingElements = document.querySelectorAll('.following-wrapper');
    
    followingElements.forEach((element) => {
        const userId = element.getAttribute('data-user-id');
        const nickname = element.querySelector('.name').textContent;
        const profileImage = element.querySelector('img').src;
        const myIntro = element.querySelector('.intro').textContent;

        followingList.push({
            id: userId,
            nickname: nickname,
            profileImage: profileImage,
            myIntro: myIntro
        });
    });

    updateFollowingList(followingList);

    // 팔로잉 항목 클릭 이벤트
    document.querySelectorAll('.following-wrapper').forEach(item => {
        item.addEventListener('click', function() {
            const userId = this.getAttribute('data-user-id');
            if (userId) {
                window.location.href = `/userpage/${userId}`;
            }
        });
    });

    // 뒤로가기 버튼 클릭 이벤트
    const backBtn = document.querySelector('.back-btn');
    if (backBtn) {
        backBtn.addEventListener('click', function() {
            window.history.back();
        });
    }
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
        element.setAttribute('data-user-id', user.id);
        element.innerHTML = `
            <img src="${imageUrl}" alt="${user.nickname}" />
            <div class="profile">
                <div class="name">${user.nickname}</div>
                <div class="intro">${user.myIntro}</div>
            </div>
        `;
        
        // 클릭 이벤트 추가
        element.addEventListener('click', function() {
            const userId = this.getAttribute('data-user-id');
            if (userId) {
                window.location.href = `/userpage/${userId}`;
            }
        });
        
        listContainer.appendChild(element);
    });
}

$('.back-btn').click(function() {
    history.back();
});
