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
					<td class="fromToDate">
						<input type="text" id="startDate" name="startDate"/><div class="dateMiddle"> to </div><input type="text" id="endDate" name="endDate"/>
						<div class="clear"></div>
					</td>
				</tr>
				<tr>
					<td>Restricted Activity Days</td>
					<td>
						<input class="restrictedDay" type="checkbox" id="sunday" name="sunday" /><label for="sunday">Sunday<br/></label>
						<input class="restrictedDay" type="checkbox" id="monday" name="monday" /><label for="monday">Monday<br/></label>
						<input class="restrictedDay" type="checkbox" id="tuesday" name="tuesday" /><label for="tuesday">Tuesday<br/></label>
						<input class="restrictedDay" type="checkbox" id="wednesday" name="wednesday" /><label for="wednesday">Wednesday<br/></label>
						<input class="restrictedDay" type="checkbox" id="thursday" name="thursday" /><label for="thursday">Thursday<br/></label>
						<input class="restrictedDay" type="checkbox" id="friday" name="friday" /><label for="friday">Friday<br/></label>
						<input class="restrictedDay" type="checkbox" id="saturday" name="saturday" /><label for="saturday">Saturday<br/></label>
						
						<span class="restrictedSection">Specific Dates:</span>
						<ul id="blackDates"></ul>
						<input type="text" id="blackDate"/> <button type="button" id="addDate"><i class="fa fa-plus"></i></button>
						
						<span class="restrictedSection">Specific Times (In 24-hour format HHMM):</span>
						<ul id="blackTimes"></ul>
						<div id="addRestrictedDate" class="fromToDate">
							<input type="text" id="startTime"/><div class="dateMiddle"> to </div><input type="text" id="endTime">
							<div class="clear"></div>
						</div>
						<button type="button" id="addTime"><i class="fa fa-plus"></i></button>
					</td>
				</tr>
			</table>
			<input class="submitForm" type="submit" value="Submit" id="addSessionButton"/>
		</form>
<jsp:include page="footer.jsp"/>