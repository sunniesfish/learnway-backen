//api
const BASE = "/api/stats"
async function fetchSubjectStats(subjectCode){
    return fetch(`${BASE}/subject/${subjectCode}`).then(res => res.json());
}

async function fetchAllStats(){
    return fetch(`${BASE}/all`).then(res => res.json());
}

async function fetchTypeStats(examType){
    return fetch(`${BASE}/type/${examType}`).then(res => res.json());
}

//page
const statsRoot = document.getElementById("stats-root");

window.addEventListener("load",()=>render());

function render(){
    ReactDOM.render(<Stats/>,detailRoot);
}

function Stats(){
    const [ selector, setSelector ] = React.useState("all")
    return(
        <>
        <div className="stats__selector">
            
        </div>
        {selector === "all" && <ChartAll/>}       
        {selector === "subject" && <ChartSubject/>}       
        {selector === "type" && <ChartType/>}       
        </>
    )
}

function ChartAll(){
    const option = {}
    const chart = new ApexChart(document.querySelector(".chart"), option);
    React.useEffect(()=>{
        chart.render();
    },[])
    return(
        <div className="chart">

        </div>
    )
}
function ChartSubject(){
    const option = {}
    const chart = new ApexChart(document.querySelector(".chart"), option);
    React.useEffect(()=>{
        chart.render();
    },[])
    return(
        <div className="chart">

        </div>
    )
}
function ChartType(){
    const option = {}
    const chart = new ApexChart(document.querySelector(".chart"), option);
    React.useEffect(()=>{
        chart.render();
    },[])
    return(
        <div className="chart">

        </div>
    )
}