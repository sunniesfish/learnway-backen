async function fetchExamData(examId) {
    const response = await fetch(`/api/exam/${examId}`);
    if (!response.ok) throw new Error('Failed to fetch stats');
    return response.json();
}

const modBtns = document.querySelectorAll(".exam__modㅡbtn");

document.addEventListener('DOMContentLoaded', function() {
    var today = new Date().toISOString().split('T')[0];
    document.getElementById('exam__modal__form-date').value = today;
});

modBtns.forEach(btn => {
    btn.addEventListener("click", async function(event) {
        const examId = event.target.value
        const examData = await fetchExamData(examId);
        console.log("data",data);
        $('#examModModal').on('show.bs.modal', function (event) {
            // 폼 요소들
            console.log("data",examData)
            const modal = $(this);
            modal.find('#exam__modal__form-examId').val(examData.examId);
            modal.find('#exam__modal__form-name').val(examData.examName);
            modal.find('#exam__modal__form-type').val(examData.examType.examTypeName);
            modal.find('#exam__modal__form-date').val(examData.examDate);
            modal.find('#exam__modal__form-end-date').val(examData.examEndDate);
            modal.find('#exam__modal__form-memo').val(examData.examMemo);
        });
    })
})