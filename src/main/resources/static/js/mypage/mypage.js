$(document).ready(function() {
    // 'update-btn' 버튼 클릭 시 '/mypageupdate'로 이동
    $('.update-btn').click(function() {
        window.location.href = '/mypageupdate';
    });

    // 'badge-cnt1' 버튼 클릭 시 사용자 ID를 이용해 '/mypageBadge?id=...'로 이동
    $('.badge-cnt1').click(function() {
        var userId = $(this).data('user-id'); // HTML에서 'data-user-id' 속성을 읽음
        if (userId) {
            window.location.href = '/mypageBadge?id=' + userId;
        } else {
            alert('사용자 정보를 찾을 수 없습니다.');
        }
    });

    // 'mypost' 버튼 클릭 시 '/mypageActive?id=...'로 이동
    $('.mypost').click(function() {
        var userId = $(this).attr('data-id');
        window.location.href = '/mypageActive?id=' + userId;
    });

    // 'myComment' 버튼 클릭 시 '/mypageComment?id=...'로 이동
    $('.myComment').click(function() {
        var userId = $(this).attr('data-id');
        window.location.href = '/mypageComment?id=' + userId;
    });

    // 'like-list-inn' 클릭 시 태그 값으로 '/mypageLike?tag=...'로 이동
    $('.like-list-inn').click(function() {
        var tag = $(this).find('.tag-text').text().substring(1);
        window.location.href = '/mypageLike?tag=' + encodeURIComponent(tag);
    });
});
