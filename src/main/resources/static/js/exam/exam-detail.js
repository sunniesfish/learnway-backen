const detailRoot = document.getElementById("exam-detail-root");
const path = window.location.pathname;
const parts = path.split('/');
const examId = parts[parts.length - 1];

render(examId);

function render(examId){
    ReactDOM.render(<SubjectList examId={examId}/>,detailRoot);
}

function SubjectList ({examId}) {
    const [ subjects, setSubjects ] = React.useState([]);
    const [ examData, setExamData ] = React.useState(null);
    const [ data, setData ] = React.useState(null);

    
    const fetchData = async (retryCount = 0) => {
        try {
            const response = await fetch(`/api/score/${examId}/1`);
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const jsonData = await response.json();
            setData(jsonData.content);
        } catch (error) {
            console.error('Error fetching data:', error);
            if (retryCount < 3) { // Retry up to 3 times
                setTimeout(() => fetchData(retryCount + 1), 300); // Retry after 1 second
            } else {
                // location.href = "/"                
            }
        }
    };
    const fetchSubjectData = async (retryCount = 0) => {
        try{
            const response = await fetch("/api/subject/");
            if(!response.ok){
                throw new Error('Network response was not ok');
            }
            const subjectData = await response.json();
            setSubjects(subjectData)
        } catch (error) {
            console.error('Error fetching data:', error);
            if (retryCount < 3) { // Retry up to 3 times
                setTimeout(() => fetchSubjectData(retryCount + 1), 300); // Retry after 1 second
            } else {
                // location.href = "/"                
            }
        }
    }
    const fetchExamData = async(retryCount = 0) => {
        try{
            const response = await fetch(`/api/exam/${examId}`);
            if(!response.ok){
                throw new Error('Network response was not ok');
            }
            const examTempData = await response.json();
            setExamData(examTempData);
        } catch (error) {
            console.error('Error fetching data:', error);
            if (retryCount < 3) { // Retry up to 3 times
                setTimeout(() => fetchExamData(retryCount + 1), 500); // Retry after 1 second
            } else {
                // location.href = "/"                
            }
        }
    }
    
    const onSubmit = () => {
        fetchData();
    }
    React.useEffect(()=>{
        fetchExamData();
        fetchSubjectData();
        fetchData();
    },[])
    
    return (
        <>
        <div class="container">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <div>
                    <button 
                        class="btn btn-primary"
                        onClick={()=>{location.href = "/exam/list/1"}}
                        >
                        시험목록
                    </button>
                </div>
                <div class="text-center exam-detail__title">
                    <span class="d-block">{examData?.examType.examTypeName}</span>
                    <span class="d-block">{examData?.examName}</span>
                    <span class="d-block">{examData?.examDate}</span>
                </div>
                <div>
                    <button class="btn btn-success">전체 등록</button>
                </div>
            </div>
        </div>
        <table className="table table-striped mt-4">
            <thead>
                <tr>
                    <th>과목</th>
                    <th>점수</th>
                    <th>표준점수</th>
                    <th>등급</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                {data && subjects?.map( subject => 
                    {
                        return <Subject 
                            key={subject.subjectCode} 
                            examId={examId} 
                            subject={subject} 
                            data={data}
                            onSubmit={onSubmit}
                        />
                    }
                )}
            </tbody>
        </table>
        </>
    )
}

function Subject({subject, data, examId, onSubmit}){

    const [ scoreId, setScoreId] = React.useState(null);
    const [score, setScore] = React.useState(0);
    const [exScore, setExScore] = React.useState(100);
    const [std, setStd] = React.useState(0);
    const [grade, setGrade] = React.useState(1);
    const [ isModify, setIsModify ] = React.useState(false);

    React.useEffect(()=>{
        if(data){
            data.map( item => {
                if(subject.subjectCode === item.subject.subjectCode) {
                    item.scoreId && setScoreId(item.scoreId);
                    item.scoreScore && setScore(item.scoreScore);
                    item.scoreExScore && setExScore(item.scoreExScore);
                    item.scoreStdScore && setStd(item.scoreStdScore);
                    item.scoreGrade && setGrade(item.scoreGrade);
                }
            })
        }   
    },[isModify]);

    
    React.useEffect(()=>{
        scoreId ? setIsModify(true) : setIsModify(false);
    },[scoreId])

    
    const handleRegister = async (event) => {
        event.preventDefault();
        const formData = new FormData();
        formData.append("subjectCode", subject.subjectCode);
        formData.append("scoreScore", score);
        formData.append("scoreExScore", exScore);
        formData.append("scoreStdScore", std);
        formData.append("scoreGrade", grade);

        const respone = await fetch(`/api/score/${examId}`,{
            method:"POST",
            credentials: "include",
            body:formData
        });
        onSubmit();
    };
    const handleModify = async (event) => {
        event.preventDefault();
        const formData = new FormData();
        formData.append("subjectCode", subject.subjectCode);
        formData.append("scoreId", scoreId);
        formData.append("scoreScore", score);
        formData.append("scoreExScore", exScore);
        formData.append("scoreStdScore", std);
        formData.append("scoreGrade", grade);

        const response = await fetch(`/api/score/${examId}/${scoreId}`,{
            method:"PUT",
            credentials: "include",
            body:formData
        });
        onSubmit();
    }
    const handleDelete = async (scoreId) => {
        const response =  await fetch(`/api/score/${examId}/${scoreId}`,{method:"DELETE"});
        if(response.ok) {
            setExScore(100);
            setGrade(1);
            setScore(0);
            setScoreId(null);
            setStd(0);
        }
    }
    return (
        <>
        <tr>
            <td>{subject.subject}</td>
            <td>
                <input className="exam-detail__row-input ed-score" value={score} required onChange={(event) => setScore(event.target.value)} type="text" name="scoreScore" />
                <input className="exam-detail__row-input ed-exscore" value={exScore} required onChange={(event) => setExScore(event.target.value)} type="text" name="scoreExScore" />
            </td>
            <td>
                <input className="exam-detail__row-input ed-std" value={std} required onChange={(event) => setStd(event.target.value)} type="text" name="scoreStdScore" />
            </td>
            <td>
                <input className="exam-detail__row-input ed-grade" value={grade} required onChange={(event) => setGrade(event.target.value)} type="text" name="scoreGrade" />
            </td>
            <td>
                <input value={scoreId} className="hidden" name="scoreId"/>
                <input value={subject.subjectCode} className="hidden" name="subjectCode"/>
                {isModify? 
                <>
                <button onClick={handleModify}>수정</button>
                <button onClick={() => handleDelete(scoreId)}>초기화</button>
                </>
                : 
                <button onClick={handleRegister}>등록</button>
                }
                
            </td>
        </tr>
        </>
    )
}