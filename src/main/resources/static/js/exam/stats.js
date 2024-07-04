//api
async function fetchTypeStats(examType,pageNo){
    return fetch(`/api/stats/${examType}/${pageNo}`).then(res => res.json());
}

//page
console.log("loading")
const statsRoot = document.getElementById("stats-root");

window.addEventListener("load",render);

function render(){
    ReactDOM.render(<Stats/>,statsRoot);
}

function Stats(){
    const [ cat, setCat ] = React.useState("score");
    const [ examType, setExamtype ] = React.useState("mid");

    const [ pageNo, setPageNo ] = React.useState(1);
    const [ pagees, setPages ] = React.useState(1);

    const [ scoreOption, setScoreOption ] = React.useState();
    const [ gradeOption, setGradeOption ] = React.useState();
    const [ stdOption, setStdOption ] = React.useState();
    const [ isOption, setIsOption ] = React.useState(false);

    React.useEffect(()=>{
        setIsOption( scoreOption || gradeOption || stdOption ? true : false);
    },[scoreOption, gradeOption, stdOption]);

    React.useEffect(async () => {
        const statdata = await fetchTypeStats(examType,pageNo);
        console.log("data",statdata);
        const subjects = await fetch("/api/subject/").then(res => res.json());

        let scoreSeries = subjects.map(item => ({ name: item.subject, data: [] }));
        let gradeSeries = subjects.map(item => ({ name: item.subject, data: [] }));
        let stdSeries = subjects.map(item => ({ name: item.subject, data: [] }));
        
        let xasisCat = [...new Set(statdata.content.map(item => item.exam.examDate))];


        statdata.content.forEach(content => {
            scoreSeries.map(item => {
                if(item.name === content.subject.subject){
                    item.data.push(content.scoreScore)
                }
            })
        })
        console.log("scoreSeries",scoreSeries)
        statdata.content.forEach(content => {
            gradeSeries.map(item => {
                if(item.name === content.subject.subject){
                    item.data.push(content.scoreGrade)
                }
            })
        })
        statdata.content.forEach(content => {
            stdSeries.map(item => {
                if(item.name === content.subject.subject){
                    item.data.push(content.scoreStdScore)
                }
            })
        })
        setScoreOption(createOption(scoreSeries,xasisCat, 0, 100, "시험 일자", "점수", false))
        setGradeOption(createOption(gradeSeries,xasisCat, 9, 1, "시험 일자", "등급", true))
        setStdOption(createOption(stdSeries,xasisCat, 0, 150, "시험 일자", "점수", false))

    },[]);
    return(
        <>
        <button onClick={() => setCat("score")}>score</button>
        <button onClick={() => setCat("grade")}>grade</button>
        <button onClick={() => setCat("std")}>std</button>
        {isOption? 
            <ChartType cat={cat} option={
                cat === "score"? scoreOption : 
                cat === "grade"? gradeOption :
                cat === "std" ? stdOption : null
            }/> 
        : null}
        </>
    )
}

function ChartType({cat, option}){
    const chartRef = React.useRef(null);

        
    React.useEffect(()=>{
        const chart = new ApexCharts(document.querySelector(".chart"), option);
        chart.render();
        return () => chart.destroy();
    },[cat]);

    return(
        <div className="chart" ref={chartRef}>

        </div>
    )
}

function createOption(series, xaxisCat, min, max, xaxisTitle, yaxisTitle,reversed){
    console.log("series",series)
    console.log("xasisCat",xaxisCat)
    const option = {
        series: series,
        chart: {    
            height: 700,
            width: "100%",
            type: 'line',
            dropShadow: {
                enabled: true,
                color: '#000',
                top: 18,
                left: 7,
                blur: 10,
                opacity: 0.2
            },
            zoom: {
                enabled: false
            },
            toolbar: {
                show: false
            }
        },
        colors: ['#77B6EA', '#545454','#ECEE81','#8DDFCB','#EDB7ED','#FF0060'],
        dataLabels: {
            enabled: true,
        },
        stroke: {
            curve: 'smooth'
        },
        title: {
            text: '성적',
            align: 'left'
        },
        grid: {
            borderColor: '#e7e7e7', row: {
                    colors: ['#f3f3f3', 'transparent'], // takes an array which will be repeated on columns
                    opacity: 0.5
                },
        },
        markers: {
            size: 1
        },
        xaxis: {
            categories: [...xaxisCat],
            title: {
                text: xaxisTitle
            }
        },
        yaxis: {
            title: {
                text: yaxisTitle
            },
            min: min,
            max: max,
            reversed:reversed
        },
        legend: {
            position: 'top',
            horizontalAlign: 'right',
            floating: true,
            offsetY: -25,
            offsetX: -5
        }
    }
    return option;
}