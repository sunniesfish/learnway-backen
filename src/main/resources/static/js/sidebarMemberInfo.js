$(document).ready(function() {
    // 페이지 로드 시 실행되는 코드
    getLoggedInUserInfo(); // 로그인한 사용자 정보를 가져와서 사이드바에 표시

    function getLoggedInUserInfo() {
        // Ajax를 사용하여 서버에서 로그인한 사용자 정보를 가져옵니다.
        $.ajax({
            url: '/api/userinfo', // 실제 API 엔드포인트로 변경해야 합니다.
            type: 'GET',
            success: function(data) {
                // 요청이 성공하면 반환된 데이터에서 사용자 이름을 추출합니다.
                var userName = "👤 " + data.userName; // 예시: 서버에서 반환하는 사용자 이름 필드

                // 사용자 이름을 사이드바에 있는 #loggedInUser 요소에 적용합니다.
                $('#loggedInUser').text(userName);
            },
            error: function(xhr, status, error) {
                console.error('로그인한 사용자 정보를 가져오는 도중 오류 발생:', error);
                // 오류 처리 로직 추가 가능
            }
        });
    }
});