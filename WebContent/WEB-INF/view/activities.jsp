<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="header.jsp"/>
		<div id="contentTitle">
			<h2><c:out value="${activeSession.name }"/></h2>
			<div id="sectionOptions">
				<a href="addActivity">Add Activity&nbsp;&nbsp;&nbsp;<i class="fa fa-plus"></i></a> <button id="genSched">Generate Schedule&nbsp;&nbsp;&nbsp;<i class="fa fa-calendar"></i></button>
			</div>
			<div class="clear"></div>
		</div>
		<script src="<c:url value="resources/js/activities.js"/>"></script>
		<input type="hidden" name="token" value="${sessionToken }"/>
		<c:choose>
		<c:when test="${empty activities }">
		No activities to display
		</c:when>
		<c:otherwise>
		<table id="activitiesTable">
		<thead>
			<tr>
				<th>Name</th>
				<th>Venue</th>
				<th>Length</th>
				<th>Target Groups</th>
				<th>Days</th>
				<th>Other Dates</th>
				<th>Time Range</th>
				<th>Assigned Time</th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${activities }" var="a">
			<tr id="act-${a.id }">
				<td><c:out value="${a.name }"/></td>
				<td><c:out value="${a.venue.name }"/></td>
				<td><c:out value="${a.length }"/> mins</td>
				<td>
				<c:set var="i" value="0"/>
				<c:forEach items="${a.targetGroups }" var="tg">
				<c:if test="${i > 0 && i % 3 == 0 }"></c:if>
					<c:out value="${tg.name }"/><br/>
				<c:set var="i" value="${i + 1 }"/>
				</c:forEach>
				</td>
				<td>
				<c:choose>
				<c:when test="${empty a.daysString }">
				N/A
				</c:when>
				<c:otherwise>
				<c:set var="i" value="0"/>
				<c:forEach items="${a.daysString }" var="d">
				<c:if test="${i > 0 }"><br/></c:if>
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
				<td><a href="editActivity?id=${a.id }"><i class="fa fa-edit"></i></a></td>
			</tr>
			</c:forEach>
		</tbody>
		</table>
		</c:otherwise>
		</c:choose>
		
		
		
		
		
		
		
		
		
<jsp:include page="footer.jsp"/>