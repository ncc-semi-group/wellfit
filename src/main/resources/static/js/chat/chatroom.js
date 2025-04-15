// stomp
let socket = null;
let stompClient = null;
const reconnectInterval = 5000; // 5초 후 재연결 시도
// unreadCount
let userCount = null;
// date
let latestDateStr = null;

function exitChatroom() {
    if (confirm("채팅방에서 퇴장하시겠습니까?")) {
        // 퇴장 처리 로직
        location.href = "/chat"; // 예: 리스트로 리다이렉트
    }
}

async function getUserList(initial) {
    return new Promise((resolve, reject) => {
        $.ajax({
            type: "GET",
            url: "http://localhost:8080/chatroom/" + roomId + "/members",
            dataType: "json",
            success: function (response) {
                const list = document.querySelector(".participants");

                // ✅ 기존 유저 목록 비우기
                list.innerHTML = "";

                response.forEach(user => {
                    const li = document.createElement("li");
                    li.className = "participant";

                    const userMeta = document.createElement("div");
                    userMeta.className = "user";
                    userMeta.id = user.userId;
                    userMeta.setAttribute("latest_read_time", new Date(user.latestReadTime).toISOString());
                    userMeta.style.display = "none"; // invisible div
                    const img = document.createElement("img");
                    img.src = user.profileImage;

                    const span = document.createElement("span");
                    span.textContent = user.nickname;

                    li.appendChild(img);
                    li.appendChild(span);
                    li.appendChild(userMeta);
                    list.appendChild(li);
                });

                userCount = response.length;
                console.log("userCount : " + userCount);

                resolve();  // AJAX 완료 후 resolve 호출
            },
            error: function (error) {
                console.error("Error fetching user list:", error);
                reject(error);  // 실패 시 reject 호출
            }
        });
    });
}


function getChatroomDetail(){
    $.ajax({
        type: "GET",
        url: "http://localhost:8080/chatroom/"+roomId+"/detail",
        dataType: "json",
        success: function(response){
            const roomName = document.querySelector(".roomName");
            roomName.textContent = response.roomName;
            setRoomDetail(response.roomName, response.roomImage);
        }
    });
}
async function getChats(){
    $.ajax({
        type: "GET",
        url: `http://localhost:8080/chatroom/${roomId}/chats`,
        data:{"userId":userId},
        dataType: "json",
        success: function (response) {
            console.log("roomId:", roomId);
            console.log("userId:", userId);
            console.log("Chat List:", response);
            const list = document.querySelector(".chat-container");
            response.forEach(chat => {
                if(chat.messageType=="TALK"){
                    changeDate(chat.createdAt);
                    addTalkMessageWith(chat, 'db');
                }else if(chat.messageType=="CREATE"){
                    changeDate(chat.createdAt);
                    addCreateMessage(chat);
                }else if(chat.messageType=="DELETE") {
                    changeDate(chat.createdAt);
                    addDeleteMessage(chat);
                }else if(chat.messageType=="IMAGE") {
                    changeDate(chat.createdAt);
                    addImageMessage(chat);
                    appendImage(chat.message);
                }
            });
        },
    })
}
function getHourMinuteFromISO(isoString) {
    const date = new Date(isoString);
    let hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const period = hours < 12 ? '오전' : '오후';

    // 12시간제로 변경
    hours = hours % 12;
    if (hours === 0) hours = 12;

    return `${period} ${hours}:${minutes}`;
}

function extractDateOnly(isoString) {
    if (!isoString || isNaN(Date.parse(isoString))) {
        console.warn("⚠️ Invalid date passed to extractDateOnly:", isoString);
        return "Invalid-Date";
    }

    // 문자열을 ISO 포맷으로 파싱
    const utcDate = new Date(isoString);

    // 한국 시간 (UTC+9)으로 변환
    const koreaTime = new Date(utcDate.getTime() + 9 * 60 * 60 * 1000);

    const yyyy = koreaTime.getFullYear();
    const mm = String(koreaTime.getMonth() + 1).padStart(2, '0');
    const dd = String(koreaTime.getDate()).padStart(2, '0');

    return `${yyyy}-${mm}-${dd}`;
}
function updateLatestReadTime(userId, time) {
    return new Promise((resolve, reject) => {
        const userElement = document.getElementById(userId);
        if (userElement) {
            userElement.setAttribute("latest_read_time", time);
            console.log("User " + userId + " latest read time updated to " + time);
            resolve();  // 처리가 완료되었음을 알려줍니다.
        } else {
            console.error("User element not found for ID: " + userId);
            reject("User element not found for ID: " + userId);  // 실패 처리
        }
    });
}

function changeDate(date) {
    const currentDateStr = extractDateOnly(date);  // "2025-04-07"
    console.log("currentDateStr : "+currentDateStr);
    if (latestDateStr === null || latestDateStr !== currentDateStr) {
        latestDateStr = currentDateStr;

        const isoDate = new Date(date);  // 시간 있는 날짜 객체
        const yyyy = isoDate.getFullYear();
        const mm = String(isoDate.getMonth() + 1).padStart(2, '0');
        const dd = String(isoDate.getDate()).padStart(2, '0');

        const messageContainer = document.querySelector(".chat-container");
        const messageElement = document.createElement("div");
        messageElement.classList.add("message", "system");
        messageElement.innerHTML = `
            <div class="bubble">${yyyy}년 ${mm}월 ${dd}일</div>
        `;
        messageContainer.appendChild(messageElement);
    }
}
function sendReadMessage(message) {
    if (message.messageType === "TALK") {
        const jsonData = {
            //  {"roomId":1,"userId":1,"latest_read_time":"2025-03-31 14:00:00.000"}
            roomId: roomId,
            userId: userId,
            createdAt: new Date().toISOString()
        };
        stompClient.send("/pub/chat/read", {}, JSON.stringify(jsonData));
    }
}
async function onMessageReceived(payload) {
    const message = JSON.parse(payload.body);
    if (message.messageType === "TALK") {
        changeDate(message.createdAt);
        addTalkMessageWith(message, "socket");
        sendReadMessage(message);
    } else if (message.messageType === "CREATE") {
        console.log("message.userId : "+message.userId);
        appendUser(message);
        changeDate(message.createdAt);
        addCreateMessage(message);
    }else if(message.messageType === "READ"){
        console.log("userId : "+message.userId);
        const date = document.getElementById(message.userId).getAttribute("latest_read_time");
        console.log("latest_read_time : "+date);
        readMessages(date, message.createdAt);
        await updateLatestReadTime(message.userId, message.createdAt);

    } else if (message.messageType === "DELETE") {
        changeDate(message.createdAt);
        addDeleteMessage(message);
        deleteUser(message);
    } else if (message.messageType === "ENTER") {
        changeDate(message.createdAt);
        await getUserList();  // 사용자 목록 완전히 로드된 후
        addEnterMessage(message);
        sendReadMessage(message);
    } else if (message.messageType === "EXIT") {
        addExitMessage(message);
        sendReadMessage(message);
    } else if (message.messageType === "IMAGE") {
        changeDate(message.createdAt);
        addImageMessage(message);
        appendImage(message.message);
    }
}
function deleteUser(message) {
    const list = document.querySelector(".participants");
    const userId = message.userId;
    const userElement = document.getElementById(userId);
    if (userElement) {
        list.removeChild(userElement.parentElement);
        console.log("User " + userId + " removed from the list.");
    } else {
        console.warn("User element not found for ID: " + userId);
    }
}
function appendUser(message) {
    getUserDetail(message.userId, function(user) {
        const list = document.querySelector(".participants");

        const li = document.createElement("li");
        li.className = "participant";

        const userMeta = document.createElement("div");
        userMeta.className = "user";
        userMeta.id = user.id;
        userMeta.setAttribute("latest_read_time", new Date(message.createdAt).toISOString());
        userMeta.style.display = "none"; // invisible div

        const img = document.createElement("img");
        img.src = user.profileImage;
        const span = document.createElement("span");
        span.textContent = user.nickname;

        li.appendChild(img);
        li.appendChild(span);
        li.appendChild(userMeta);
        list.appendChild(li);
        userCount++;
    });
}

function getUserDetail(userId, callback){
    $.ajax({
        type: "GET",
        url: "http://localhost:8080/user/detail/"+userId,
        dataType: "json",
        success: function (response) {
            const user = {
                id: response.id,
                nickname: response.nickname,
                profileImage: response.profileImage
            };
            console.log("User Details:", user);
            callback(user);
        },
        error: function (error) {
            console.error("Error fetching user nickname:", error);
            return null;
        }
    })
}
function addTalkMessageWith(message, mode) {
    const messageContainer = document.querySelector(".chat-container");
    // LatestReadTime 이 message.createdAt 보다 작으면 읽음 처리
    let count = 0;
    if(mode == "db"){
        const users = document.getElementsByClassName("user");
        for (let i = 0; i < users.length; i++) {
            const user = users[i];
            const latestReadTime = new Date(user.getAttribute("latest_read_time"));
            const createdAt = new Date(message.createdAt);
            if (latestReadTime>= createdAt) {
                count++;
            }
        }
    }
    const messageElement = createTalkMessageElement(message, (userCount - count == 0 ? "" : userCount - count ));
    console.log("userCount : "+userCount);

    messageContainer.appendChild(messageElement);
    messageContainer.scrollTop = messageContainer.scrollHeight;
}
function readMessages(time1, time2){
    const chatList = document.querySelectorAll(".message .meta");
    const from = new Date(time1);
    const to = new Date(time2);
    let flag = false;

    for (let i = chatList.length - 1; i >= 0; i--) {
        const item = chatList[i];
        const isoText = item.querySelector(".isotime")?.textContent;

        // console.log("⏳ Checking isoText:", isoText, "→", new Date(isoText));
        if (!isoText) continue;
        console.log("from : "+from);
        console.log("to : "+to);
        const isoTime = new Date(isoText);
        console.log("isoTime : "+isoTime);
        if (!flag && isoTime > from) {
            flag = true;
        }

        if (flag) {
            if (isoTime >= to) break;

            const unreadEl = item.querySelector(".unread-count");
            if (!unreadEl) {
                console.warn("⚠️ No .unread-count found in message meta:", item);
                continue;
            }

            let count = parseInt(unreadEl.textContent);
            if (!isNaN(count) && count > 0) {
                console.log("count: "+count);
                unreadEl.textContent = count - 1 == 0 ? "" : count - 1;
                console.log("✅ unread reduced →", unreadEl.textContent);
            }
        }
    }
}


function createTalkMessageElement(message, unreadCount) {
    const messageElement = document.createElement("div");
    messageElement.classList.add("message");
    //console.log("unreadCount : "+unreadCount);
    if (message.userId == userId) {
        messageElement.classList.add("outgoing");
    } else {
        messageElement.classList.add("incoming");
    }
    document.create
    const time = getHourMinuteFromISO(message.createdAt);
    const userElement = document.getElementById(message.userId);
    let nickname = userElement
        ? userElement.parentElement.getElementsByTagName("span").item(0).textContent
        : "익명";
    if(nickname == null){
        getUserDetail(message.userId, function(user) {
            nickname = user.nickname;
        });
    }

// 최상단 div.content
    const contentDiv = document.createElement("div");
    contentDiv.classList.add("content");

// 닉네임
    const nicknameDiv = document.createElement("div");
    nicknameDiv.classList.add("nickname");
    nicknameDiv.textContent = nickname || "익명";

// detail
    const detailDiv = document.createElement("div");
    detailDiv.classList.add("detail");

// bubble
    const bubbleDiv = document.createElement("div");
    bubbleDiv.classList.add("bubble");
    bubbleDiv.textContent = message.message;

// meta
    const metaDiv = document.createElement("div");
    metaDiv.classList.add("meta");

// unread-count
    const unreadSpan = document.createElement("span");
    unreadSpan.classList.add("unread-count");
    unreadSpan.textContent = unreadCount === 0 ? "" : unreadCount;

// time
    const timeSpan = document.createElement("span");
    timeSpan.classList.add("time");
    timeSpan.textContent = time;

// isotime
    const isoTimeSpan = document.createElement("span");
    isoTimeSpan.classList.add("isotime");
    isoTimeSpan.textContent = message.createdAt;

// 구조 연결
    metaDiv.appendChild(unreadSpan);
    metaDiv.appendChild(timeSpan);
    metaDiv.appendChild(isoTimeSpan);

    detailDiv.appendChild(bubbleDiv);
    detailDiv.appendChild(metaDiv);

    contentDiv.appendChild(nicknameDiv);
    contentDiv.appendChild(detailDiv);

    messageElement.appendChild(contentDiv);

    return messageElement;
}

function addCreateMessage(message) {
    const messageContainer = document.querySelector(".chat-container");
    const userElement = document.getElementById(message.userId);

    // DOM에서 닉네임 얻기
    if (userElement) {
        const nickname = userElement.parentElement.querySelector("span").textContent;

        appendSystemMessage(nickname, message.message, messageContainer);
    } else {
        // DOM 없으면 AJAX로 닉네임 가져오기
        getUserDetail(message.userId, function (user) {
            appendSystemMessage(user.nickname, message.message, messageContainer);
        });
    }
}

function appendSystemMessage(nickname, msg, container) {
    const messageElement = document.createElement("div");
    messageElement.classList.add("message", "system");
    messageElement.innerHTML = `
        <div class="bubble">
            <span class="nickname">${nickname}</span>
            ${msg}
        </div>
    `;
    container.appendChild(messageElement);
    container.scrollTop = container.scrollHeight;
}
function addDeleteMessage(message){
    const messageContainer = document.querySelector(".chat-container");
    const messageElement = document.createElement("div");
    const userElement = document.getElementById(message.userId);
    const nickname = userElement
        ? userElement.parentElement.getElementsByTagName("span").item(0).textContent
        : "익명";
    messageElement.classList.add("message", "system");
    messageElement.innerHTML = `
            <div class="bubble">
            <span class="nickname">${nickname}</span>
            ${message.message}</div>
        `;
    messageContainer.appendChild(messageElement);
    // // 스크롤 맨 아래로
    // messageContainer.scrollTop = messageContainer.scrollHeight;
}
function addEnterMessage(message){
    const userId = message.userId;
    const user = document.getElementById(userId);
    const tempLRT = user.getAttribute("latest_read_time");
    const newReadTime = new Date();
    user.setAttribute("latest_read_time", newReadTime);
    readMessages(tempLRT, newReadTime);
}
function addExitMessage(message){
    const userId = message.userId;
    const user = document.getElementById(userId);
    const newReadTime = message.createdAt;
    user.setAttribute("latest_read_time", new Date(newReadTime).toISOString());
}
function addImageMessage(message){
    const messageContainer = document.querySelector(".chat-container");
    const messageElement = document.createElement("div");
    messageElement.classList.add("message");
    if(message.userId == userId){
        messageElement.classList.add("outgoing");
    }else{
        messageElement.classList.add("incoming");
    }
    messageElement.innerHTML = `
            <div class="bubble">
                <img src="${message.message}" class="image" alt="Image">
            </div>
        `;
    messageContainer.appendChild(messageElement);
    // 스크롤 맨 아래로
    messageContainer.scrollTop = messageContainer.scrollHeight;
}
function appendImage(imageUrl){
    const messageContainer = document.querySelector(".pictures");
    const messageElement = document.createElement("div");
    messageElement.classList.add("picture");
    messageElement.classList.add("image");
    messageElement.innerHTML = `<img src="${imageUrl}" class="image" alt="Image">`;
    messageContainer.appendChild(messageElement);
}
function stompConnect(){
    // 1. SockJS 및 STOMP 클라이언트 설정
    socket = new SockJS('http://localhost:8080/ws'); // 서버의 웹소켓 엔드포인트
    stompClient = Stomp.over(socket);
    // 2. STOMP 연결
    stompClient.connect({}, function (frame) {
        // 3. 구독 설정
        stompClient.subscribe("/sub/chatroom/" + roomId, onMessageReceived);

        // 4. JSON 데이터를 서버로 전송
        const jsonData = {
            //  {"roomId":1,"userId":1,"latest_read_time":"2025-03-31 14:00:00.000"}
            roomId: roomId,
            userId: userId,
            createdAt: new Date().toISOString()
        };
        stompClient.send("/pub/chat/read", {}, JSON.stringify(jsonData));
    });
    // ✅ WebSocket 연결 종료 시 /pub/chat/off 에 메시지 전송
    socket.onclose = function () {
        console.log("⚠️ WebSocket Disconnected. Sending disconnect signal...");
        sendDisconnectSignal();
        setTimeout(stompConnect, reconnectInterval); // 5초 후 재연결 시도
    };
}
$(document).ready(function () {
    getChatroomDetail();
    getUserList().then(() => {
        getChats().then(() => {
            stompConnect();
        });
    });
    let file = null;
    $(document).on("click", ".image", function(){
        const imageUrl = this.src;
        window.open(imageUrl, "_blank");
    });
    document.querySelector(".chat button").addEventListener("click", async function () {
        const message = document.getElementById("text").value;
        if (message.trim() === "") return;

        const chatMessage = {
            roomId: roomId,
            userId: userId,
            message: message,
            messageType: "TALK",
            createdAt: new Date().toISOString()
        };
        // 이미지 업로드
        if(file!=null){
            await uploadImage();
            file = null; // 전송 후 파일 초기화
        }
        try {
            if (stompClient && stompClient.connected) {
                stompClient.send("/pub/chat/talk", {}, JSON.stringify(chatMessage));
            } else {
                console.error("STOMP client is not connected.");
            }

            document.getElementById("text").value = ""; // 전송 후 입력창 비움
            document.getElementById("imagePreviewContainer").innerHTML = ""; // 미리보기 초기화
            document.getElementById("imageUpload").value = ""; // 파일 입력 초기화
        } catch (error) {
            console.error("Error updating latest read time:", error);
        }
    });
    async function uploadImage(){
        // 이미지 파일이 있는 경우
        if (file) {
            console.log("Image Exists");
            const formData = new FormData();
            formData.append("file", file);

            const res = await fetch("/chat/upload/image", {
                method: "POST",
                body: formData,
            });
            const data = await res.json();
            const imageUrl = data.url;

            stompClient.send("/pub/chat/image", {}, JSON.stringify({
                type: "IMAGE",
                roomId: roomId,
                userId: userId,
                imageUrl: imageUrl,
                createdAt: new Date().toISOString()
            }));
        }
    }
    document.getElementById("text").addEventListener("keydown", function (e) {
        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault();
            document.querySelector(".chat button").click();
        }
    });
    document.getElementById("imageUpload").addEventListener("change", function(event) {
        file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                const img = document.createElement("img");
                img.src = e.target.result;
                img.className = "image-preview";

                const previewContainer = document.getElementById("imagePreviewContainer");
                previewContainer.innerHTML = ""; // 기존 미리보기 초기화
                previewContainer.appendChild(img);
            };
            reader.readAsDataURL(file);
        }
    });
    // 드래그로 가로 스크롤 기능 추가
    const pictureContainer = document.querySelector(".pictures");
    let isDragging = false;
    let startX, scrollLeft;

    pictureContainer.addEventListener("mousedown", (e) => {
        isDragging = true;
        startX = e.pageX - pictureContainer.offsetLeft;
        scrollLeft = pictureContainer.scrollLeft;
    });

    pictureContainer.addEventListener("mouseleave", () => {
        isDragging = false;
    });

    pictureContainer.addEventListener("mouseup", () => {
        isDragging = false;
    });

    pictureContainer.addEventListener("mousemove", (e) => {
        if (!isDragging) return;
        e.preventDefault();
        const x = e.pageX - pictureContainer.offsetLeft;
        const walk = (x - startX) * 2; // 스크롤 속도 조절
        pictureContainer.scrollLeft = scrollLeft - walk;
    });
});

//  창을 닫거나 이동할 때 서버에 알림
function sendDisconnectSignal() {
    if (stompClient && stompClient.connected) {
        const disconnectData = {
            roomId: roomId,
            userId: userId,
            createdAt: new Date().toISOString()
        };
        stompClient.send("/pub/chat/off", {}, JSON.stringify(disconnectData));
        stompClient.disconnect();
    }
}
window.addEventListener("beforeunload", sendDisconnectSignal);
// 메시지 수신 처리

function exitChatroom(){
    if (confirm("채팅방에서 퇴장하시겠습니까?")) {
        const exitData = {
            roomId: roomId,
            userId: userId,
            createdAt: new Date().toISOString()
        };
        $.ajax({
            url: '/chatroom/exit',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(exitData),
            success: function (res) {
                console.log("✅ 퇴장 처리 성공:", res);
                // 퇴장 후 리스트 페이지로 이동
                window.location.href = '/chat';
            },
        });
    }
}
function setRoomDetail(roomName, roomImage){
    const roomImageContainer = document.querySelector(".room-detail");
    const roomNameContainer = document.querySelector(".room-name");
    if(roomImage != null){
        const img = document.createElement("img");
        img.src = roomImage;
        img.alt = "Room Image";
        img.className = "room-image";
        roomImageContainer.appendChild(img);
    }
    roomNameContainer.textContent = roomName;
}
function goToList() {
    window.location.href = "/chat"; // 리스트 페이지 URL 설정
}

function openModal() {
    document.getElementById("modal").classList.add("active");
}

function closeModal() {
    document.getElementById("modal").classList.remove("active");
}