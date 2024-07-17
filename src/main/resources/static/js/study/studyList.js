 $(document).ready(function() {
    	
    	
        let startDateResults = [];
        let hashtagResults = [];
        let chatRoomResults = [];
        let hashtagPostIds = [];

        // 채팅방 체크 박스 처리 함수
        function updateCheckboxValue(checkbox) {
    checkbox.value = checkbox.checked ? 1 : 0;
    var roomCheck = checkbox.value;
    console.log("Room Check Value:", roomCheck);
    
    if (roomCheck == 1) {
        return $.ajax({
            type: "POST",
            url: "/api/study/searchChatStudy",
            data: JSON.stringify({roomCheck: roomCheck}),
            contentType: "application/json",
            dataType: "json",
            success: function(data) {
                console.log("채팅방 데이터전송 성공", data);
                chatRoomResults = data;
            },
            error: function(error) {
                console.log('채팅방 데이터전송 실패:', error);
            }
        });
    } else {
        // 체크박스가 체크되지 않았을 때는 chatRoomResults를 빈 배열로 설정
        chatRoomResults = [];
        return Promise.resolve(); // 빈 프로미스 반환
    }
}
        window.updateCheckboxValue = updateCheckboxValue;

      

        $('#exampleModal').modal('hide');
        $('#advancedSearchButton').on('click', function() {
            $('#exampleModal').modal('show');
        });

        let hashtags = [];
        const hashtagsContainer = document.querySelector(".hashtag-container");
        const hiddenHashtagsInput = document.createElement("input");
        hiddenHashtagsInput.type = "hidden";
        hiddenHashtagsInput.name = "hashtags";
        document.body.appendChild(hiddenHashtagsInput);

        // 모달이 보여질 때 해시태그 입력 이벤트 바인딩
        $('#exampleModal').on('shown.bs.modal', function () {
            $('#searchtag').off('keyup').on('keyup', function(event) {
                if (event.key === 'Enter') {
                    const tag = $('#searchtag').val().trim();
                    if (tag) {
                        addHashtag(tag);
                        $('#searchtag').val(''); // 입력 필드 초기화
                    }
                }
            });
        });

        // 적용 버튼 클릭 시 처리
       $('#applyButton').on('click', function() {
    const promises = [];

    // 해시태그 처리
    const currentTags = hashtags.map(tag => tag.replace('#', ''));
    if (currentTags.length > 0) {
        promises.push(new Promise(resolve => {
            sendHashtagsToServer(currentTags);
            resolve();
        }));
    }

    // 시작일 처리
    const startDateVal = $('#startdate').val();
    if (startDateVal) {
        promises.push(startDate(startDateVal));
    }

    // 채팅방 처리
    const roomCheck = $('#roomCheck').prop('checked');
    if (roomCheck) {
        promises.push(updateCheckboxValue($('#roomCheck')[0]));
    }

    // 모든 비동기 작업이 완료된 후 중복 체크 실행
    Promise.all(promises).then(() => {
        checkAndProcessDuplicates();
        // 모달 창 닫기
        $('#exampleModal').modal('hide');
    });
});

        // 태그 추가 함수
        function addHashtag(tag) {
            tag = tag.replace(/[\[\]]/g, '').trim();
            if (tag && !hashtags.includes("#" + tag)) { // 중복된 해시태그 방지
                const span = document.createElement("span");
                span.innerText = "#" + tag;
                span.classList.add("hashtag");

                const removeButton = document.createElement("button");
                removeButton.innerText = "x";
                removeButton.classList.add("remove-button");
                removeButton.addEventListener("click", () => {
                    hashtagsContainer.removeChild(span);
                    hashtags = hashtags.filter((hashtag) => hashtag !== "#" + tag);
                    hiddenHashtagsInput.value = hashtags.map(tag => tag.substring(1)).join(",");
                    // 해시태그 제거 후 서버로 보내기
                    sendHashtagsToServer(hashtags);
                });

                span.appendChild(removeButton);
                hashtagsContainer.appendChild(span);
                hashtags.push("#" + tag);
                hiddenHashtagsInput.value = hashtags.map(tag => tag.substring(1)).join(",");

                // AJAX 요청
                sendHashtagsToServer(hashtags);
            }
        }

        // 해시태그를 서버로 보내는 함수
        function sendHashtagsToServer(hashtags) {
    $.ajax({
        type: "POST",
        url: "/api/study/searchHashtags",
        data: JSON.stringify({tags: hashtags}),
        contentType: "application/json",
        dataType: "json",
        success: function(data) {
            console.log("해시태그 업데이트 성공:", data);
            hashtagPostIds = data; // 결과를 전역 변수에 저장
        },
        error: function(error) {
            console.log('해시태그 업데이트 실패:', error);
        }
    });
}

        // 시작일 날짜 값 처리 함수
        function startDate(startDateVal) {
            console.log(startDateVal);
            $.ajax({
                type: "POST",
                url: "/api/study/searchStartdate",
                data: JSON.stringify({startdate: startDateVal}),
                contentType: "application/json",
                dataType: "json",
                success: function(data) {
                    console.log("시작일 데이터전송 성공", data);
                    startDateResults = data; // 결과 저장
                    checkAndProcessDuplicates();
                },
                error: function(error) {
                    console.log('시작일 데이터전송 실패:', error);
                }
            });
        }

        // 중복 체크 및 결과 처리
      function checkAndProcessDuplicates() {
    const activeArrays = [
        startDateResults,
        hashtagPostIds,
        chatRoomResults
    ].filter(array => array && array.length > 0);

    const duplicates = findDuplicates(activeArrays);

    // 결과를 hidden input에 저장
    $('#hiddenDuplicates').val(duplicates.join(','));

    // 콘솔에 결과 출력
    console.log('중복된 값:', duplicates);
}
        // 중복된 값 찾는 함수
        function findDuplicates(arrays) {
            if (arrays.length === 0) return [];

            let duplicates = arrays[0];
            for (let i = 1; i < arrays.length; i++) {
                duplicates = duplicates.filter(value => arrays[i].includes(value));
            }
            return duplicates;
        }
    });
    
   document.addEventListener('DOMContentLoaded', function() {
    const listViewIcon = document.getElementById('listViewIcon');
    const cardViewIcon = document.getElementById('cardViewIcon');
    const listView = document.getElementById('listView');
    const cardView = document.getElementById('cardView');

    // 현재 뷰 상태를 저장하는 변수
    let currentView = 'list';

    function toggleView(viewType) {
        if (viewType === 'list') {
            listView.style.display = 'block';
            cardView.style.display = 'none';
            listViewIcon.classList.add('active');
            cardViewIcon.classList.remove('active');
            currentView = 'list';
        } else {
            listView.style.display = 'none';
            cardView.style.display = 'flex';
            listViewIcon.classList.remove('active');
            cardViewIcon.classList.add('active');
            currentView = 'card';
        }
        // 현재 뷰 상태를 로컬 스토리지에 저장
        localStorage.setItem('currentView', currentView);
    }

    listViewIcon.addEventListener('click', () => toggleView('list'));
    cardViewIcon.addEventListener('click', () => toggleView('card'));

    // 페이지 로드 시 저장된 뷰 상태 확인 및 적용
    const savedView = localStorage.getItem('currentView');
    if (savedView) {
        toggleView(savedView);
    }

    // 페이지네이션 링크 수정
    const paginationLinks = document.querySelectorAll('.pagination .page-link');
    paginationLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const url = new URL(this.href);
            url.searchParams.set('view', currentView);
            window.location.href = url.toString();
        });
    });

    // 검색 폼 수정
    /*const searchForm = document.querySelector('.search-form');
    searchForm.addEventListener('submit', function(e) {
        e.preventDefault();
        const formData = new FormData(this);
        formData.append('view', currentView);
        fetch(this.action, {
            method: 'POST',
            body: formData
        }).then(response => response.text())
          .then(html => {
              document.querySelector('.content-wrapper').innerHTML = html;
          });
    });*/
});