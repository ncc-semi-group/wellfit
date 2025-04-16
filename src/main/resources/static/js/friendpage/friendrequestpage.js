window.addEventListener('load', function () {
    history.replaceState(null, '', '/friends');
});

document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll('.recommand-friend-wrapper').forEach(friendElement => {
        const userId = friendElement.getAttribute('data-user-id');
        friendElement.addEventListener('click', () => {
            if (userId) {
                window.location.href = `/userpage/${userId}`;
            }
        });
    });

    document.querySelectorAll('.friend-accept-wrapper').forEach(friendElement => {
        friendElement.addEventListener('click', (e) => {
            if (e.target.tagName === 'BUTTON') return;
            const userId = friendElement.getAttribute('data-user-id');
            if (userId) {
                window.location.href = `/userpage/${userId}`;
            }
        });
    });

    const backBtn = document.querySelector('.back-btn');
    if (backBtn) {
        backBtn.addEventListener('click', function () {
            window.history.back();
        });
    }
});

function fetchUserIdByNickname(nickname) {
    fetch(`/user-id-by-nickname?nickname=${encodeURIComponent(nickname)}`)
        .then(res => res.json())
        .then(data => {
            if (data && data.id) {
                window.location.href = `/userpage/${data.id}`;
            } else {
                window.showToast("유저 정보를 가져올 수 없습니다.");
            }
        })
        .catch(err => {
            console.error("유저 정보 가져오기 오류:", err);
        });
}

document.getElementById("add-friend-btn").addEventListener("click", function () {
    togglePageVisibility('add');
});

document.getElementById("accept-friend-btn").addEventListener("click", function () {
    togglePageVisibility('accept');
});

function togglePageVisibility(page) {
    if (page === 'add') {
        document.getElementById("friends-add").classList.remove("hidden");
        document.getElementById("friends-accept").classList.add("hidden");
        document.getElementById("add-friend-btn").classList.add("on");
        document.getElementById("add-friend-btn").classList.remove("off");
        document.getElementById("accept-friend-btn").classList.add("off");
        document.getElementById("accept-friend-btn").classList.remove("on");
    } else {
        document.getElementById("friends-accept").classList.remove("hidden");
        document.getElementById("friends-add").classList.add("hidden");
        document.getElementById("accept-friend-btn").classList.add("on");
        document.getElementById("accept-friend-btn").classList.remove("off");
        document.getElementById("add-friend-btn").classList.add("off");
        document.getElementById("add-friend-btn").classList.remove("on");
    }
}

function handleKeyDown(event) {
    if (event.key === 'Enter') {
        searchFriend();
    }
}

function searchFriend() {
    const nickname = document.getElementById("searchInput").value.trim();
    const listContainer = document.getElementById("friendList");

    if (nickname === '') {
        listContainer.innerHTML = '';
        return;
    }

    fetch(`/search?nickname=${encodeURIComponent(nickname)}`)
        .then(response => {
            if (!response.ok) throw new Error("검색 실패");
            return response.json();
        })
        .then(data => {
            updateFriendList(data, listContainer);
        })
        .catch(error => {
            console.error("검색 중 오류 발생:", error);
            listContainer.innerHTML = `<div style="padding: 1rem;">검색 중 오류가 발생했습니다.</div>`;
        });
}

function updateFriendList(data, listContainer) {
    if (data.length === 0) {
        listContainer.innerHTML = `<div style="padding: 1rem;">검색 결과가 없습니다.</div>`;
        return;
    }

    listContainer.innerHTML = '';
    data.forEach(user => {
        const imageUrl = user.profileImage && (user.profileImage.startsWith('http') || user.profileImage.startsWith('/images/'))
            ? user.profileImage
            : `/images/${user.profileImage || 'default-profile.jpg'}`;

        const friendElement = document.createElement('div');
        friendElement.classList.add('friend-wrapper');
        friendElement.setAttribute('data-user-id', user.id);

        friendElement.innerHTML = `
            <img src="${imageUrl}" alt="${user.nickname}"
                 onerror="this.onerror=null; this.src='/static/images/default-profile.jpg';" />
            <div class="profile">
                <div class="name">${user.nickname}</div>
                <div class="intro">${user.myIntro}</div>
            </div>
        `;

        friendElement.addEventListener('click', function () {
            const userId = this.getAttribute('data-user-id');
            if (userId) {
                window.location.href = `/userpage/${userId}`;
            }
        });

        listContainer.appendChild(friendElement);
    });
}

function acceptFriendRequest(id, userId, senderId) {
    handleFriendRequest('/accept', { id, userId, senderId });
}

function rejectFriendRequest(id) {
    handleFriendRequest('/reject', { id });
}

function handleFriendRequest(url, bodyData) {
    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(bodyData)
    })
        .then(response => response.text())
        .then(data => {
            window.showToast(data);
            fetchFriendRequests();
        })
        .catch(error => console.error('에러:', error));
}

function fetchFriendRequests() {
    fetch('/friend-requests')
        .then(res => res.json())
        .then(data => {
            const container = document.getElementById('list-container2');
            container.innerHTML = '';

            if (!container) {
                console.error('#friends-accept 요소가 존재하지 않습니다.');
                return;
            }

            if (data.length === 0) {
                container.innerHTML = `<div style="padding: 1rem;">받은 친구 요청이 없습니다.</div>`;
                return;
            }

            data.forEach(request => {
                const imageUrl = request.senderProfileImage && (request.senderProfileImage.startsWith('http') || request.senderProfileImage.startsWith('/images/'))
                    ? request.senderProfileImage
                    : `/images/${request.senderProfileImage || 'default-profile.jpg'}`;

                const wrapper = document.createElement('div');
                wrapper.classList.add('friend-accept-wrapper');
                wrapper.setAttribute('data-user-id', request.senderId);

                wrapper.innerHTML = `
                    <img src="${imageUrl}" alt="${request.senderNickname}"
                         onerror="this.onerror=null; this.src='/static/images/default-profile.jpg';" />
                    <div class="profile">
                        <div class="name">${request.senderNickname}</div>
                        <div class="intro">${request.senderIntro}</div>
                    </div>
                    <div class="buttons">
                        <button onclick="acceptFriendRequest(${request.id}, ${request.receiverId}, ${request.senderId})">수락</button>
                        <button onclick="rejectFriendRequest(${request.id})">거절</button>
                    </div>
                `;

                wrapper.addEventListener('click', (e) => {
                    if (e.target.tagName === 'BUTTON') return;
                    window.location.href = `/userpage/${request.senderId}`;
                });

                container.appendChild(wrapper);
            });
        })
        .catch(err => {
            console.error('친구 요청 리스트 가져오기 오류:', err);
        });
}
