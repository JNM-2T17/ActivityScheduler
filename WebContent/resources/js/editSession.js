var editSession = (function() {
	var sessionId = 0;
	var startDate = null;
	var endDate = null;
	var blackdates = {};
	var bdid = 0;
	var blacktimes = {};
	var btid = 0;
	var dateRegex = /^(0?[1-9]|1[0-2])\/(0?[1-9]|[1-2][0-9]|3[0-1])\/2[0-9]{3}$/;
	var timeRegex = /^((0?|1)[0-9]|2[0-3])([0-5][0-9])$/;
	
	function setStartDate(sd) {
		if( dateRegex.test(sd)) {
			var parts = sd.split(/[/]/);
			startDate = new Date(parts[2],parts[0] - 1,parts[1]);
			$("#endDate").datepicker("destroy");
			$("#endDate").datepicker({minDate:startDate});
			$("#blackDate").datepicker("destroy");
			$("#blackDate").datepicker({minDate : startDate,maxDate : endDate});
		}
	}
	
	function setEndDate(ed) {
		if( dateRegex.test(ed)) {
			var parts = ed.split(/[/]/);
			endDate = new Date(parts[2],parts[0] - 1,parts[1]);
			$("#startDate").datepicker("destroy");
			$("#startDate").datepicker({maxDate:endDate});
			$("#blackDate").datepicker("destroy");
			$("#blackDate").datepicker({minDate : startDate,maxDate : endDate});
		}
	}
	
	function addBD(bd) {
		if( dateRegex.test(bd)) {
			for(x in blackDates) {
				if( blackDates[x] === bd ) {
					showError("You have already added that date.");
					return;
				}
			}
			blackdates[bdid] = bd;
			$("#blackDates").append("<div id='bd-" + bdid + "'>" + bd + "<button type='button' onclick='editSession.removeBD(" + bdid + ");'><i class='fa fa-trash'/></button></div>");
			$("#blackDate").val("");
			bdid++;
		} else {
			showError("Invalid date");
		}
	}
	
	function addBT(bts, bte) {
		if( timeRegex.test(bts) && timeRegex.test(bte) ) {
			if( bts * 1 < bte * 1 ) {
				var timeval = bts + "-" + bte;
				for(x in blacktimes) {
					if(blacktimes[x] === timeval ) {
						showError("You already added that interval.");
						return;
					}
				}
				blacktimes[btid] = timeval;
				$("#blackTimes").append("<div id='bt-" + btid + "'>" + timeval + "<button type='button' onclick='editSession.removeBT(" + btid + ");'><i class='fa fa-trash'/></button></div>");
				$("#startTime").val("");
				$("#endTime").val("");
				btid++;
			} else {
				showError("End time must be after start time.");
			}
		} else {
			showError("Invalid time format. Make sure it is in HHMM format.");
		}
	}
	
	$(document).ready(function() {
		sessionId = $("#sessionId").val();
		$("#startDate").datepicker({maxDate : endDate})
			.change(function() {
			setStartDate($(this).val());
		});
		$("#endDate").datepicker({minDate : startDate})
			.change(function() {
			setEndDate($(this).val());
		});
		$("#blackDate").datepicker({minDate : startDate,maxDate : endDate});
		$("#addDate").click(function() {
			addBD($("#blackDate").val());
		});
		$("#addTime").click(function() {
			addBT($("#startTime").val(),$("#endTime").val());
		});
		$.ajax({
			url : "getSession",
			method : "POST",
			data : {
				token : $("#token").val(),
				sessionId : sessionId
			},
			dataType : "json",
			success : function(a) {
				if( a ) {
					$("#name").val(a.name);
					$("#startDate").val(a.startDate);
					$("#endDate").val(a.endDate);
					setStartDate(a.startDate);
					setEndDate(a.endDate);
					for(x in a.blackDays) {
						$("#" + a.blackDays[x].toLowerCase()).prop('checked',true);
					}
					for( x in a.blackDates) {
						addBD(a.blackDates[x]);
					}
					for(x in a.blackTimes) {
						addBT(a.blackTimes[x].startTime,a.blackTimes[x].endTime);
					}
				} else {
					window.location = ".";
				}
			}
		});
	});
	
	return {
		checkSubmit : function() {
			$("#sessionId").val(sessionId);
			var name = $("#name").val();
			var startDate = $("#startDate").val();
			var endDate = $("#endDate").val();
			var message = "";
			if( !/^[A-Za-z0-9.,' \-]+$/.test(name)) {
				message = appendMessage(message,"Name is invalid.");
			} 
			
			if( !dateRegex.test(startDate) ) {
				message = appendMessage(message,"Start date is invalid.");
			}
			
			if( !dateRegex.test(endDate)) {
				message = appendMessage(message,"End date is invalid.");
			}
			
			if( message.length == 0 ) {
				var bt = "";
				for(x in blacktimes) {
					bt += "<input type='hidden' name='bt[]' value='" + blacktimes[x] + "'/>";
				}
				$("#blackTimes").html(bt);
				var bd = "";
				for(x in blackdates) {
					bd += "<input type='hidden' name='bd[]' value='" + blackdates[x] + "'/>";
				}
				$("#blackDates").html(bd);
				return true;
			} else {
				showError(message);
				return false;
			}
		},
		removeBD : function(id) {
			delete blackdates[id];
			$("#bd-" + id).remove();
		},
		removeBT : function(id) {
			delete blacktimes[id];
			$("#bt-" + id).remove();
		}
	};
})();