<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>UpdateDescription</title>
</head>
<body>

<c:if test="${not empty requestScope.errors}">
    <div style="color: red">
        <c:forEach var="error" items="${requestScope.errors}">
            <span>${error.field}: </span><span>${error.type.toString()}</span><br/>
        </c:forEach>
    </div>
</c:if>

<form method="post" action="<c:url value='/user/update/description'/>">
    <label for="nickname">Nickname:</label><br>
    <input type="text" id="nickname" name="nickname" value="${sessionScope.user.nickname()}" required><br>

    <label for="firstName">First Name:</label><br>
    <input type="text" id="firstName" name="firstName" value="${sessionScope.user.firstName()}" required><br>

    <label for="lastName">Last Name:</label><br>
    <input type="text" id="lastName" name="lastName" value="${sessionScope.user.lastName()}" required><br>

    <label for="patronymic">Patronymic:</label><br>
    <input type="text" id="patronymic" name="patronymic" value="${sessionScope.user.patronymic()}"><br>

    <label for="phoneNumber">Phone Number:</label><br>
    <input type="tel" id="phoneNumber" name="phoneNumber" value="${sessionScope.user.phoneNumber()}" required><br>

    <label for="email">Email:</label><br>
    <input type="email" id="email" name="email" value="${sessionScope.user.email()}"  required><br>

    <label for="birthDate">Birth Date:</label><br>
    <input type="date" id="birthDate" name="birthDate" value="${sessionScope.user.birthDate()}" required><br>

    <input type="submit" value="Change"><br>

    <a href="<c:url value='/user/default'/>">
        <button type="button">On default page</button>
    </a>
</form>

</body>
</html>
