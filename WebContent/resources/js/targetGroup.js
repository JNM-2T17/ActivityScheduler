var tg = (function(){
	return {
		checkSubmit : function() {
			var token = $("#token").val();
			var name = $("#name").val();
			
			var message = "";
			
			if(!/^[A-Za-z0-9.', _\-]+$/.test(name)) {
				message = appendMessage(message,"Target Group Name is not valid.");
			}
			
			if( message.length > 0 ) {
				showError(message);
			} else {
				$.ajax({
					url : "addTG",
					method : "POST",
					data : {
						token : token,
						name : name
					},
					dataType : "json",
					success : function(a) {
						console.log(a);
						if( a != null ) {
							if( a.exit == 1 ) {
								location = ".";
							} else {
								$("#name").val("");
								$("#empty-prompt").remove();
								$("table tbody").append("<tr><td>" + a.name + "</td></tr>");
								$("#addButton").show();
								$("#addForm").hide();
								showMessage("Target group successfully added.");
							} 
						} else {
							showError("Failed to add target group.");
						}
					}
				});
			}
			return false;
		}
	};
})();

$(document).ready(function() {
	$("#addForm").hide();
	$("#addButton").click(function() {
		$("#addButton").hide();
		$("#addForm").show();
	});
})