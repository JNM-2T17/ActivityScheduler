<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 

<jsp:include page="header.jsp"/>
		<div id="popup-overlay"></div>
		<div id="popup-frame">
			<div id="popup-title">
				<h3>Edit Target Group</h3>
				<div id="popup-options">
					<button id="delete"><i class="fa fa-trash"></i></button>
				</div>
				<div class="clear"></div>
			</div>
			<table>
				<tr>
					<td>Name</td>
					<td><input id="newName"/></td>
				</tr>
			</table>
			<button id="edit">Confirm</button>
		</div>
		<script src="<c:url value="resources/js/targetGroup.js"/>"></script>
		
		<div id="contentTitle">
			<h2>Target Groups</h2>
			<div id="sectionOptions">
				<form id="addForm" onsubmit="return tg.checkSubmit();">
					<input type="hidden" id="token" value="<c:out value="${sessionToken }"/>"/>
					<input type="text" placeholder="Target Group Name" id="name"/>
					<input type="submit" value="Confirm"/>
				</form><%-- 
				<form id="addForm" onsubmit="return tg.checkSubmit();">
					<input type="hidden" id="token" value="<c:out value="${sessionToken }"/>"/>
					<input type="text" id="targetGroupName" placeholder="Target Group Name" id="name"/>
					<input type="submit" value="Confirm"/>
				</form>  --%><button id="cancelAdd">Cancel</button>
				<button id="addButton">Add Target Group&nbsp;&nbsp;&nbsp;<i class="fa fa-plus"></i></button>
			</div>
			<div class="clear"></div>
		</div>
		
		<ul id="itemList">
			<c:choose>
			<c:when test="${empty tgs }">
			No Target Groups
			</c:when>
			<c:otherwise>
			<c:forEach items="${tgs }" var="tg">
			<li id="tg-${tg.id }">
				<span class="name"><c:out value="${tg.name }"/></span> <span class="editTG" data-id="${tg.id }"><i class="fa fa-edit"></i></span>
			</li>
			</c:forEach>
			</c:otherwise>
			</c:choose>
		</ul>
<jsp:include page="footer.jsp"/>