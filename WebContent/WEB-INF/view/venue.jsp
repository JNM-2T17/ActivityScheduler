<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 

<jsp:include page="header.jsp"/>
		<div id="popup-overlay"></div>
		<div id="popup-frame">
			Name: <input id="newName"/>
			<button id="edit">Confirm Edit</button>
			<button id="delete"><i class="fa fa-trash"></i></button>
		</div>
		<script src="<c:url value="resources/js/venue.js"/>"></script>
		<button id="addButton">Add Venue</button>
		<form id="addForm" onsubmit="return venue.checkSubmit();">
			<input type="hidden" id="token" value="<c:out value="${sessionToken }"/>"/>
			<input type="text" placeholder="Venue Name" id="name"/>
			<input type="submit" value="Confirm"/>
		</form>
		<table>
			<thead><tr><th>Venue</th><th></th></tr></thead>
			<tbody>
			<c:choose>
			<c:when test="${empty venues }">
			<tr id="empty-prompt"><td colspan="2">No Venues</td></tr>
			</c:when>
			<c:otherwise>
			<c:forEach items="${venues }" var="v">
			<tr id="venue-${v.id }">
				<td class="name"><c:out value="${v.name }"/></td>
				<td><span class="editVenue" data-id="${v.id }"><i class="fa fa-edit"></i></span></td>
			</tr>
			</c:forEach>
			</c:otherwise>
			</c:choose>
			</tbody>	
		</table>
<jsp:include page="footer.jsp"/>