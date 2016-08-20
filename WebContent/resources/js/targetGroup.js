var tg = (function(){
	var currId = null;
	
	function showPopup() {
		$("#popup-overlay").show();
		$("#popup-frame").show();
	}
	
	function hidePopup() {
		$("#popup-overlay").hide();
		$("#popup-frame").hide();
	}
	
	function editTG() {
		currId = $(this).data("id");
		$("#newName").val($("#tg-" + currId + " .name").text());
		showPopup();
	}
	
	$(document).ready(function() {
		$("#addForm").hide();
		$("#addButton").click(function() {
			$("#addButton").hide();
			$("#addForm").show();
		});
		
		hidePopup();
		$(".editTG").click(editTG);
		
		$("#popup-overlay").click(function() {
			currId = null;
			hidePopup();
		});
		
		$("#edit").click(function() {
			var token = $("#token").val();
			var name = $("#newName").val();
			if(!/^[A-Za-z0-9.', _\-]+$/.test(name)) {
				showMessage("Target Group Name is not valid.");
			} else {
				$.ajax({
					url : "editTG",
					method : "POST",
					data : {
						token : token,
						id : currId,
						name : name
					},
					success : function(a) {
						if(a === "true") {
							$("#tg-" + currId + " .name").text(escapeHtml(name));
							currId = null;
							hidePopup();
							showMessage("Edit successful")
						} else if(a === "false")  {
							window.location = ".";
						} else {
							showError(a);
						}
					}
				});
			}
		});
		
		$("#delete").click(function() {
			var token = $("#token").val();
			var name = $("#tg-" + currId + " .name").text();
			console.log("Deleting " + currId);
			$.ajax({
				url : "deleteTG",
				method : "POST",
				data : {
					token : token,
					id : currId
				},
				success : function(a) {
					if(a === "true") {
						$("#tg-" + currId).remove();
						currId = null;
						hidePopup();
						showMessage(name + " has been deleted.");
					} else if(a === "false")  {
						window.location = ".";
					} else {
						showError(a);
					}
				}
			});
		});
	});
	
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
							if( a === false ) {
								location = ".";
							} else {
								$("#name").val("");
								$("#empty-prompt").remove();
								$("table tbody").append("<tr id='tg-" + a.id + "'><td class='name'>" + escapeHtml(a.name) + 
															"</td><td><span class='editTG' data-id='" + a.id + "'><i class='fa fa-edit'></i></span></td></tr>");
								$("#tg-" + a.id + " .editTG").click(editTG);
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