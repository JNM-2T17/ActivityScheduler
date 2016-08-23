<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="header.jsp"/>
		<div id="contentTitle">
			<h2>Add Activity</h2>
			<div class="clear"></div>
		</div>
		
		<script src="<c:url value="resources/js/addActivity.js"/>"></script>
		<input type="hidden" id="startDate" value="${startDate }"/>
		<input type="hidden" id="endDate" value="${endDate }"/>
		<c:forEach items="${blackdays }" var="bd">
		<input type="hidden" class="blackDays" value="${bd }"/>
		</c:forEach>
		<form action="addActivity" method="POST" onsubmit="return addActivity.checkSubmit();">
			<table id="addActivityForm">
				<tr>
					<td>Name</td>
					<td>
						<input type="text" name="name" id="name"/>
					</td>
				</tr>
				<tr>
					<td>Venue</td>
					<td>
						<select id="venue" name="venue">
							<c:forEach items="${venues }" var="v">
							<option value="${v.id }"><c:out value="${v.name }"/></option>
							</c:forEach>
						</select>
					</td>
				</tr>
				<tr>
					<td>Length (in minutes)</td>
					<td>
						<input type="number" min="1" step="1" name="length" id="length"/>
					</td>
				</tr>
				<tr>
					<td>Possible Activity Days</td>
					<td>
						<input class="possibleCheck" type="checkbox" id="sunday" name="sunday" /><label for="sunday">Sunday<br/></label>
						<input class="possibleCheck" type="checkbox" id="monday" name="monday" /><label for="monday">Monday<br/></label>
						<input class="possibleCheck" type="checkbox" id="tuesday" name="tuesday" /><label for="tuesday">Tuesday<br/></label>
						<input class="possibleCheck" type="checkbox" id="wednesday" name="wednesday" /><label for="wednesday">Wednesday<br/></label>
						<input class="possibleCheck" type="checkbox" id="thursday" name="thursday" /><label for="thursday">Thursday<br/></label>
						<input class="possibleCheck" type="checkbox" id="friday" name="friday" /><label for="friday">Friday<br/></label>
						<input class="possibleCheck" type="checkbox" id="saturday" name="saturday" /><label for="saturday">Saturday<br/></label>
						
						<span class="possibleSection">Specific Dates:</span>
						<ul id="dates"></ul>
						<input type="text" id="dateRange"/> <button type="button" id="addDate"><i class="fa fa-plus"></i></button>
						<div class="clear"></div>
					</td>
				</tr>
				<tr>
					<td>Target Groups</td>
					<td>
						<button type="button" onclick="$('.targetGroup').prop('checked',true);">Select All</button>
						<c:set var="i" value="0"/>
						<c:forEach items="${targetGroups }" var="tg">
						<br/><input class="possibleCheck" type="checkbox" id="tg-${tg.id }" class="targetGroup"><c:out value="${tg.name }"/></input>
						<c:set var="i" value="${i + 1 }"/>
						</c:forEach>
					</td>
				</tr>
				<tr>
					<td>Time Range (In 24-hour format HHMM ):</td>
					<td class="fromToDate">
						<input type="text" name="startTime" id="startTime"/><div class="dateMiddle"> to </div><input type="text" name="endTime" id="endTime">
						<div class="clear"></div>
					</td>
				</tr>
			</table>
			<input type="hidden" name="token" value="${sessionToken }"/>
			<input class="submitForm" type="submit" value="Submit" id="addActivityButton"/>
		</form>
<jsp:include page="footer.jsp"/>