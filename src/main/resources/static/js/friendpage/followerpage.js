let followerList = [];

// 팔로워 목록 조회
document.addEventListener("DOMContentLoaded", function() {
    const followerElements = document.querySelectorAll('.follower-wrapper');
    
    followerElements.forEach((element) => {
        const userId = element.getAttribute('data-user-id');
        const nickname = element.querySelector('.name').textContent;
        const profileImage = element.querySelector('img').src;
        const myIntro = element.querySelector('.intro').textContent;

        followerList.push({
            id: userId,
            nickname: nickname,
            profileImage: profileImage,
            myIntro: myIntro
        });
    });

    updateFollowerList(followerList);

    // 팔로워 항목 클릭 이벤트
    document.querySelectorAll('.follower-wrapper').forEach(item => {
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

// 닉네임으로 팔로워 검색
function searchFollowerByNickname() {
    const searchQuery = document.querySelector("input").value.toLowerCase();

    const filtered = followerList.filter(user => 
        user.nickname.toLowerCase().includes(searchQuery)
    );

    updateFollowerList(filtered);
}

// 팔로워 목록 갱신
function updateFollowerList(users) {
    const listContainer = document.querySelector('.list-container');
    listContainer.innerHTML = '';

    users.forEach(user => {
        let imageUrl = user.profileImage;

        // URL 형태가 아니면 /images/ 붙이기
        if (!imageUrl.startsWith('http') && !imageUrl.startsWith('/images/')) {
            imageUrl = `/images/${imageUrl}`;
        }

        const element = document.createElement('div');
        element.classList.add('follower-wrapper');
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
