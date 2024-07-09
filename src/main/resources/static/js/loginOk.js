document.addEventListener('DOMContentLoaded', function() {
  var calendarEl = document.getElementById('calendar');

  var calendar = new FullCalendar.Calendar(calendarEl, {
    timeZone: 'UTC',
    initialView: 'dayGridMonth',
    editable: true,
    selectable: true,
    eventContent: function(arg) {
	    if (arg.event.title === "●") {
	      return {
      html: '<img src="/img/schedule/check3.png" alt="Check" style="width: 20px; height: 20px;  margin: 0 auto; display: block;">'
			};
	    }
	  },
    events: function(info, successCallback, failureCallback) {
      var currentDate = new Date(); // 현재 날짜 가져오기
      var year = currentDate.getFullYear(); // 현재 연도
      var month = currentDate.getMonth() + 1; // 현재 월 (0부터 시작하므로 1을 더해줌)
       	  
      $.ajax({
        url: "/login/getMonthlySchedule",
        type: "GET",
        dataType: "json",
        data: {
		    year: year,
		    month: month,
		},
        success: function(data) {
          var events = data.map(function(dto) {
            return {
              title: dto.scheduleId != null ? "●" : "",
              start: dto.startTime
            };
          });
          successCallback(events);
        },
        error: function(xhr, status, error) {
          failureCallback(error);
        }
      });
    }
  });

  calendar.render();
});