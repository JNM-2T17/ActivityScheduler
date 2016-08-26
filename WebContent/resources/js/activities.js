$(document).ready(function() {
	$("#genSched").click(function() {
		showMessage("Generating schedule. This might take a while.");
		$.ajax({
			url : "genSched",
			method : "POST",
			data : {
				token : $("#token").val()
			},
			dataType : "json",
			success : function(a) {
				if( a ) {
					if( a.length > 0 ) {
						var html = "";
						for(x in a) {
							if( a[x].startTime === "null" ) {
								$("#start-" + a[x].id).html("N/A");
							} else {
								$("#start-" + a[x].id).html(a[x].startTime + " - " + a[x].endTime);
							}
							html += "<tr id='act-" + a[x].id + "'>" + $("#act-" + a[x].id).html() + "</tr>";
						}
						$("#activitiesTable tbody").html(html);
					}
					showMessage("Schedule Generated!");
				} else {
					location = ".";
				}
			}
		});
	});
});