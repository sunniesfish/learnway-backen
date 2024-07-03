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
    return(
        <>
        <button onClick={() => setCat("score")}>score</button>
        <button onClick={() => setCat("grade")}>grade</button>
        <button onClick={() => setCat("std")}>std</button>
        <h1>test</h1>
        <ChartType cat={cat}/> 
        </>
    )
}

function ChartType({cat}){
    console.log("rendering chart")
    const chartRef = React.useRef(null);
    React.useEffect(async () => {

        console.log("in useEffect")

        const data = await fetchTypeStats("A",1);
        const subjects = await fetch("/api/subject/").then(res => res.json());
        const option = {};

        console.log("data",data)
        console.log("subjects",subjects);

        let series = subjects.map(item => ({ name: item.subject, data: [] }));
        console.log("series",series)
        let scoreSeries = [...series];
        let gradeSeries = [...series];
        let stdSeries = [...series];

        let xasisCat = data.content.map(item => item.exam.examDate);
        console.log("xasisCat",xasisCat)

        data.content.forEach(content => {
            scoreSeries.map(item => {
                if(item.name === content.subject.subject){
                    item.data.push(content.scoreScore)
                }
            })
        })
        console.log("scoreSeries",scoreSeries)
        data.content.forEach(content => {
            gradeSeries.map(item => {
                if(item.name === content.subject.subject){
                    item.data.push(content.scoreGrade)
                }
            })
        })
        console.log("gradeSeries",gradeSeries)
        data.content.forEach(content => {
            stdSeries.map(item => {
                if(item.name === content.subject.subject){
                    item.data.push(content.scoreStdScore)
                }
            })
        })
        console.log("stdSeries",stdSeries)
        if(cat === "score"){
            option = setOption(scoreSeries,xasisCat, "시험 일자", "점수")
        }else if(cat === "grade"){
            option = setOption(gradeSeries,xasisCat, "시험 일자", "등급")
        }else if(cat === "std"){
            option = setOption(stdSeries,xasisCat, "시험 일자", "점수")
        }

        const chart = new ApexCharts(document.querySelector(".chart"), option);
        chart.render();

        return () => chart.destroy();
    },[])
    return(
        <div className="chart" ref={chartRef}>

        </div>
    )
}

function setOption({series, xaxisCat, xaxisTitle, yaxisTitle }){
    const option = {
        series: series,
        chart: {
            height: 350,
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
        colors: ['#77B6EA', '#545454'],
        dataLabels: {
            enabled: true,
        },
        stroke: {
            curve: 'smooth'
        },
        title: {
            text: 'Average High & Low Temperature',
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
            min: 5,
            max: 40
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