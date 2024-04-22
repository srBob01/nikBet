<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>User Search</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 20px;
            position: relative;
        }

        .container {
            width: 80%;
            margin: auto;
            padding: 0 20px;
        }

        .action-button {
            padding: 10px 20px;
            background-color: #5C7AEA;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            transition: background-color 0.3s;
        }

        .action-button:hover {
            background-color: #3f5bcc;
        }

        .user-card {
            background-color: #fff;
            padding: 20px;
            margin-top: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .form-search {
            margin-bottom: 20px;
        }

        .label-block {
            margin-bottom: 10px;
        }

        .fixed-button {
            position: fixed;
            top: 20px;
            z-index: 1000;
            width: 160px;
            height: 50px;
        }

        .fixed-button-left {
            left: 20px;
        }

        .fixed-button-right {
            right: 20px;
        }

        .alert {
            text-align: center;
            width: 80%;
            margin: 20px auto;
            padding: 10px;
            border-radius: 5px;
            font-size: 16px;
        }

        .alert-success {
            background-color: #DFF2BF;
            color: #4F8A10;
        }

        .alert-danger {
            background-color: #FFD2D2;
            color: #D8000C;
        }

    </style>
</head>
<body>

<div class="container">

    <a href="<c:url value='/admin/default'/>" class="fixed-button fixed-button-left action-button">Admin Home</a>
    <a href="<c:url value='/admin/user/list'/>" class="fixed-button fixed-button-right action-button">List user</a>

    <c:if test="${not empty requestScope.message}">
        <div class="alert alert-success">
                ${requestScope.message}
        </div>
    </c:if>

    <c:if test="${not empty requestScope.messageNotFound}">
        <div class="alert alert-danger">
                ${requestScope.messageNotFound}
        </div>
    </c:if>

    <form class="form-search" action="<c:url value='/admin/user/find'/>" method="post">
        <div class="label-block">
            <label for="nickname">Enter Nickname:</label>
            <input type="text" id="nickname" name="nickname" required class="form-control"/>
        </div>
        <button type="submit" class="action-button">Search</button>
    </form>

    <c:if test="${not empty requestScope.userDto}">
        <div class="user-card">
            <h2>User Details</h2>
            <p>Nickname: ${requestScope.userDto.nickname()}</p>
            <p>Name: ${requestScope.userDto.firstName()} ${requestScope.userDto.lastName()}
                    ${requestScope.userDto.patronymic()}</p>
            <p>Phone: ${requestScope.userDto.phoneNumber()}</p>
            <p>Email: ${requestScope.userDto.email()}</p>
            <p>Birth Date: ${requestScope.userDto.birthDate()}</p>
            <p>Account Balance: ${requestScope.userDto.accountBalance()}</p>
            <form method="post" action="<c:url value='/admin/user/delete'/>">
                <input type="hidden" name="userId" value="${requestScope.userDto.idUser()}"/>
                <button type="submit" class="action-button">Delete</button>
            </form>
        </div>
    </c:if>
</div>

</body>
</html>
