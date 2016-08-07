<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<html>
	<head>
		<title>Activity Scheduler</title>
		<link rel="stylesheet" href="<c:url value="resources/css/style.css"/>"/>
		<link rel="shortcut icon" href="<c:url value="resources/images/icon.png"/>"/>
		<script src="<c:url value="resources/js/jquery.min.js"/>"></script>
		<script src="<c:url value="resources/js/jquery-migrate-1.2.1.min.js"/>"></script>
		<script src="<c:url value="resources/js/script.js"/>"></script>
	</head>
	<body>
		<div class="main-container">
			<nav>
				<h1><a href=".">CSO Activity Scheduler</a></h1>
				<ul>
				<c:choose>
				<c:when test="${empty sessionUser }">
					<li id="loginOption">
						<div id="loginForm">
						<form action="login" method="POST">
						Login
						<input type="hidden" name="token" value="<c:out value="${sessionToken }"/>"/>
						<input type="text" name="username" placeholder="Username"/>
						<input type="password" name="password" placeholder="Password"/>
						<input type="submit" value="Login"/>
						</form>
						</div>
					</li>
					<li id="registerOption"><a href="register">Register</a></li>
				</c:when>
				<c:otherwise>
					<li id="accountOption">Account</li>
					<li id="logoutOption"><a href="logout">Logout <c:out value="${sessionUser.username }"/></a></li>
					<li id="targetGroupOption"><a href="targetGroup">Target Groups</a></li>
					<li id="venueOption"><a href="venue">Venues</a></li>
					<c:if test="${not empty currentSession }">
					<li id="settingsOption"><a href="settings">Settings</a></li>
					</c:if>
				</c:otherwise>
				</c:choose>
				</ul>
			</nav>
			<input type="hidden" id="error" value="<c:out value="${error}"/>"/>
			<input type="hidden" id="message" value="<c:out value="${message}"/>"/>