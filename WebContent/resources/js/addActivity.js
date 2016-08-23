var addActivity = (function() {
	var dateRange = {};
	var did = 0;
	var dateRegex = /^(0?[1-9]|1[0-2])\/(0?[1-9]|[1-2][0-9]|3[0-1])\/2[0-9]{3}$/;
	var timeRegex = /^((0?|1)[0-9]|2[0-3])([0-5][0-9])$/;
	var startDate = null;
	var endDate = null;
	
	function setStartDate(sd) {
		if( dateRegex.test(sd)) {
			var parts = sd.split(/[/]/);
			startDate = new Date(parts[2],parts[0] - 1,parts[1]);
			$("#dateRange").datepicker("destroy");
			$("#dateRange").datepicker({minDate : startDate,maxDate : endDate});
		}
	}
	
	function setEndDate(ed) {
		if( dateRegex.test(ed)) {
			var parts = ed.split(/[/]/);
			endDate = new Date(parts[2],parts[0] - 1,parts[1]);
			$("#dateRange").datepicker("destroy");
			$("#dateRange").datepicker({minDate : startDate,maxDate : endDate});
		}
	}
	
	$(document).ready(function() {
		setStartDate($("#startDate").val());
		setEndDate($("#endDate").val());
		$(".blackDays").each(function() {
			$("#" + $(this).val().toLowerCase()).remove();
			$("label[for='" + $(this).val().toLowerCase() + "']").remove();
		});
		$("#addDate").click(function() {
			var bd = $("#dateRange").val();
			if( dateRegex.test(bd)) {
				for(x in dateRange) {
					if( dateRange[x] === bd ) {
						showError("You have already added that date.");
						return;
					}
				}
				dateRange[did] = bd;
				$("#dates").append("<li id='d-" + did + "'>" + bd + "<button type='button' onclick='addActivity.removeDate(" + did + ");'><i class='fa fa-trash'/></button></li>");
				$("#dateRange").val("");
				did++;
			} else {
				showError("Invalid date");
			}
		});
	});
	
	return {
		checkSubmit : function() {
			var name = $("#name").val();
			var length = $("#length").val();
			var startTimeRange = $("#startTime").val();
			var endTimeRange = $("#endTime").val();
			var message = "";
			if( !/^[A-Za-z0-9.,' \-]+$/.test(name)) {
				message = appendMessage(message,"Name is invalid.");
			} 
			
			if( !/^[1-9][0-9]*$/.test(length)) {
				message = appendMessage(message,"Length is invalid.");
			} 
			
			var ok = true;
			
			if( !timeRegex.test(startTimeRange) ){
				message = appendMessage(message,"Start time is invalid.");
				ok = false;
			} 
			
			if( !timeRegex.test(endTimeRange) ){
				message = appendMessage(message,"End time is invalid.");
				ok = false;
			} 
			
			if( ok && startTimeRange > endTimeRange ) {
				message = appendMessage(message,"Start Time must be before End Time.");
			}
			
			if( message.length == 0 ) {
				var tg = "";
				$(".targetGroup:checked").each(function() {
					tg += "<input type='hidden' name='tg[]' value='" + $(this).attr('id').substring(3) + "'/>";
				});
				if( tg.length > 0 ) {
					var d = "";
					for(x in dateRange) {
						d += "<input type='hidden' name='dr[]' value='" + dateRange[x] + "'/>";
					}
					$("#dates").html(d);
					
					$("#targets").html(tg);
					return true;
				} else {
					showError("Please choose at least one target group.");
					return false;
				}
			} else {
				showError(message);
				return false;
			}
		},
		removeDate : function(id) {
			delete dateRange[id];
			$("#d-" + id).remove();
		}
	};
})();