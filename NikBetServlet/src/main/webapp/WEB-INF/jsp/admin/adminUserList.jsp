<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin User Listing</title>
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
            font-size: 16px;
            line-height: 1.5;
        }

        .action-button:hover {
            background-color: #3f5bcc;
        }

        table {
            width: 100%;
            margin-top: 20px;
            border-collapse: collapse;
        }

        th, td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
        }

        th {
            background-color: #f2f2f2;
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
    <a href="<c:url value='/admin/user/find'/>" class="fixed-button fixed-button-right action-button">Find user</a>

    <c:if test="${not empty requestScope.message}">
        <div class="${requestScope.message.startsWith('error') ? 'alert alert-danger' : 'alert alert-success'}">
                ${requestScope.message}
        </div>
    </c:if>

    <h2>User Listing for Admin</h2>
    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>Nickname</th>
            <th>Full Name</th>
            <th>Phone</th>
            <th>Email</th>
            <th>Birth Date</th>
            <th>Account Balance</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${requestScope.userForAdminViewDtoList}" var="user">
            <tr>
                <td>${user.idUser()}</td>
                <td>${user.nickname()}</td>
                <td>${user.firstName()} ${user.lastName()} ${user.patronymic()}</td>
                <td>${user.phoneNumber()}</td>
                <td>${user.email()}</td>
                <td>${user.birthDate()}</td>
                <td>${user.accountBalance()}</td>
                <td>
                    <form method="post" action="<c:url value='/admin/user/list'/>">
                        <input type="hidden" name="userId" value="${user.idUser()}"/>
                        <button type="submit" class="action-button">Delete</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

</body>
</html>
