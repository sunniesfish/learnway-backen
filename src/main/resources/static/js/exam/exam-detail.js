const detailRoot = document.getElementById("exam-detail-root");
const path = window.location.pathname;
const parts = path.split('/');
const examId = parts[parts.length - 1];

console.log("new exam-detail")
render(examId);

function render(examId){
    ReactDOM.render(<SubjectList examId={examId}/>,detailRoot);
}

function SubjectList ({examId}) {
    const [ subjects, setSubjects ] = React.useState([]);
    const [ examData, setExamData ] = React.useState(null);
    const [ onModify, setOnModify ] = React.useState(false);
    const [ onRegister, setOnRegister ] = React. useState(false);
    const [ onView, setOnView ] = React.useState(true);
    const [ data, setData ] = React.useState(null);

    const setViewTrue = () => {
        setOnModify(false);
        setOnRegister(false);
    }
    const onSubmit = () => {
        fetchData();
    }
    const fetchData = async (retryCount = 0) => {
        try {
            const response = await fetch(`/api/score/${examId}/1`);
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const jsonData = await response.json();
            if(!jsonData.content) {
                setOnModify(false);
            }
            setData(jsonData.content);
        } catch (error) {
            console.error('Error fetching data:', error);
            if (retryCount < 5) { // Retry up to 3 times
                setTimeout(() => fetchData(retryCount + 1), 1000); // Retry after 1 second
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
            console.log("subjectData",subjectData)
            setSubjects(subjectData)
        } catch (error) {
            console.error('Error fetching data:', error);
            if (retryCount < 5) { // Retry up to 3 times
                setTimeout(() => fetchSubjectData(retryCount + 1), 1000); // Retry after 1 second
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
            console.log("exemData",examTempData)
            setExamData(examTempData);
        } catch (error) {
            console.error('Error fetching data:', error);
            if (retryCount < 5) { // Retry up to 3 times
                setTimeout(() => fetchExamData(retryCount + 1), 1000); // Retry after 1 second
            }
        }
    }
    
    React.useEffect(()=>{
        console.log("in useEffect")
        fetchExamData();
        fetchSubjectData();
        fetchData();
    },[])

    return (
        <>
        <div className="d-flex mb-3">
            <div>
                <button onClick={()=>{
                    location.href = "/exam/list/1"
                }}>
                    시험목록
                </button>
            </div>
            <div>
                <span>{examData?.examType.examTypeName}</span>
                <span>{examData?.examName}</span>
                <span>{examData?.examDate}</span>
            </div>
            <div>
                <button>보기</button>
                <button>수정</button>
                <button>삭제</button>
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
                        console.log("1",subject);
                        console.log("2",data)
                        return <Subject 
                            key={subject.subjectCode} 
                            examId={examId} 
                            subject={subject} 
                            data={data} 
                            onModify={onModify} 
                            onRegister={onRegister} 
                            onView={onView}
                            onSubmit={onSubmit}
                        />
                    }
                )}
            </tbody>
        </table>
        </>
    )
}

function Subject({subject, data, onModify, onRegister, onView, examId, onSubmit}){
    const [ scoreId, setScoreId] = React.useState(null);
    const [score, setScore] = React.useState(100);
    const [exScore, setExScore] = React.useState(100);
    const [std, setStd] = React.useState(100);
    const [grade, setGrade] = React.useState(1);

    React.useEffect(()=>{
        if(data){
            data.map( item => {
                if(subject.subjectCode === item.subject.subjectCode) {
                    console.log("useEffect item",item)
                    item.scoreId && setScoreId(item.scoreId);
                    item.scoreScore && setScore(item.scoreScore);
                    item.scoreExScore && setExScore(item.scoreExScore);
                    item.scoreStdScore && setStd(item.scoreStdScore);
                    item.scoreGrade && setGrade(item.scoreGrade);
                }
            })
        }
    },[]);

    
    const handleRegister = (event) => {
        event.preventDefault();
        const formData = new FormData();
        formData.append("subjectCode", subject.subjectCode);
        formData.append("scoreScore", score);
        formData.append("scoreExScore", exScore);
        formData.append("scoreStdScore", std);
        formData.append("scoreGrade", grade);

        fetch(`/api/score/${examId}`,{
            method:"POST",
            credentials: "include",
            body:formData
        }).then(res => console.log(res));
        onSubmit();
    };
    const handleModify = (event) => {
        event.preventDefault();
        const formData = new FormData();
        formData.append("subjectCode", subject.subjectCode);
        formData.append("scoreId", scoreId);
        formData.append("scoreScore", score);
        formData.append("scoreExScore", exScore);
        formData.append("scoreStdScore", std);
        formData.append("scoreGrade", grade);

        fetch(`/api/score/${examId}/${scoreId}`,{
            method:"PUT",
            credentials: "include",
            body:formData
        }).then(res => console.log(res));
        onSubmit();
    }
    const handleDelete = (scoreId) => {
        fetch(`/api/score/${examId}/${scoreId}`,{method:"DELETE"});
    }
    return (
        <>
        <tr>
            <td>{subject.subject}</td>
            <td>
                <input value={score} required onChange={(event) => setScore(event.target.value)} type="text" name="scoreScore" readOnly={onView} />
                <input value={exScore} required onChange={(event) => setExScore(event.target.value)} type="text" name="scoreExScore" readOnly={onView} />
            </td>
            <td>
                <input value={std} required onChange={(event) => setStd(event.target.value)} type="text" name="scoreStdScore" readOnly={onView} />
            </td>
            <td>
                <input value={grade} required onChange={(event) => setGrade(event.target.value)} type="text" name="scoreGrade" readOnly={onView} />
            </td>
            <td>
                <input value={scoreId} className="hidden" name="scoreId"/>
                <input value={subject.subjectCode} className="hidden" name="subjectCode"/>
                {onRegister && <button onClick={handleRegister}>등록</button>}
                {onModify && <button onClick={handleModify}>수정</button>}
                {onView && scoreId && <button onClick={() => handleDelete(scoreId)}>삭제</button>}
            </td>
        </tr>
        </>
    )
}