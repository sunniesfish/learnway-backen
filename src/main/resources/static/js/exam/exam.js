async function fetchExamData(examId) {
    const response = await fetch(`/api/exam/${examId}`);
    if (!response.ok) throw new Error('시험 데이터를 불러오는데 실패했습니다');
    return response.json();
}

document.addEventListener('DOMContentLoaded', function() {
    var today = new Date().toISOString().split('T')[0];
    document.getElementById('exam__mod-modal__form-date').value = today;
});

const modBtns = document.querySelectorAll(".exam__modㅡbtn");

modBtns.forEach(btn => {
    btn.addEventListener("click", async function(event) {
        const examId = event.target.value;
        try {
            const examData = await fetchExamData(examId);
            console.log("시험 데이터:", examData);

            // 모달이 표시되기 전에 폼 필드를 채웁니다
            $('#examModModal').on('show.bs.modal', function (event) {
                const modal = $(this);
                modal.find('#exam__mod-modal__form-examId').val(examData.examId);
                modal.find('#exam__mod-modal__form-name').val(examData.examName);
                modal.find('#exam__mod-modal__form-type').val(examData.examType.examTypeName);
                modal.find('#exam__mod-modal__form-date').val(examData.examDate);
                modal.find('#exam__mod-modal__form-end-date').val(examData.examEndDate);
                modal.find('#exam__mod-modal__form-memo').val(examData.examMemo);
            });

            // 모달을 표시합니다
            $('#examModModal').modal('show');
        } catch (error) {
            console.error('시험 데이터를 불러오는 중 오류가 발생했습니다:', error);
            // 필요에 따라 오류 처리를 추가합니다
        }
    });
});
