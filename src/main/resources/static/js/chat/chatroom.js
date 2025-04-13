// stomp
let socket = null;
let stompClient = null;
const reconnectInterval = 5000; // 5초 후 재연결 시도
// unreadCount
let readingCount = 0;
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
                userMeta.setAttribute("latestReadTime", new Date(user.latestReadTime).toISOString());
                userMeta.style.display = "none"; // invisible div
                userMeta.setAttribute("active", user.active);
                console.log("active : " + user.active);
                const img = document.createElement("img");
                img.src = user.profileImage;

                const span = document.createElement("span");
                span.textContent = user.nickname;

                li.appendChild(img);
                li.appendChild(span);
                li.appendChild(userMeta);
                list.appendChild(li);
            });

            const participants = document.getElementsByClassName("participant");
            for (let i = 0; i < participants.length; i++) {
                if (participants[i].getAttribute("active") === "true") {
                    readingCount++;
                }
            }
            userCount = response.length;
            console.log("userCount : " + userCount);
        },
        error: function (error) {
            console.error("Error fetching user list:", error);
        }
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
            console.log("readingCount : " + readingCount);
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
                    addTalkMessageWithDB(chat);
                    activeUsersRead();
                }else if(chat.messageType=="CREATE"){
                    changeDate(chat.createdAt);
                    addCreateMessage(chat);
                }else if(chat.messageType=="DELETE") {
                    changeDate(chat.createdAt);
                    addDeleteMessage(chat);
                }else if(chat.messageType=="IMAGE") {
                    changeDate(chat.createdAt);
                    addImageMessage(chat);
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
    return isoString.split('T')[0];  // "2025-04-07"
}

function changeDate(date) {
    const currentDateStr = extractDateOnly(date);  // "2025-04-07"

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
function onMessageReceived(payload) {
    const message = JSON.parse(payload.body);
    if(message.messageType=="TALK"){
        changeDate(message.createdAt);
        addTalkMessageWithSocket(message);
    }else if(message.messageType=="CREATE"){
        changeDate(message.createdAt);
        getUserList().then(() => addCreateMessage(message))
        ;
    }else if(message.messageType=="DELETE"){
        changeDate(message.createdAt);
        addDeleteMessage(message);
    }else if(message.messageType=="ENTER"){
        addEnterMessage(message);
    }else if(message.messageType=="EXIT"){
        addExitMessage(message);
    }else if(message.messageType=="IMAGE") {
        changeDate(message.createdAt);
        addImageMessage(message);
    }
}
function addTalkMessageWithDB(message) {
    const messageContainer = document.querySelector(".chat-container");
    let unreadCount = userCount - message.readCount;
    console.log("message.readCount : " + message.readCount);
    console.log("unreadCount : " +unreadCount);
    console.log("readingCount : " + readingCount);
    //const messageElement = createTalkMessageElement(message, (userCount - message.readCount==0?"":userCount - message.readCount));
    const messageElement = createTalkMessageElement(message, (unreadCount==0?"":unreadCount));
    messageContainer.appendChild(messageElement);
    messageContainer.scrollTop = messageContainer.scrollHeight;
}

function addTalkMessageWithSocket(message) {
    const messageContainer = document.querySelector(".chat-container");
    const messageElement = createTalkMessageElement(message, (userCount - message.readCount - readingCount==0?"":userCount - message.readCount - readingCount));
    messageContainer.appendChild(messageElement);
    messageContainer.scrollTop = messageContainer.scrollHeight;
}
function activeUsersRead(){
    const users = document.getElementsByClassName("user");
    let activeUsers = [];
    for(let i = 0; i < users.length; i++){
        const user = users[i];
        if(user.getAttribute("active") == "true"){
            activeUsers.push(user);
        }
    }
    for(let i = 0; i < activeUsers.length; i++){
        const user = users[i];
        console.log("activeUsers : " + user.getAttribute("active") + " / latestReadTime : " + user.getAttribute("latestReadTime") + " / id : " + user.getAttribute("id"));
        if(user.getAttribute("active") == "true"){
            const tempLRT = user.getAttribute("latestReadTime");
            const newReadTime = new Date().toISOString();
            console.log("readMessages:", tempLRT, "→", newReadTime);
            readMessages(tempLRT, newReadTime);
        }
    }
}
function readMessages(time1, time2){
    const chatList = document.querySelectorAll(".message .meta");
    const from = new Date(time1);
    const to = new Date(time2);
    // console.log("🕓 readMessages:", time1, "→", time2);
    // console.log("✅ time1 Date:", new Date(time1));
    // console.log("✅ time2 Date:", new Date(time2));
    let flag = false;

    for (let i = chatList.length - 1; i >= 0; i--) {
        const item = chatList[i];
        const isoText = item.querySelector(".isotime")?.textContent;

        // console.log("⏳ Checking isoText:", isoText, "→", new Date(isoText));
        if (!isoText) continue;

        const isoTime = new Date(isoText);

        if (!flag && isoTime >= from) {
            flag = true;
        }

        if (flag) {
            if (isoTime > to) break;

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

    const time = getHourMinuteFromISO(message.createdAt);
    const userElement = document.getElementById(message.userId);
    const nickname = userElement
        ? userElement.parentElement.getElementsByTagName("span").item(0).textContent
        : "익명";

    messageElement.innerHTML = `
        <div class="content">
            <div class="nickname">${nickname || "익명"}</div>
            <div class="detail">
                <div class="bubble">${message.message}</div>
                <div class="meta">
                    <span class="unread-count">${unreadCount==0?"":unreadCount}</span>
                    <span class="time">${time}</span>
                    <span class="isotime">${message.createdAt}</span>
                </div>
            </div>
        </div>
    `;
    return messageElement;
}

function addCreateMessage(message){
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
    // 스크롤 맨 아래로
    messageContainer.scrollTop = messageContainer.scrollHeight;
}
function addDeleteMessage(message){
    const messageContainer = document.querySelector(".chat-container");
    const messageElement = document.createElement("div");
    messageElement.classList.add("message", "system");
    messageElement.innerHTML = `
            <div class="bubble">
            <span class="nickname">${message.userId}</span>
            ${message.message}</div>
        `;
    messageContainer.appendChild(messageElement);
    // // 스크롤 맨 아래로
    // messageContainer.scrollTop = messageContainer.scrollHeight;
}
function addEnterMessage(message){
    readingCount++;
    const users = document.getElementsByClassName("user");
    for(let i = 0; i < users.length; i++){
        const user = users[i];
        if(user.getAttribute("id") == message.userId){
            user.setAttribute("active", "true");
            console.log("active : " + user.getAttribute("active"));
            break;
        }
    }
    console.log("readingCount : "+readingCount);
    const userId = message.userId;
    const user = document.getElementById(userId);
    const tempLRT = user.getAttribute("latestReadTime");
    const newReadTime = new Date().toISOString();
    console.log("readMessages:", tempLRT, "→", newReadTime);
    user.setAttribute("latestReadTime", newReadTime);
    readMessages(tempLRT, newReadTime);
}
function addExitMessage(message){
    readingCount--;
    console.log("Exit Message -- readingCount : "+readingCount);
    const userId = message.userId;
    const user = document.getElementById(userId);
    const newReadTime = message.latestReadTime;
    user.setAttribute("latestReadTime", new Date(newReadTime).toISOString());
    user.setAttribute("active", "false");
}
function addImageMessage(message){
    const messageContainer = document.querySelector(".chat-container");
    const messageElement = document.createElement("div");
    messageElement.classList.add("message", "system");
    messageElement.innerHTML = `
            <div class="bubble">
            <span class="nickname">${message.userId}</span>
            ${message.message}</div>
        `;
    messageContainer.appendChild(messageElement);
    // 스크롤 맨 아래로
    messageContainer.scrollTop = messageContainer.scrollHeight;
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
            //  {"roomId":1,"userId":1,"latestReadTime":"2025-03-31 14:00:00.000"}
            roomId: roomId,
            userId: userId,
            latestReadTime: new Date().toISOString()
        };
        stompClient.send("/pub/chat/on", {}, JSON.stringify(jsonData));
    });
    // ✅ WebSocket 연결 종료 시 /pub/chat/off 에 메시지 전송
    socket.onclose = function () {
        console.log("⚠️ WebSocket Disconnected. Sending disconnect signal...");
        sendDisconnectSignal();
        // 🔻 readingCount 감소 처리
        readingCount--;
        console.log("🔌 disconnected, readingCount:", readingCount);
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

    // EventListener
    document.querySelector(".chat button").addEventListener("click", function () {
        const message = document.getElementById("text").value;
        if (message.trim() === "") return;

        const chatMessage = {
            roomId: roomId,
            userId: userId,
            message: message,
            messageType: "TALK",
            createdAt: new Date().toISOString()
        };

        if (stompClient && stompClient.connected) {
            stompClient.send("/pub/chat/talk", {}, JSON.stringify(chatMessage));
        }else{
            console.error("STOMP client is not connected.");
        }
        document.getElementById("text").value = ""; // 전송 후 입력창 비움
        document.getElementById("imagePreviewContainer").innerHTML = ""; // 미리보기 초기화
        document.getElementById("imageUpload").value = ""; // 파일 입력 초기화
    });
    document.getElementById("text").addEventListener("keydown", function (e) {
        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault();
            document.querySelector(".chat button").click();
        }
    });
    document.getElementById("imageUpload").addEventListener("change", function(event) {
        const file = event.target.files[0];
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
            latestReadTime: new Date().toISOString()
        };
        stompClient.send("/pub/chat/off", {}, JSON.stringify(disconnectData));
        stompClient.disconnect();
        userActiveChange(userId, "false");
    }
}
function userActiveChange(userId, activeStatus) {
    const userElement = document.getElementById(userId);
    if (userElement) {
        userElement.setAttribute("active", activeStatus);
        console.log("User " + userId + " active status changed to " + activeStatus);
    } else {
        console.error("User element not found for ID: " + userId);
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

function goToList() {
    window.location.href = "/chat"; // 리스트 페이지 URL 설정
}

function openModal() {
    document.getElementById("modal").classList.add("active");
}

function closeModal() {
    document.getElementById("modal").classList.remove("active");
}