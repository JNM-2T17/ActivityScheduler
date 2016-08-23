<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="header.jsp"/>
		<h1>Add Activity</h1>
		<script src="<c:url value="resources/js/addActivity.js"/>"></script>
		<input type="hidden" id="startDate" value="${startDate }"/>
		<input type="hidden" id="endDate" value="${endDate }"/>
		<c:forEach items="${blackdays }" var="bd">
		<input type="hidden" class="blackDays" value="${bd }"/>
		</c:forEach>
		<form action="addActivity" method="POST" onsubmit="return addActivity.checkSubmit();">
			<input type="hidden" name="token" value="${sessionToken }"/>
			Name: <input type="text" name="name" id="name"/><br/>
			Venue: <select id="venue" name="venue">
			<c:forEach items="${venues }" var="v">
			<option value="${v.id }"><c:out value="${v.name }"/></option>
			</c:forEach>
			</select><br/>
			Length (in minutes): <input type="number" name="length" id="length"/><br/>
			Possible Activity Days:
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
			Target Groups: <button type="button" onclick="$('.targetGroup').prop('checked',true);">Select All</button>
			<table>
			<tr>
			<c:set var="i" value="0"/>
			<c:forEach items="${targetGroups }" var="tg">
			<c:if test="${i > 0 && i % 4 == 0 }"></tr><tr></c:if>
			<td><input type="checkbox" id="tg-${tg.id }" class="targetGroup"><c:out value="${tg.name }"/></input></td>
			<c:set var="i" value="${i + 1 }"/>
			</c:forEach>
			</tr>
			</table>
			Time Range: (Time is in the 24-hour format HHMM )<br/>
			<input type="text" name="startTime" id="startTime"/> to <input type="text" name="endTime" id="endTime"><br/>
			Specific Dates: <input type="text" id="dateRange"/> <button type="button" id="addDate">+</button>
			<div id="dates"></div>
			<div id="targets"></div>
			<input type="submit" value="Add Activity"/>
		</form>
<jsp:include page="footer.jsp"/>