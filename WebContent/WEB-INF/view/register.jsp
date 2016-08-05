<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 

<jsp:include page="header.jsp"/>
				<script src="<c:url value="resources/js/register.js"/>"></script>
				<form action="register" method="POST" onsubmit="return register.checkSubmit();">
					<h1>Register</h1>
					<table id="regForm">
						<tr>
							<td>Name</td>
							<td id="regName">
								<input type="text" name="fname" placeholder="First" id="fname"/><input type="text" name="mi" placeholder="M.I" id="mi"/><input type="text" name="lname" placeholder="Last Name" id="lname"/>
							</td>
						</tr>
						<tr>
							<td>Username</td>
							<td>
								<input type="text" name="username" id="username"/>
							</td>
						</tr>
						<tr>
							<td>Email</td>
							<td>
								<input type="text" name="email" id="email"/>
							</td>
						</tr>
						<tr>
							<td>Password</td>
							<td>
								<input type="password" name="password" id="password"/>
							</td>
						</tr>
						<tr>
							<td>Confirm</td>
							<td>
								<input type="password" name="confirmPassword" placeholder="Confirm Password" id="confirmPassword"/>
							</td>
						</tr>
					</table>
					<input type="hidden" value="${sessionToken }" name="token"/>
					<input type="submit" value="Register &gt;" id="regButton"/>
				</form>
<jsp:include page="footer.jsp"/>