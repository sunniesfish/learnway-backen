const examDetailBtn = document.querySelector(".exam-detail__detail-btn");
const examGoBackBtn = document.querySelector(".exam-detail__goback-btn")
const examDetailModal = document.querySelector(".exam-detail__modal__info");
const examModalOverlay = document.querySelector(".exam-detail__modal__detail-overlay");
examDetailBtn.addEventListener("click",handleRegBtnClick);
examGoBackBtn.addEventListener("clicl",()=>{
    location.href = "/exam/list/1"
})

function handleRegBtnClick(event){
    event.preventDefault();
    examDetailModal.classList.remove("hidden")
    examModalOverlay.addEventListener("click",()=>{
        examDetailModal.classList.add("hidden")
    });
}


const detailRoot = document.getElementById("exam-detail-root");
const path = window.location.pathname;
const parts = path.split('/');
const examId = parts[parts.length - 1];

window.onload = () => render(examId);

function render(examId){
    console.log("render",examId)
    ReactDOM.render(<Subjects examId={examId}/>,detailRoot);
}

function Subjects({examId}){
    const [ pageNo, setPageNo ] = React.useState(1);
    const [ pages, setPages ] = React.useState(0);
    const [ showModal1, setShowModal1 ] = React.useState(false);
    const [ showModal2, setShowModal2 ] = React.useState(false);
    const [ onModify, setOnModify ] = React.useState(false);
    const [ detail, setDetail ] = React.useState();
    const [ data, setData ] = React.useState("");
    const [ list, setList ] = React.useState([]);
    const fetchData = async (pageNo) => {
        // const response = await fetch(`/api/score/${examId}/${pageNo}`).then(res => res.json());
        // setData(response);
        // setList(response.content)
        try {
            const response = await fetch(`/api/score/${examId}/${pageNo}`);
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const data = await response.json();
            setData(data);
            setList(data.content);
        } catch (error) {
            console.error('Error fetching data:', error);
        }
    }
    React.useEffect(()=>{
        fetchData(pageNo);
    },[pageNo, showModal1 ]);
    const handleDelete = async (scoreId) => {
        await fetch(`/api/score/${examId}/${scoreId}`,{method:"DELETE"}).then(res => console.log(res));
        fetchData(pageNo);
    }
    const handleAddSubject = () => setShowModal1(true);
    const handleOnModify = () => {
        setOnModify(true);
        setShowModal2(false);
        setShowModal1(true);
        
    }
    const handleOverlayClick1 = () => {
        setOnModify(false);
        setShowModal1(false);
    }
    const handleOverlayClick2 = () => {
        setShowModal2(false);
        setOnModify(false);
        setDetail("")
    };
    const handleGoPrev = () => {
        pageNo > 1 && setPageNo(prev => prev - 1);
    }
    const handleGoNext = () => {
        pageNo < data.totalPages && setPageNo(prev => prev + 1);
    }
    return(
        <>
        <div className="exam-detail__container">
            {list.map((item, index) => 
                <div key={index} className="exam-detail__item">
                    <div 
                        className="exam-detail__item-info" 
                        onClick={() => {
                            setDetail(item.scoreId);
                            setShowModal2(true);
                        }}
                    >
                        <div className="exam-detail__item__title">{item.subject?.subject}</div>
                        <div className="exam-detail__item__score">
                            <span>{item.scoreScore}</span>
                            <span>/{item.scoreExScore}</span>
                        </div>
                        <div className="exam-detail__item__grade">{item.scoreGrade}등급</div>
                    </div>
                    <div className="exam-detail__item__delete-btn-controller">
                        <button onClick={() => handleDelete(item.scoreId)}>
                            <svg className="exam-detail__item__delete-svg" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 448 512">
                                <path d="M135.2 17.7L128 32H32C14.3 32 0 46.3 0 64S14.3 96 32 96H416c17.7 0 32-14.3 32-32s-14.3-32-32-32H320l-7.2-14.3C307.4 6.8 296.3 0 284.2 0H163.8c-12.1 0-23.2 6.8-28.6 17.7zM416 128H32L53.2 467c1.6 25.3 22.6 45 47.9 45H346.9c25.3 0 46.3-19.7 47.9-45L416 128z"/>
                            </svg>
                        </button>
                    </div>
                </div> 
            )}
            <button onClick={handleAddSubject}>+</button>
        </div>
        <div className="pagination">
            { data?
            <>
            <button onClick={handleGoPrev}>prev</button>
                {Array.from(
                    {length:data.totalPages > pages + 5? 5 : data.totalPages % 5},
                    (_,i) => i
                ).map(index => {
                    return (
                        <div 
                            key={index} 
                            className="clickable pagination__page-btn"
                            style={{
                                "backgroundColor": `${index+1+pages === pageNo ?  "#1565FF" : "#F5F8FF"}`,
                                "color": `${index+1+pages === pageNo? "white" : "black"}`,
                            }}
                            onClick = {() => setPageNo(index + 1 + pages)}
                        >
                            {index + 1 + pages}
                        </div>
                    )
                })}
            <button onClick={handleGoNext}>next</button>
            </>            
            :
            null
            }
        </div>
        {showModal1 ? <SubjectFormModal handleOverlayClick={handleOverlayClick1} scoreId={detail} examId={examId} onModify={onModify}/> : null}
        {showModal2 ? <SubjectDetailModal handleOverlayClick={handleOverlayClick2} scoreId={detail} handleOnModify={handleOnModify}/> : null}
        </>
    );
}

function SubjectFormModal({handleOverlayClick, examId, onModify, scoreId}) {
    console.log("modal rendered", examId)
    const [ subjects, setSubjects ] = React.useState("");
    
    const [ subjectCode, setSubjectCode ] = React.useState("");
    const [ score, setScore ] = React.useState(100);
    const [ exScore, setExScore ] = React.useState(100);
    const [ std, setStd ] = React.useState(100);
    const [ grade, setGrade ] = React.useState(1);
    const [ memo, setMemo ] = React.useState("");
    
    const fetchData = async(scoreId) => {
        const response = await fetch(`/api/score/exam/${scoreId}`).then(res => res.json());
        setSubjectCode(response.subject.subjectCode);
        setScore(response.scoreScore);
        setExScore(response.scoreExScore);
        setStd(response.scoreStdScore);
        setGrade(response.scoreGrade);
        setMemo(response.scoreMemo? response.scoreMemo : "");
    }
    const fetchSubjectData = async () => {
        const response = async () => fetch("/api/subject/").then(res => res.json());
        const subjectData = await response();
        setSubjects(subjectData)
        if(onModify) {
            fetchData(scoreId);
        } else {
            setSubjectCode(subjectData[0].subjectCode);
        }
    }
    const handleSubmit = async (event) => {
        event.preventDefault();
        const formData = new FormData();
        formData.append("examId",examId);
        formData.append("subjectCode",subjectCode);
        formData.append("scoreExScore", exScore);
        formData.append("scoreScore", score);
        formData.append("scoreGrade", grade);
        formData.append("scoreStdScore", std);
        formData.append("scoreMemo", memo);
        if (onModify) {
            await fetch(`/api/score/${examId}/${scoreId}`,{
                method:"PUT",
                credentials: "include",
                body: formData
            }).then(res => console.log(res));
        } else {
            await fetch(`/api/score/${examId}`,{
                method:"POST",
                credentials: "include",
                body:formData
            }).then(res => console.log(res));
        }
        setSubjectCode(subjects? subjects[0].subjectCode : "");
        setScore(100)
        setExScore(100);
        setStd(100);
        setGrade(1)
        setMemo("");
        handleOverlayClick();
    }
    const handleOptionChange = (event) => {
        console.log("option",event);
        setSubjectCode(event.target.value)
    }

    React.useEffect(()=>{
        fetchSubjectData()
    },[])
    return(
        <>
        <div className="exam-detail__modal__overlay" onClick={handleOverlayClick}></div>
        <form className="exam-detail__modal__form" onSubmit={handleSubmit}>
            <div>
                <label for="exam-detail__modal__form-subject">과목</label>
                <select 
                    id="exam-detail__modal__form-subject" 
                    name="subjectCode" 
                    key={subjectCode} 
                    defaultValue={subjectCode} 
                    onChange={handleOptionChange}
                >
                    {subjects && subjects.map(subject => 
                        <option key={subject.subjectCode} value={subject.subjectCode}>{subject.subject}</option>
                    )}
                </select>
            </div>
            <div>
                <label for="exam-detail__modal__form-score">점수</label>
                <input value={score} required onChange={(event) => setScore(event.target.value)} id="exam-detail__modal__form-score" type="text" name="scoreScore"/>
            </div>
            <div>
                <label for="exam-detail__modal__form-exscore">만점</label>
                <input value={exScore} required onChange={(event) => setExScore(event.target.value)} id="exam-detail__modal__form-exscore" type="text" name="scoreExScore"/>
            </div>
            <div>
                <label for="exam-detail__modal__form-std">표준 점수</label>
                <input value={std} required onChange={(event) => setStd(event.target.value)} id="exam-detail__modal__form-std" type="text" name="scoreStdScore"/>
            </div>
            <div>
                <label for="exam-detail__modal__form-grade">등급</label>
                <input value={grade} required onChange={(event) => setGrade(event.target.value)} id="exam-detail__modal__form-grade" type="text" name="scoreGrade"/>
            </div>
            <div>
                <label for="exam-detail__modal__form-memo">메모</label>
                <input value={memo} onChange={(event) => setMemo(event.target.value)} id="exam-detail__modal__form-memo" type="text" name="scoreMemo"/>
            </div>
            {onModify? <input value={scoreId? scoreId : null} type="hidden" name="scoreId"/> : null}
            <button>Submit</button>
        </form>
        </>
    );
}

function SubjectDetailModal({handleOverlayClick ,scoreId, handleOnModify}){
    const [ data, setData ] = React.useState();
    const fetchData = async(scoreId) => {
        const jsonResponse = await fetch(`/api/score/exam/${scoreId}`).then(res => res.json());
        setData(jsonResponse);
    }
    React.useEffect(()=>{
        fetchData(scoreId);
    },[]);
    console.log(data)
    return(
        <>
        <div className="exam-detail__modal__overlay" onClick={handleOverlayClick}></div>
        <div className="exam-detail__modal__detail">
            {data ?
            <>
            <div>{data.subject.subject}</div>
            <div>{data.scoreGrade}등급</div>
            <div>점수 {data.scoreScore}/{data.scoreExScore}</div>
            <div>표준점수 {data.scoreStdScore}</div>
            <div>{data.scoreMemo}</div>
            </> 
            :
            null
            }
            <button onClick={handleOnModify}>수정</button>
        </div>
        </>
    );
}