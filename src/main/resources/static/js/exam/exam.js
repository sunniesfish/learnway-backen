async function fetchExamData(examId) {
    try {
        const response = await fetch(`/api/exam/${examId}`);
        if (!response.ok) throw new Error('Failed to fetch stats');
        return response.json();
    } catch (error){
        console.error('Error fetching data:', error);
        location.href = "/"                
    }
}

document.addEventListener('DOMContentLoaded', function() {
    var today = new Date().toISOString().split('T')[0];
    document.getElementById('exam__modal__form-date').value = today;
});

document.addEventListener("click", async function(event) {
    console.log(event.target);
    const data = await fetchExamData(3);
    console.log("data",data)
})