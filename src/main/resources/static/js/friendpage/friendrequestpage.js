
// '/friends_request' 대신 '/friends'로 히스토리 항목을 덮어씀
window.addEventListener('load', function() {
	history.replaceState(null, '', '/friends');
});

/* 친구 요청 페이지 */
document.getElementById("add-friend-btn").addEventListener("click", function() {
	document.getElementById("friends-add").classList.remove("hidden");
	document.getElementById("friends-accept").classList.add("hidden");

	this.classList.add("on");
	this.classList.remove("off");
	document.getElementById("accept-friend-btn").classList.add("off");
	document.getElementById("accept-friend-btn").classList.remove("on");
});

/* 친구 수락 페이지 */
document.getElementById("accept-friend-btn").addEventListener("click", function() {
	document.getElementById("friends-accept").classList.remove("hidden");
	document.getElementById("friends-add").classList.add("hidden");

	this.classList.add("on");
	this.classList.remove("off");
	document.getElementById("add-friend-btn").classList.add("off");
	document.getElementById("add-friend-btn").classList.remove("on");
});
        
/* 친구 검색 */
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
			if (data.length === 0) {
				listContainer.innerHTML = `<div style="padding: 1rem;">검색 결과가 없습니다.</div>`;
				return;
			}

			listContainer.innerHTML = data.map(user => {
				const imageUrl =
					user.profileImage.startsWith('http') || user.profileImage.startsWith('/images/')
						? user.profileImage
						: `/images/${user.profileImage}`;

				return `
					<div class="friend-wrapper">
						<img src="${imageUrl}" />
						<div class="profile">
							<div class="name">${user.nickname}</div>
							<div class="intro">${user.myIntro}</div>
						</div>
					</div>
				`;
			}).join('');
		})
		.catch(error => {
			console.error("검색 중 오류 발생:", error);
			listContainer.innerHTML = `<div style="padding: 1rem;">검색 중 오류가 발생했습니다.</div>`;
		});
}

function acceptFriendRequest(requestId, userId, senderId) {
	fetch('/accept', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({
			id: requestId,
			userId: userId,
			senderId: senderId
		})
	})
	.then(response => response.text())
	.then(data => {
		alert(data);
		document.getElementById("accept-friend-btn").click(); 
	})
	.catch(error => console.error('에러:', error));
}

function rejectFriendRequest(requestId) {
	fetch('/reject', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({
			id: requestId
		})
	})
	.then(response => response.text())
	.then(data => {
		alert(data);
		document.getElementById("accept-friend-btn").click();
	})
	.catch(error => console.error('에러:', error));
}
