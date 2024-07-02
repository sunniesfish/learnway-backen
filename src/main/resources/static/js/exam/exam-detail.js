const detailRoot = document.getElementById("exam-detail-root");
const path = window.location.pathname;
const parts = path.split('/');
const examId = parts[parts.length - 1];

window.addEventListener("load",()=>render(examId));

function render(examId){
    console.log("rendering")
    ReactDOM.render(<Subjects examId={examId}/>,detailRoot);
}

function Subjects({examId}){
    console.log("Subjects Rendered ",examId)
    const [ pageNo, setPageNo ] = React.useState(1);
    const [ pages, setPages ] = React.useState(0);
    const [ showModal1, setShowModal1 ] = React.useState(false);
    const [ showModal2, setShowModal2 ] = React.useState(false);
    const [ detail, setDetail ] = React.useState();
    const [ data, setData ] = React.useState("");
    const [ list, setList ] = React.useState([]);
    const fetchData = async (pageNo) => {
        const response = await fetch(`/api/score/${examId}/${pageNo}`).then(res => res.json());
        setData(response);
        setList(response.content)
    }
    React.useEffect(()=>{
        fetchData(pageNo);
    },[pageNo]);
    const handleAddSubject = () => setShowModal1(true);
    const handleOverlayClick1 = () => setShowModal1(false);
    const handleOverlayClick2 = () => {
        setShowModal2(false);
        setDetail("")
    };
    return(
        <>
        <div className="exam-detail__container">
            {list.map((subject, index) => 
                <div key={index} className="exam-detail__item">
                    <div 
                        className="exam-detail__item-info" 
                        onClick={() => {
                            setDetail(subject.scoreId);
                            setShowModal2(true);
                        }}
                    >
                        <div className="exam-detail__item__title">{subject.subject.subject}</div>
                        <div className="exam-detail__item__score">
                            <span>{subject.scoreScore}</span>
                            <span>/{subject.scoreExScore}</span>
                        </div>
                        <div className="exam-detail__item__grade">{subject.scoreGrade} 등급</div>
                    </div>
                </div> 
            )}
            <button onClick={handleAddSubject}>+</button>
        </div>
        <div className="pagination">
            { data?
            <>
            <button>prev</button>
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
            <button>next</button>
            </>            
            :
            null
            }
        </div>
        {showModal1 ? <SubjectFormModal handleOverlayClick={handleOverlayClick1} examId={examId}/> : null}
        {showModal2 ? <SubjectDetailModal handleOverlayClick={handleOverlayClick2} scoreId={detail}/> : null}
        </>
    );
}

function SubjectFormModal({handleOverlayClick, examId}) {
    console.log("modal rendered", examId)
    const handleSubmit = async (event) => {
        event.preventDefault();
        const formData = new FormData();
        formData.append("subjectCode",data.subjectCode);
        formData.append("scoreExScore",data.scoreExScore);
        formData.append("scoreScore",data.scoreScore);
        formData.append("scoreGrade",data.scoreGrade);
        formData.append("scoreStdScore",data.scoreStdScore);
        formData.append("scoreMemo",data.scoreMemo);
        await fetch(`/api/${examId}`,{method:"POST", body:formData}).then(res => res.json());
        await handleOverlayClick();
    }

    return(
        <>
        <div className="exam-detail__modal__overlay" onClick={handleOverlayClick}></div>
        <form className="exam-detail__modal__form" onSubmit={handleSubmit}>
            {/* <div>
                <label for="exam-detail__modal__form-name">과목명</label>
                <input id="exam-detail__modal__form-name" type="text" name="su"/>
            </div> */}
            <div>
                <label for="exam-detail__modal__form-score">점수</label>
                <input id="exam-detail__modal__form-score" type="text" name="scoreScore"/>
            </div>
            <div>
                <label for="exam-detail__modal__form-exscore">만점</label>
                <input id="exam-detail__modal__form-exscore" type="text" name="scoreExScore"/>
            </div>
            <div>
                <label for="exam-detail__modal__form-std">표준 점수</label>
                <input id="exam-detail__modal__form-std" type="text" name="scoreStdScore"/>
            </div>
            <div>
                <label for="exam-detail__modal__form-grade">등급</label>
                <input id="exam-detail__modal__form-grade" type="text" name="scoreGrade"/>
            </div>
            <div>
                <label for="exam-detail__modal__form-memo">메모</label>
                <input id="exam-detail__modal__form-memo" type="text" name="scoreMemo"/>
            </div>
            <button>Submit</button>
        </form>
        </>
    );

}
function SubjectDetailModal({handleOverlayClick ,scoreId}){
    const [ data, setData ] = React.useState();
    const fetchData = async(scoreId) => {
        const jsonResponse = await fetch(`/api/score/exam/${scoreId}`).then(res => res.json());
        setData(jsonResponse);
        console.log("response",jsonResponse)
    }
    React.useEffect(()=>{
        fetchData(scoreId);
    },[]);
    console.log("data",data);
    return(
        <>
        <div className="exam-detail__modal__overlay" onClick={handleOverlayClick}></div>
        <div className="exam-detail__modal__detail">
            {data ?
            <>
            <div>{data.exam.examName}</div>
            <div>{data.scoreGrade}등급</div>
            <div>점수 {data.scoreScore}/{data.scoreExScore}</div>
            <div>표준점수 {data.scoreStdScore}</div>
            <div>{data.scoreMemo}</div>
            </> 
            :
            null
            }
        </div>
        </>
    );
}