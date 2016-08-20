function escapeHtml(text) {
  var map = {
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#039;'
  };

  return text.replace(/[&<>"']/g, function(m) { return map[m]; });
}

function showError(message) {
	alert(message);
}

function showMessage(message) {
	alert(message);
}

function appendMessage(message, add) {
	return message + (message.length == 0 ? "" : "\n") + add;
}

function checkPass(pass) {
	var message = "";
	if( pass.length < 8 ) {
		message = appendMessage(message,"Password must be at least 8 characters long.");
	}
	
	var cap = false;
	var low = false;
	var num = false;
	
	for(var i = 0; i < pass.length; i++) {
		if( /[A-Z]/.test(pass.substring(i,i + 1))) {
			cap = true;
		} else if(/[a-z]/.test(pass.substring(i,i + 1))) {
			low = true;
		} else if(/[0-9]/.test(pass.substring(i,i + 1))) {
			num = true;
		}
	}
	
	if( !cap ) {
		message = appendMessage(message,"Password must contain at least one uppercase letter.");
	}
	if( !low ) {
		message = appendMessage(message,"Password must contain at least one lowercase letter.");
	}
	if( !num ) {
		message = appendMessage(message,"Password must contain at least one number.");
	}
	
	return message.length == 0 ? true : message;
}

$(document).ready(function() {
	var error = $("#error").val() ? $("#error").val() : "";
	if( error.length > 0 ) {
		showError(error);
	}
	
	var message= $("#message").val() ? $("#message").val() : "";
	if( message.length > 0 ) {
		showError(message);
	}
});