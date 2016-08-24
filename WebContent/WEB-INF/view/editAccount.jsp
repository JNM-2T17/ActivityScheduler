<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 

<jsp:include page="header.jsp"/>
		<div id="contentTitle">
			<h2>Edit Account</h2>
			<div class="clear"></div>
		</div>
		
		<script src="<c:url value="resources/js/editAccount.js"/>"></script>
		<form action="editAccount" method="POST" onsubmit="return editAccount.checkSubmit();">
			<table id="regForm">
				<tr>
					<td>Name</td>
					<td id="regName">
						<input type="text" value="<c:out value="${u.fName}"/>" name="fname" placeholder="First" id="fname"/><input type="text" value="<c:out value="${u.mi}"/>" name="mi" placeholder="M.I" id="mi"/><input type="text" value="<c:out value="${u.lName}"/>" name="lname" placeholder="Last Name" id="lname"/>
					</td>
				</tr>
				<tr>
					<td>Username</td>
					<td>
						<input type="text" value="<c:out value="${u.username}"/>" name="username" id="username"/>
					</td>
				</tr>
				<tr>
					<td>Email</td>
					<td>
						<input type="text" name="email" id="email" value="<c:out value="${u.email}"/>" />
					</td>
				</tr>
				<tr>
					<td>Old Password</td>
					<td>
						<input type="password" name="password" id="password"/>
					</td>
				</tr>
				<tr>
					<td>New Password</td>
					<td>
						<input type="password" name="newPassword" id="newPassword"/>
					</td>
				</tr>
				<tr>
					<td>Confirm</td>
					<td>
						<input type="password" name="confirmPassword" id="confirmPassword"/>
					</td>
				</tr>
			</table>
			<input type="hidden" value="${sessionToken }" id="token" name="token"/>
			<input class="submitForm" type="submit" value="Submit" id="regButton"/>
		</form>
<jsp:include page="footer.jsp"/>