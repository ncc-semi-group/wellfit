$(document).ready(function () {
    console.log("userId : " + userId);
    function loadChatRooms(url, isMyChatroom = false) {
        $.ajax({
            url: url,
            type: 'GET',
            dataType: 'json',
            success: function (data) {
                $('.chatroom-list').empty(); // 기존 목록 초기화
                data.forEach(function (chatroom) {
                    let chatroomElement = `
                        <div class="chatroom"
                             data-roomid="${chatroom.roomId}"
                             data-roomname="${chatroom.roomName}"
                             data-description="${chatroom.description}"
                             data-usercount="${chatroom.userCount} / ${chatroom.maxUser}"
                             data-roomimage="${chatroom.roomImage}"
                             data-latestmessage="${chatroom.latestMessage || ''}"
                             ${isMyChatroom ? 'onclick="enterChatroom(this)"' : 'data-bs-toggle="modal" data-bs-target="#chatroomModal"'}>
                            <img class="roomImage" src="${chatroom.roomImage}" alt="채팅방 이미지">
                            <div class="chatroom-details">
                                <div>
                                    <div class="chatroom-name"><strong>${chatroom.roomName}</strong></div>
                                    ${chatroom.latestMessage ? `
                                    <div class="latest-message" style="color: gray; font-size: 0.9em;">${chatroom.latestMessage}</div>` : ''}
                                </div>
                            </div>
                            <div class="user-count" style="margin-left:auto;">${chatroom.userCount} / ${chatroom.maxUser}</div>
                        </div>
                    `;
                    $('.chatroom-list').append(chatroomElement);
                });

                if (!isMyChatroom) {
                    // 오픈채팅 클릭 시 모달 내용 변경
                    $('.chatroom').click(function () {
                        let roomName = $(this).data('roomname');
                        let description = $(this).data('description');
                        let userCount = $(this).data('usercount').split(' / ')[0];  // 현재 인원
                        let maxUser = $(this).data('usercount').split(' / ')[1];    // 최대 인원
                        let roomId = $(this).data('roomid');
                        let roomImage = $(this).data('roomimage'); // 👉 이 줄 추가

                        $('#chatroomModal').data('roomid', roomId)
                            .data('usercount', parseInt(userCount))
                            .data('maxuser', parseInt(maxUser));
                        $('#chatroomModalLabel').text(roomName);
                        $('#chatroomModal .modal-body').html(`
                            <div><strong>채팅방 이름:</strong> <span>${roomName}</span></div>
                            <div><strong>인원:</strong> <span>${userCount}</span></div>
                            <div><strong>상세설명:</strong> <span>${description}</span></div>
                            <div><img src="${roomImage}" alt="채팅방 이미지" style="width:100%; border-radius:10px; margin-top:10px;"></div>
                        `);
                    });
                }
            }
        });
    }

    // 나의 채팅방 입장 함수
    window.enterChatroom = function (element) {
        let roomId = $(element).data('roomid');
        const url = "/chatroom/enter/" + roomId+"?userId="+userId;
        window.location.href = url; // 즉시 입장
    };

    // 페이지 로드 시 기본적으로 "오픈채팅" 실행
    loadChatRooms('http://localhost:8080/chatroom/all/'+userId, false);
    $('.btn-openchat').addClass('active');

    // 오픈채팅 버튼 클릭 이벤트
    $('.btn-openchat').click(function () {
        loadChatRooms('http://localhost:8080/chatroom/all/'+userId, false);
        $('.toggle-buttons a').removeClass('active');
        $(this).addClass('active');
    });

    // 나의 채팅방 버튼 클릭 이벤트
    $('.btn-mychat').click(function () {
        loadChatRooms('http://localhost:8080/chatroom/my/'+userId, true);
        $('.toggle-buttons a').removeClass('active');
        $(this).addClass('active');
    });
});
// 입장 버튼 클릭 시
$(document).on('click', '.btn-enter-chat', function () {
    let roomId = $('#chatroomModal').data('roomid');
    let userCount = $('#chatroomModal').data('usercount');
    let maxUser = $('#chatroomModal').data('maxuser');

    // 인원 수 체크
    if (userCount >= maxUser) {
        alert('채팅방이 가득 찼습니다. 입장할 수 없습니다.');
        return;
    }

    // 1. DB에 유저 정보 등록
    $.ajax({
        url: '/chatroom/enter',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            roomId: roomId,
            userId: userId,
            createdAt: new Date().toISOString()
        }),
        success: function (res) {
            console.log("✅ 입장 처리 성공:", res);

            // 2. DB 등록 성공 시, 채팅방으로 이동
            window.location.href = '/chatroom/enter/' + roomId + '?userId=' + userId;
        },
        error: function (xhr) {
            console.error("❌ 입장 실패:", xhr.responseText);
            alert('채팅방 입장 실패: ' + xhr.responseText);
        }
    });
});
