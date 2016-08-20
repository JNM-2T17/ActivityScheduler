<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="header.jsp"/>

		<div id="contentTitle">
			<h2>Add Session</h2>
			<div class="clear"></div>
		</div>
		
		<script src="<c:url value="resources/js/addSession.js"/>"></script>
		<form action="addSession" method="POST" onsubmit="return addSession.checkSubmit();">
			<input type="hidden" name="token" value="${sessionToken }"/>
			<table id="addSessionForm">
				<tr>
					<td>Name</td>
					<td>
						<input type="text" name="name" id="name"/>
					</td>
				</tr>
				<tr>
					<td>Start and End Date</td>
					<td id="addSessionDate">
						<input type="text" id="startDate" name="startDate"/><div class="dateMiddle"> to </div><input type="text" id="endDate" name="endDate"/>
						<div class="clear"></div>
					</td>
				</tr>
				<tr>
					<td>Restricted Activity Days</td>
					<td>
						<input type="checkbox" id="sunday" name="sunday" /><label for="sunday">Sunday</label><br/>
						<input type="checkbox" id="monday" name="monday" /><label for="monday">Monday</label><br/>
						<input type="checkbox" id="tuesday" name="tuesday" /><label for="tuesday">Tuesday</label><br/>
						<input type="checkbox" id="wednesday" name="wednesday" /><label for="wednesday">Wednesday</label><br/>
						<input type="checkbox" id="thursday" name="thursday" /><label for="thursday">Thursday</label><br/>
						<input type="checkbox" id="friday" name="friday" /><label for="friday">Friday</label><br/>
						<input type="checkbox" id="saturday" name="saturday" /><label for="saturday">Saturday</label><br/>
						Specific Dates: <input type="text" id="blackDate"/> <button type="button" id="addDate">+</button>
						<div id="blackDates"></div>
						Specific Times: (In 24-hour format HHMM) <button type="button" id="addTime">+</button>
						<input type="text" id="startTime"/> to <input type="text" id="endTime">
						<div id="blackTimes"></div>
					</td>
				</tr>
			</table>
			<input class="submitForm" type="submit" value="Submit" id="addSessionButton"/>
		</form>
<jsp:include page="footer.jsp"/>