<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 

<jsp:include page="header.jsp"/>
		<script src="<c:url value="resources/js/venue.js"/>"></script>
		<button id="addButton">Add Venue</button>
		<form id="addForm" onsubmit="return venue.checkSubmit();">
			<input type="hidden" id="token" value="<c:out value="${sessionToken }"/>"/>
			<input type="text" placeholder="Venue Name" id="name"/>
			<input type="submit" value="Confirm"/>
		</form>
		<table>
			<thead><tr><th>Venue</th></tr></thead>
			<tbody>
			<c:choose>
			<c:when test="${empty venues }">
			<tr id="empty-prompt"><td>No Venues</td></tr>
			</c:when>
			<c:otherwise>
			<c:forEach items="${venues }" var="v">
			<tr><td><c:out value="${v.name }"/></td></tr>
			</c:forEach>
			</c:otherwise>
			</c:choose>
			</tbody>	
		</table>
<jsp:include page="footer.jsp"/>