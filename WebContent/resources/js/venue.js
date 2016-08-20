var venue = (function(){
var currId = null;
	
	function showPopup() {
		$("#popup-overlay").show();
		$("#popup-frame").show();
	}
	
	function hidePopup() {
		$("#popup-overlay").hide();
		$("#popup-frame").hide();
	}
	
	function editVenue() {
		currId = $(this).data("id");
		$("#newName").val($("#venue-" + currId + " .name").text());
		showPopup();
	}
	
	$(document).ready(function() {
		$("#addForm").hide();
		$("#addButton").click(function() {
			$("#addButton").hide();
			$("#addForm").show();
		});
		
		hidePopup();
		$(".editVenue").click(editVenue);
		
		$("#popup-overlay").click(function() {
			currId = null;
			hidePopup();
		});
		
		$("#edit").click(function() {
			var token = $("#token").val();
			var name = $("#newName").val();
			if(!/^[A-Za-z0-9.', _\-]+$/.test(name)) {
				showMessage("Venue Name is not valid.");
			} else {
				$.ajax({
					url : "editVenue",
					method : "POST",
					data : {
						token : token,
						id : currId,
						name : name
					},
					success : function(a) {
						if(a === "true") {
							$("#venue-" + currId + " .name").text(escapeHtml(name));
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
			var name = $("#venue-" + currId + " .name").text();
			$.ajax({
				url : "deleteVenue",
				method : "POST",
				data : {
					token : token,
					id : currId
				},
				success : function(a) {
					if(a === "true") {
						$("#venue-" + currId).remove();
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
				message = appendMessage(message,"Venue Name is not valid.");
			}
			
			if( message.length > 0 ) {
				showError(message);
			} else {
				$.ajax({
					url : "addVenue",
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
								$("table tbody").append("<tr id='venue-" + a.id + "'><td class='name'>" + escapeHtml(a.name) + "</td><td><span class='editVenue' data-id='" + a.id + "'><i class='fa fa-edit'></i></span></tr>");
								$("#venue-" + a.id + " .editVenue").click(editVenue);
								$("#addButton").show();
								$("#addForm").hide();
								showMessage("Venue successfully added.");
							} 
						} else {
							showError("Failed to add venue.");
						}
					}
				});
			}
			return false;
		}
	};
})();