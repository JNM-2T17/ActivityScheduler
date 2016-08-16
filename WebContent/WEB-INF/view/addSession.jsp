<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="header.jsp"/>
		<h1>Add Session</h1>
		<script src="<c:url value="resources/js/addSession.js"/>"></script>
		<form action="addSession" method="POST" onsubmit="return addSession.checkSubmit();">
			<input type="hidden" name="token" value="${sessionToken }"/>
			Name: <input type="text" name="name" id="name"/><br/>
			Start and End Date: <input type="text" id="startDate" name="startDate"/> to 
			<input type="text" id="endDate" name="endDate"/><br/>
			Restricted Activity Days:
			<input type="checkbox" id="sunday" name="sunday" />
			<label for="sunday">Sunday</label>
			<input type="checkbox" id="monday" name="monday" />
			<label for="monday">Monday</label>
			<input type="checkbox" id="tuesday" name="tuesday" />
			<label for="tuesday">Tuesday</label>
			<input type="checkbox" id="wednesday" name="wednesday" />
			<label for="wednesday">Wednesday</label><br/>
			<input type="checkbox" id="thursday" name="thursday" />
			<label for="thursday">Thursday</label>
			<input type="checkbox" id="friday" name="friday" />
			<label for="friday">Friday</label>
			<input type="checkbox" id="saturday" name="saturday" />
			<label for="saturday">Saturday</label><br/>
			Specific Dates: <input type="text" id="blackDate"/> <button type="button" id="addDate">+</button>
			<div id="blackDates"></div>
			Specific Times: (Time is in the 24-hour format HHMM )<br/>
			<input type="text" id="startTime"/> to <input type="text" id="endTime"> <button type="button" id="addTime">+</button>
			<div id="blackTimes"></div>
			<input type="submit" value="Add Session"/>
		</form>
<jsp:include page="footer.jsp"/>