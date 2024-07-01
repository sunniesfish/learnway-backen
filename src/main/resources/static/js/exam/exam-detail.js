const detailRoot = document.getElementById("exam-detail-root");

window.addEventListener("load",render);

function render(examId){
    ReactDom.render(<Subjects examId={examId}/>,detailRoot);
}

function Subjects({examId}){
    const [ pageNo, setPageNo ] = useState(1);
    const [ showModal, setShowModal ] = useState(false);
    useEffect(()=>{
        const data = async(pageNo) => await fetch(`/api/scoreId/${examId}/${pageNo}`).then(res => res.json())
    },[pageNo]);
    const handleAddSubject = () => setShowModal(true);
    const handleOverlayClick = () => setShowModal(false);
    return(
        <>
        <div className="exam-detail__container">
            {data.map((subject, index) => 
            <div key={index} className="exam-detail__item">
                    <div className="exam-detail__item__title">{subject.subject.subject}</div>
                    <div className="exam-detail__item__score">
                        <span>{subject.scoreScore}</span>
                        <span>/{subject.scoreExScore}</span>
                    </div>
                    <div className="exam-detail__item__grade">{subject.scoreGrade} 등급</div>
            </div> 
            )}
            <button onClick={handleAddSubject}>+</button>
        </div>
        <div className="pagination">

        </div>
        {showModal ? <SubjectFormModal handleOverlayClick={handleOverlayClick} examId={subject.examId}/> : null}
        </>
    );
}

function SubjectFormModal({handleOverlayClick, examId}) {
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
        <form onSubmit={handleSubmit} className="exam-detail__modal__form">
            <input type="text"/>
            <input type="text"/>
            <input type="text"/>
            <input type="text"/>
            <button>Submit</button>
        </form>
        </>
    );
}