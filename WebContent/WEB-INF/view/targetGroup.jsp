<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 

<jsp:include page="header.jsp"/>
		<script src="<c:url value="resources/js/targetGroup.js"/>"></script>
		<button id="addButton">Add Target Group</button>
		<form id="addForm" onsubmit="return tg.checkSubmit();">
			<input type="hidden" id="token" value="<c:out value="${sessionToken }"/>"/>
			<input type="text" placeholder="Target Group Name" id="name"/>
			<input type="submit" value="Confirm"/>
		</form>
		<table>
			<thead><tr><th>Target Groups</th></tr></thead>
			<tbody>
			<c:choose>
			<c:when test="${empty tgs }">
			<tr id="empty-prompt"><td>No Target Groups</td></tr>
			</c:when>
			<c:otherwise>
			<c:forEach items="${tgs }" var="tg">
			<tr><td><c:out value="${tg.name }"/></td></tr>
			</c:forEach>
			</c:otherwise>
			</c:choose>
			</tbody>	
		</table>
<jsp:include page="footer.jsp"/>