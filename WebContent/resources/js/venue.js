var venue = (function(){
	var currId = null;
	
	function showPopup() {
		$("#popup-overlay").fadeIn(250);
		$("#popup-frame").fadeIn(250);
	}
	
	function hidePopup() {
		$("#popup-overlay").fadeOut(250);
		$("#popup-frame").fadeOut(250);
	}
	
	function editVenue() {
		currId = $(this).data("id");		
		$("#newName").val($("#venue-" + currId + " .name").text());
		showPopup();
	}
	
	$(document).ready(function() {
		$("#addForm").hide();
		$("#cancelAdd").hide();
		
		$("#addButton").click(function() {
			$("#addButton").hide();
			$("#cancelAdd").show();
			$("#addForm").show();
		});
		
		$("#cancelAdd").click(function() {
			$("#addButton").show();
			$("#cancelAdd").hide();
			$("#addForm").hide();
			$("#targetGroupName").val("");
		});
		
		$(".editVenue").click(editVenue);
		
		$("#popup-overlay").click(function() {
			currId = null;
			hidePopup();
		});
		
		$("#edit").click(function() {
			var token = $("#token").val();
			var name = $("#newName").val();
			if(!/^[A-Za-z0-9.', _\-()]+$/.test(name)) {
				showError("Venue Name is not valid.");
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
						if( $("li[id^='venue-'").length == 0 ) {
							$("#itemList").html("<li id='empty-prompt'>No Venues</li>");
						}
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
			
			if(!/^[A-Za-z0-9.', _\-()]+$/.test(name)) {
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
							if( a === false ) {
								location = ".";
							} else {
								$("#name").val("");
								$("#empty-prompt").hide();
								$("ul#itemList").append("<li id='venue-" + a.id + "'><span class='name'>" + escapeHtml(a.name) + "</span> <span class='editVenue' data-id='" + a.id + "'><i class='fa fa-edit'></i></span></li>");
								$("#venue-" + a.id + " .editVenue").click(editVenue);
								$("#addButton").show();
								$("#addForm").hide();
								$("#cancelAdd").hide();
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