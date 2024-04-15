<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Default</title>
</head>
<body>

<c:if test="${not empty sessionScope.user}">
    <form method="post" action="<c:url value='/logout'/>">
        <button type="submit">Logout</button>
    </form>
    <h2>User Profile</h2>
    <div>
        <p>ID: ${sessionScope.user.idUser()}</p>
        <p>Nickname: ${sessionScope.user.nickname()}</p>
        <p>First Name: ${sessionScope.user.firstName()}</p>
        <p>Last Name: ${sessionScope.user.lastName()}</p>
        <p>Patronymic: ${sessionScope.user.patronymic()}</p>
        <p>Phone Number: ${sessionScope.user.phoneNumber()}</p>
        <p>Email: ${sessionScope.user.email()}</p>
        <p>Birth Date: ${sessionScope.user.birthDate()}</p>
        <p>Role: ${sessionScope.user.role()}</p>
    </div>
</c:if>

<a href="<c:url value='/user/update/description'/>">
    <button type="button">Update description</button>
</a>
<br/>

<a href="<c:url value='/user/update/password'/>">
    <button type="button">Update password</button>
</a>
<br/>

<a href="<c:url value='/user/money/default'/>">
    <button type="button">Look balance</button>
</a>

</body>
</html>
