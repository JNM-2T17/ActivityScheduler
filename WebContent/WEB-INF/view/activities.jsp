<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="header.jsp"/>
		<h1><c:out value="${activeSession.name }"/></h1>
		<a href="addActivity">Add Activity</a>
		<a href="deleteActivity">Delete Activity</a>
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
				<c:choose>
				<c:when test="${empty a.daysString }">
				N/A
				</c:when>
				<c:otherwise>
				<c:forEach items="${a.daysString }" var="d">
					${d }<br/>
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
				<c:forEach items="${a.dateRange}" var="d">
					<fmt:formatDate pattern="MM/dd/yyyy" value="${d.time }"/><br/>
				</c:forEach>
				</c:otherwise>
				</c:choose>
				</td>
				<td><fmt:formatDate pattern="hh:mm aa" value="${a.startTimeRange.time }"/> - <fmt:formatDate pattern="hh:mm aa" value="${a.endTimeRange.time }"/></td>
				<td>
				<c:choose>
				<c:when test="${empty a.startTime }">
				N/A
				</c:when>
				<c:otherwise>
				<fmt:formatDate pattern="MM/dd/yyyy hh:mm aa" value="${a.startTime.time }"/> - <fmt:formatDate pattern="MM/dd/yyyy hh:mm aa" value="${a.endTime.time }"/>
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