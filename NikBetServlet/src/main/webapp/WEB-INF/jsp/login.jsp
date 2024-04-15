<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Login</title>
</head>
<body>

<c:if test="${not empty requestScope.error}">
    <div style="color: red">
        <span>${requestScope.error.toString()}</span>
    </div>
</c:if>

<form method="post" action="<c:url value='/login'/>">

    <label for="email">Email:</label><br>
    <input type="email" id="email" name="email" required><br>

    <label for="password">Password:</label><br>
    <input type="password" id="password" name="password" required><br>

    <input type="submit" value="Login"><br>

    <a href="<c:url value='/registration'/>">
        <button type="button">Register</button>
    </a>
</form>
</body>
</html>
