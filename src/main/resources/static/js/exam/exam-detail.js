const detailRoot = document.getElementById("exam-detail-root");

window.addEventListener("load",render);

function render(examId){
    ReactDom.render(<Subjects examId={examId}/>,detailRoot);
}

function Subjects({examId}){
    const [ pageNo, setPageNo ] = useState(1);
    useEffect(()=>{
        const data = async(pageNo) => await fetch(`/api/scoreId/${examId}/${pageNo}`).then(res => res.json())
    },[pageNo]);
    return(
        <div className="exam-detail__container">
            {data.map((subject, index) => 
               <div key={index} className="exam-detail__item">
                    <div>{subject}</div>
               </div> 
            )}
        </div>
    );
}