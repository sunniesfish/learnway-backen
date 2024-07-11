document.addEventListener('DOMContentLoaded', function() {
    $('.sidebar-item').click(function() {
        $(this).find('.submenu').slideToggle(300);
    });

    $('.consult-link').click(function(event) {
        event.preventDefault(); // 링크의 기본 동작을 막음
        openModal();
    });
});

var modal = document.getElementById("myModal");
var span = document.getElementsByClassName("close")[0];

span.onclick = function() {
    modal.style.display = "none";
}

window.onclick = function(event) {
    if (event.target == modal) {
        modal.style.display = "none";
    }
}

function openModal() {
    modal.style.display = "block";
    loadConsultants();
}

// 모달창에 상담사리스트 불러오기 요청
function loadConsultants() {
    fetch('/api/consultants')
        .then(response => response.json())
        .then(data => {
            const consultantList = document.getElementById('consultantList');
            consultantList.innerHTML = '';
            data.forEach(consultant => {
                const consultantBox = document.createElement('div');
                consultantBox.className = 'modal-consultant-box';

                const img = document.createElement('img');
                img.src = consultant.imageUrl;
                img.alt = `상담사 ${consultant.name} 사진`;

                const consultantHeader = document.createElement('div');
                consultantHeader.className = 'modal-consultant-header';

                const name = document.createElement('h3');
                name.textContent = consultant.name + ' 상담사';

                consultantHeader.appendChild(name);

                const consultantBody = document.createElement('div');
                consultantBody.className = 'modal-consultant-body';

                const subject = document.createElement('p');
                subject.textContent = `상담과목: ${consultant.subject}`;

                const description = document.createElement('p');
                description.textContent = `소개: ${consultant.description}`;

                consultantBody.appendChild(subject);
                consultantBody.appendChild(description);

                const consultantFooter = document.createElement('div');
                consultantFooter.className = 'modal-consultant-footer';

                // 예약하기 버튼을 부트스트랩 버튼으로 변경
                const link = document.createElement('button');
                link.className = 'btn btn-outline-success fixed-size-button'; // 클래스 추가
                link.type = 'button';
                link.textContent = '예약하기';
                link.addEventListener('click', function() {
                    // 예약하기 버튼 클릭 시 동작 설정
                    window.location.href = `/reservationBoard?consultant=${consultant.id}`;
                });

                consultantFooter.appendChild(link);

                consultantBox.appendChild(img);
                consultantBox.appendChild(consultantHeader);
                consultantBox.appendChild(consultantBody);
                consultantBox.appendChild(consultantFooter);

                consultantList.appendChild(consultantBox);
            });
        });
}
