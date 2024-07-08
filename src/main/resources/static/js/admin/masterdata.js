// 기준정보 등록 시 URL 메세지 확인 후 얼럿창
window.onload = function() {
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('message') === 'success') {
        alert('등록이 완료되었습니다.');
        // URL에서 매개변수 제거
        window.history.replaceState({}, document.title, window.location.pathname);
    } else if (urlParams.get('message') === 'updateSuccess') {
        alert('수정이 완료되었습니다.');
        window.history.replaceState({}, document.title, window.location.pathname);
    } else if (urlParams.get('message') === 'deleteSuccess') {
        alert('삭제가 완료되었습니다.');
        window.history.replaceState({}, document.title, window.location.pathname);
    }
};
function ViewMasterData() {
    const category = $('#viewCategory').val();
    console.log("Selected category:", category); // 확인용 로그

    if (category === '') {
        $('#masterDataTableBody').empty();
        return;
    }

    $.get(`/admin/${category}`, function(data) {
        let rows = '';
        data.forEach(item => {
            const code = item.code || item.materialCode || item.studywayCode || item.subjectCode || item.examCode;
            const name = item.name || item.material || item.studyway || item.subject || item.exam;
            const note = item.note || item.materialNote || item.studywayNote || item.subjectNote || item.examNote;

            rows += `<tr>
                <td>${code}</td>
                <td>${name}</td>
                <td>${note}</td>
                <td><a href="/admin/updateMasterData/${category.slice(0, -1)}/${code}" class="btn btn-outline-success btn-sm">수정</a></td>
                <td>
                    <form action="/admin/deleteMasterData/${category.slice(0, -1)}/${code}" method="post">
                        <button type="submit" class="btn btn-outline-danger btn-sm">삭제</button>
                    </form>
                </td>
            </tr>`;
        });
        $('#masterDataTableBody').html(rows);
    });
}