async function fetchTypeStats(examType, year, retryCount = 0, maxRetries = 2) {
    try {

        const response = await fetch(`/api/stats/${examType}/${year}`);
        if (!response.ok) throw new Error('Failed to fetch stats');
        return response.json();
    } catch (error){
        console.error('Error fetching data:', error);
        if (retryCount < maxRetries) {
            setTimeout(() => fetchTypeStats(examType, year, retryCount + 1, maxRetries), 300); // Retry after 1 second
        } else {
            // location.href = "/"                
        }
    }
}

const statsRoot = document.getElementById("stats-root");

render();

function render() {
    console.log("render")
    ReactDOM.render(<Stats />, statsRoot);
}

function Stats() {
    console.log("stat")
    const [cat, setCat] = React.useState("score");
    const [examType, setExamType] = React.useState("all");
    const [examTypeList, setExamTypeList] = React.useState(["all"]);
    const [year, setYear] = React.useState(2024);
    const [scoreOption, setScoreOption] = React.useState();
    const [gradeOption, setGradeOption] = React.useState();
    const [stdOption, setStdOption] = React.useState();
    const [isOption, setIsOption] = React.useState(false);

    const fetchData = async (retryCount = 0) => {
        try {
            const statdata = await fetchTypeStats(examType, year);
            console.log("fetchData", statdata)
            
            const subjects = await fetch("/api/subject/").then(res => {
                if (!res.ok) throw new Error('Failed to fetch subjects');
                return res.json();
            });
    
            let scoreSeries = subjects.map(item => ({ name: item.subject, data: [] }));
            let gradeSeries = subjects.map(item => ({ name: item.subject, data: [] }));
            let stdSeries = subjects.map(item => ({ name: item.subject, data: [] }));
    
            let xasisCat = statdata.map(exam => exam.examDate);
    
            const addDataToSeries = (series, dataKey) => {
                console.log("series",series)
                console.log("dataKey",dataKey)
                statdata.forEach(exam => {
                    console.log("in forEach exam",exam)
                    series?.forEach(seriesData => {
                        exam.scoreList?.forEach(score => {
                            if (seriesData.name === score.subject.subject) {
                                console.log("seriesData.name subject.subject", dataKey, seriesData.name, score.subject.subject);
                                seriesData.data.push(score[dataKey]);
                            }
                        });
                    });
                });
            };
    
            addDataToSeries(scoreSeries, 'scoreScore');
            addDataToSeries(gradeSeries, 'scoreGrade');
            addDataToSeries(stdSeries, 'scoreStdScore');
    
            setScoreOption(createOption(scoreSeries, xasisCat, 0, 100, "시험 일자", "점수", false));
            setGradeOption(createOption(gradeSeries, xasisCat, 1, 9, "시험 일자", "등급", true));
            setStdOption(createOption(stdSeries, xasisCat, 0, 150, "시험 일자", "표준점수", false));

            console.log("scoreSeries",scoreSeries);
            console.log("gradeSeries",gradeSeries);
            console.log("stdSeries",stdSeries);

        } catch (error) {
            console.error('Error fetching data:', error);
            if (retryCount < 5) {
                setTimeout(() => fetchData(retryCount + 1), 1000); // Retry after 1 second
            }
        }
    };

    const fetchExamTypeData = async (retryCount = 0) => {
        try {
            const response = await fetch("/api/examtype/all");
            if (!response.ok) throw new Error('Failed to fetch exam types');

            const examTypeData = await response.json();
            setExamTypeList(prev => examTypeData ? examTypeData : prev);
        } catch (error) {
            console.error('Error fetching exam types:', error);
            if (retryCount < 5) {
                setTimeout(() => fetchExamTypeData(retryCount + 1), 1000); // Retry after 1 second
            }
        }
    };

    React.useEffect(() => {
        setIsOption(scoreOption || gradeOption || stdOption ? true : false);
    }, [scoreOption, gradeOption, stdOption]);

    React.useEffect(() => {
        fetchData();
    }, [examType, year]);
    
    React.useEffect(() => {
        fetchExamTypeData();
    }, []);


    const handleExamTypeChange = (event) => {
        setExamType(event.target.value);
    }
    const handleYearChange = (event) => {
        setYear(event.target.value);
    }
    
    return (
        <>
            <div className="stats__btn-box">
                <div className="stats__btn-box__col">
                    <select className="form-control" onChange={handleExamTypeChange} defaultValue={"all"}>
                        <option value="all">All</option>
                        {examTypeList?.map(item =>
                            <option key={item.examTypeId} value={item.examTypeName}>{item.examTypeName}</option>
                        )}
                    </select>
                    <select className="form-control" onChange={handleYearChange} defaultValue={2024}>
                        {[...Array(15)].map((_, index) => (
                            <option key={2010 + index} value={2010 + index}>
                            {2010 + index}년
                        </option>
                        ))}
                    </select>
                </div>
                <div className="stats__btn-box__col">
                    <button className={cat ==="score" ? "btn btn-outline-secondary selected" : "btn btn-outline-secondary"} onClick={() => setCat("score")}>점수</button>
                    <button className={cat ==="std" ? "btn btn-outline-secondary selected" : "btn btn-outline-secondary"} onClick={() => setCat("std")}>표준점수</button>
                    <button className={cat ==="grade" ? "btn btn-outline-secondary selected" : "btn btn-outline-secondary"} onClick={() => setCat("grade")}>등급</button>
                </div>
            </div>
            
            <div className="chart-container">
                {isOption ?
                    <ChartType cat={cat} option={
                        cat === "score" ? scoreOption :
                            cat === "grade" ? gradeOption :
                                cat === "std" ? stdOption : null
                    } />
                    : null}
            </div>
        </>
    )
}

function ChartType({ cat, option }) {
    const chartRef = React.useRef(null);
    const [subjectList, setSubjectList] = React.useState([]);
    const [checkedList, setCheckedList] = React.useState([]);

    React.useEffect(() => {
        fetchSubjectData();
        const chart = new ApexCharts(chartRef.current, option);
        chart.render();
        return () => {
            chart.destroy();
        };
    }, [cat, option]);

    const fetchSubjectData = async (retryCount = 0) => {
        try{
            const response = await fetch("/api/subject/");
            if(!response.ok){
                throw new Error('Network response was not ok');
            }
            const subjectData = await response.json();
            setSubjectList(subjectData)
            setCheckedList(subjectData.map(item => true));
        } catch (error) {
            console.error('Error fetching data:', error);
            if (retryCount < 3) { // Retry up to 3 times
                setTimeout(() => fetchSubjectData(retryCount + 1), 300); // Retry after 1 second
            } else {
                // location.href = "/"                
            }
        }
    }

    const handleCheckBox = (index) => {
        const newCheckedList = [...checkedList];
        newCheckedList[index] = !newCheckedList
        setCheckedList(prev => [])
    }

    return (
        <>
        {/* <div className="col-md-4">
            {subjectList.map((subject, index) => 
                <div className="form-check" key={subject.subject}>
                    <input 
                        className="form-check-input" 
                        type="checkbox" 
                        value={subject.subject} 
                        id={subject.subjectCode} 
                        checked={checkedList[index]}
                        onChange={() => handleCheckBox(index)}
                    />
                    <label className="form-check-label" for={subject.subjectCode}>
                        {subject.subject}
                    </label>
                </div>
            )}
        </div> */}
        <div className="chart" ref={chartRef}></div>
        </>
    )
}

function createOption(series, xaxisCat, min, max, xaxisTitle, yaxisTitle, reversed) {
    console.log("series",series);
    console.log("xaxis",xaxisCat);
    return {
        series: series,
        chart: {
            height: "100%",
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
        colors: ['#77B6EA', '#545454', '#ECEE81', '#8DDFCB', '#EDB7ED', '#FF0060'],
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
                colors: ['#f3f3f3', 'transparent'],
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
            },
            labels: {
                rotate: -45
            }
        },
        yaxis: {
            title: {
                text: yaxisTitle
            },
            min: min,
            max: max,
            reversed: reversed
        },
        legend: {
            position: 'top',
            horizontalAlign: 'right',
            floating: true,
            offsetY: -25,
            offsetX: -5
        }
    };
}
