<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Registration</title>
</head>
<body>

<c:if test="${not empty requestScope.errors}">
    <div style="color: red">
        <c:forEach var="error" items="${requestScope.errors}">
            <span>${error.field}: </span><span>${error.type.toString()}</span><br/>
        </c:forEach>
    </div>
</c:if>


<form method="post" action="<c:url value='/registration'/>">
    <label for="nickname">Nickname:</label><br>
    <input type="text" id="nickname" name="nickname" required><br>

    <label for="firstName">First Name:</label><br>
    <input type="text" id="firstName" name="firstName" required><br>

    <label for="lastName">Last Name:</label><br>
    <input type="text" id="lastName" name="lastName" required><br>

    <label for="patronymic">Patronymic:</label><br>
    <input type="text" id="patronymic" name="patronymic"><br>

    <label for="password">Password:</label><br>
    <input type="password" id="password" name="password" required><br>

    <label for="phoneNumber">Phone Number:</label><br>
    <input type="tel" id="phoneNumber" name="phoneNumber" required><br>

    <label for="email">Email:</label><br>
    <input type="email" id="email" name="email" required><br>

    <label for="birthDate">Birth Date:</label><br>
    <input type="date" id="birthDate" name="birthDate" required><br>

    <input type="submit" value="Register"><br>
    <a href="<c:url value='/login'/>">
        <button type="button">Login</button>
    </a>
</form>
</body>
</html>
