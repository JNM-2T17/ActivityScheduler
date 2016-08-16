<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="header.jsp"/>
		<h1><c:out value="${activeSession.name }"/></h1>
		<a href="addActivity">Add Activity</a>
		<a href="deleteActivity">Delete Activity</a>
		<button id="genSched">Generate Schedule</button>
		<script src="<c:url value="resources/js/activities.js"/>"></script>
		<input type="hidden" name="token" value="${sessionToken }"/>
		<c:choose>
		<c:when test="${empty activities }">
		<h2>No Activities to Display</h2>
		</c:when>
		<c:otherwise>
		<table>
			<tr>
				<th>Name</th>
				<th>Venue</th>
				<th>Length(minutes)</th>
				<th>Target Groups</th>
				<th>Days</th>
				<th>Other Dates</th>
				<th>Time Range</th>
				<th>Assigned Time</th>
				<th></th>
			</tr>
			<c:forEach items="${activities }" var="a">
			<tr>
				<td><c:out value="${a.name }"/></td>
				<td><c:out value="${a.venue.name }"/></td>
				<td><c:out value="${a.length }"/></td>
				<td>
				<table>
				<tr>
				<c:set var="i" value="0"/>
				<c:forEach items="${a.targetGroups }" var="tg">
				<c:if test="${i > 0 && i % 3 == 0 }"></tr><tr></c:if>
					<td><c:out value="${tg.name }"/></td>
				<c:set var="i" value="${i + 1 }"/>
				</c:forEach>
				</tr></table>
				</td>
				<td>
				<c:choose>
				<c:when test="${empty a.daysString }">
				N/A
				</c:when>
				<c:otherwise>
				<c:set var="i" value="0"/>
				<c:forEach items="${a.daysString }" var="d">
				<c:if test="${i > 0 }">,</c:if>
				<!-- <c:if test="${i > 0 && i % 3 == 0 }"><br/></c:if> -->
					${d }
				<c:set var="i" value="${i + 1 }"/>
				</c:forEach>
				</c:otherwise>
				</c:choose>
				</td>
				<td>
				<c:choose>
				<c:when test="${empty a.dateRange }">
				N/A
				</c:when>
				<c:otherwise>
				<c:set var="i" value="0"/>
				<c:forEach items="${a.dateRange}" var="d">
				<c:if test="${i > 0 }"><br/></c:if>
					<fmt:formatDate pattern="MM/dd/yyyy" value="${d.time }"/>
				<c:set var="i" value="${i + 1 }"/>
				</c:forEach>
				</c:otherwise>
				</c:choose>
				</td>
				<td><fmt:formatDate pattern="hh:mm aa" value="${a.startTimeRange.time }"/> - <fmt:formatDate pattern="hh:mm aa" value="${a.endTimeRange.time }"/></td>
				<td id="start-${a.id}">
				<c:choose>
				<c:when test="${empty a.startTime }">
				N/A
				</c:when>
				<c:otherwise>
				<fmt:formatDate pattern="EEEE, MMMM dd, yyyy hh:mm aa" value="${a.startTime.time }"/> - <fmt:formatDate pattern="hh:mm aa" value="${a.endTime.time }"/>
				</c:otherwise>
				</c:choose>
				</td>
				<td><a href="editActivity?actId=${a.id }"><i class="fa fa-edit"></i></a></td>
			</tr>
			</c:forEach>
		</table>
		</c:otherwise>
		</c:choose>
		
<jsp:include page="footer.jsp"/>