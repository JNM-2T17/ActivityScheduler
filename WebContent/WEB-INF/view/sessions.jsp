<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="header.jsp"/>
		<h1>Sessions</h1>
		<a href="addSession">Add Session</a>
		<a href="deleteSession">Delete Sessions</a>
		<c:choose>
		<c:when test="${empty sessions }">
		<h2>No Sessions to Display</h2>
		</c:when>
		<c:otherwise>
		<table>
			<tr>
				<th>Name</th>
				<th>Blackout Days</th>
				<th>Blackout Dates</th>
				<th>Blackout Times</th>
				<th>Start Date</th>
				<th>End Date</th>
				<th></th>
			</tr>
			<c:forEach items="${sessions }" var="s">
			<tr>
				<td><c:out value="${s.name }"/></td>
				<td>
				<c:choose>
				<c:when test="${empty s.blackDaysString }">
				N/A
				</c:when>
				<c:otherwise>
				<c:forEach items="${s.blackDaysString }" var="d">
					${d }<br/>
				</c:forEach>
				</c:otherwise>
				</c:choose>
				</td>
				<td>
				<c:choose>
				<c:when test="${empty s.blackdates }">
				N/A
				</c:when>
				<c:otherwise>
				<c:forEach items="${s.blackdates }" var="d">
					<fmt:formatDate pattern="MM/dd/yyyy" value="${d.time }"/><br/>
				</c:forEach>
				</c:otherwise>
				</c:choose>
				</td>
				<td>
				<c:choose>
				<c:when test="${empty s.blacktimes }">
				N/A
				</c:when>
				<c:otherwise>
				<c:forEach items="${s.blacktimes }" var="d">
					<fmt:formatDate pattern="hh:mm aa" value="${d.startTime.time }"/> - <fmt:formatDate pattern="hh:mm aa" value="${d.endTime.time }"/><br/>
				</c:forEach>
				</c:otherwise>
				</c:choose>
				</td>
				<td>
					<fmt:formatDate pattern="MM/dd/yyyy" value="${s.startDate.time }"/>
				</td>
				<td>
					<fmt:formatDate pattern="MM/dd/yyyy" value="${s.endDate.time }"/>
				</td>
				<td><a href="editSession?sessionId=${s.id }"><i class="fa fa-edit"></i></a></td>
			</tr>
			</c:forEach>
		</table>
		</c:otherwise>
		</c:choose>
		
<jsp:include page="footer.jsp"/>