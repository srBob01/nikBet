<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>User Profile</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 20px;
        }

        h2 {
            color: #333;
            text-align: center;
            margin-bottom: 20px;
        }

        div.profile {
            background-color: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            max-width: 600px;
            margin: 20px auto;
            text-align: left;
        }

        button {
            padding: 10px 20px;
            background-color: #5C7AEA;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            transition: background-color 0.3s;
            display: block;
            margin: 10px auto;
            width: 30%;
        }

        button:hover {
            background-color: #3f5bcc;
        }

        a {
            text-decoration: none;
        }

        p {
            margin: 10px 0;
        }
    </style>
</head>
<body>

<c:if test="${not empty sessionScope.user}">
    <form method="post" action="<c:url value='/logout'/>">
        <button type="submit">Logout</button>
    </form>
    <h2>User Profile</h2>
    <div class="profile">
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
<a href="<c:url value='/user/update/password'/>">
    <button type="button">Update password</button>
</a>
<a href="<c:url value='/user/money/default'/>">
    <button type="button">Look balance</button>
</a>
<a href="<c:url value='/user/matches/default'/>">
    <button type="button">Look matches</button>
</a>

</body>
</html>
