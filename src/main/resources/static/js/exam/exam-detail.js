const detailRoot = document.getElementById("exam-detail-root");
const path = window.location.pathname;
const parts = path.split('/');
const examId = parts[parts.length - 1];

render(examId);

function render(examId){
    ReactDOM.render(<SubjectList examId={examId}/>,detailRoot);
}


function SubjectList({ examId }) {
    const [subjects, setSubjects] = React.useState([]);
    const [examData, setExamData] = React.useState(null);
    const [data, setData] = React.useState(null);

    const childRefs = React.useRef([]); // useRef로 ref 배열 초기화

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
            if (retryCount < 3) {
                setTimeout(() => fetchData(retryCount + 1), 300);
            } else {
                // location.href = "/"                
            }
        }
    };

    const fetchSubjectData = async (retryCount = 0) => {
        try {
            const response = await fetch("/api/subject/");
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const subjectData = await response.json();
            setSubjects(subjectData);
        } catch (error) {
            console.error('Error fetching data:', error);
            if (retryCount < 3) {
                setTimeout(() => fetchSubjectData(retryCount + 1), 300);
            } else {
                // location.href = "/"                
            }
        }
    };

    const fetchExamData = async (retryCount = 0) => {
        try {
            const response = await fetch(`/api/exam/${examId}`);
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const examTempData = await response.json();
            setExamData(examTempData);
        } catch (error) {
            console.error('Error fetching data:', error);
            if (retryCount < 3) {
                setTimeout(() => fetchExamData(retryCount + 1), 500);
            } else {
                // location.href = "/"                
            }
        }
    };

    const onSubmit = () => {
        fetchData();
    };

    const handleSubmitAll = () => {
        console.log("childRefs.current:", childRefs.current); // childRefs.current 로그 추가
        childRefs.current.forEach((childRef) => {
            console.log("childRef:", childRef); // 각 childRef 로그 추가
            if (childRef && childRef.handleRegister) {
                childRef.handleRegister();
            }
        });
    };

    React.useEffect(() => {
        childRefs.current = Array(subjects.length)
            .fill()
            .map((_, index) => childRefs.current[index] || {}); // 객체 배열로 초기화
        console.log("childRefs after setting:", childRefs.current); // 초기화 후 childRefs 로그 추가
    }, [subjects]);

    React.useEffect(() => {
        fetchExamData();
        fetchSubjectData();
        fetchData();
    }, []);

    const setChildRef = (index, ref) => {
        childRefs.current[index] = ref;
    };

    return (
        <>
            <div className="container">
                <div className="d-flex justify-content-between align-items-center mb-3">
                    <div>
                        <button
                            className="btn btn-primary"
                            onClick={() => { location.href = "/exam/list/1" }}
                        >
                            시험목록
                        </button>
                    </div>
                    <div className="text-center exam-detail__title">
                        <span className="d-block">{examData?.examType.examTypeName}</span>
                        <span className="d-block">{examData?.examName}</span>
                        <span className="d-block">{examData?.examDate}</span>
                    </div>
                    <div>
                        <button className="btn btn-success" onClick={handleSubmitAll}>등록</button>
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
                    {data && subjects?.map((subject, index) => {
                        return <Subject
                            setRef={(ref) => setChildRef(index, ref)} // 각 Subject에 setRef 전달
                            key={subject.subjectCode}
                            examId={examId}
                            subject={subject}
                            data={data}
                            onSubmit={onSubmit}
                        />
                    })}
                </tbody>
            </table>
        </>
    );
}

const Subject = ({ subject, data, examId, onSubmit, setRef }) => {
    const [scoreId, setScoreId] = React.useState(null);
    const [score, setScore] = React.useState(0);
    const [exScore, setExScore] = React.useState(100);
    const [std, setStd] = React.useState(0);
    const [grade, setGrade] = React.useState(1);
    const [isModify, setIsModify] = React.useState(false);

    React.useEffect(() => {
        const item = data.find(item => item.subject.subjectCode === subject.subjectCode);
        if (item) {
            setScoreId(item.scoreId || null);
            setScore(item.scoreScore || 0);
            setExScore(item.scoreExScore || 100);
            setStd(item.scoreStdScore || 0);
            setGrade(item.scoreGrade || 1);
        }
    }, [isModify]);

    React.useEffect(() => {
        scoreId ? setIsModify(true) : setIsModify(false);
    }, [scoreId]);

    React.useEffect(() => {
        setRef({
            handleRegister,
            handleModify
        });
    }, [grade,score]); // 빈 배열로 한 번만 설정

    const handleRegister = async () => {
        console.log("handle register",subject.subjectCode,score,exScore,std,grade); // handleRegister 로그 추가
        const formData = new FormData();
        formData.append("subjectCode", subject.subjectCode);
        formData.append("scoreScore", score);
        formData.append("scoreExScore", exScore);
        formData.append("scoreStdScore", std);
        formData.append("scoreGrade", grade);
        const response = await fetch(`/api/score/${examId}`, {
            method: "POST",
            credentials: "include",
            body: formData
        });
        if (response.ok) {
            console.log("성공",response);
        } else {
            console.log("실패",response);
        }
        onSubmit();
    };

    const handleModify = async () => {
        const formData = new FormData();
        formData.append("subjectCode", subject.subjectCode);
        formData.append("scoreId", scoreId);
        formData.append("scoreScore", score);
        formData.append("scoreExScore", exScore);
        formData.append("scoreStdScore", std);
        formData.append("scoreGrade", grade);

        const response = await fetch(`/api/score/${examId}/${scoreId}`, {
            method: "PUT",
            credentials: "include",
            body: formData
        });
        if (response.ok) {
            console.log("성공");
        }
        onSubmit();
    };

    return (
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
            <td></td>
        </tr>
    );
};
