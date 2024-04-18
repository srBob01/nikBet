<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Balance Information</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 20px;
            text-align: center;
        }

        .balance-info {
            color: #D8000C;
            background-color: #FFD2D2;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            width: 50%;
            margin: 20px auto;
            font-weight: bold;
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
    </style>
</head>
<body>

<div class="balance-info">
    <p>Balance: ${requestScope.balance}</p>
</div>

<a href="<c:url value='/user/default'/>">
    <button type="button">On default page</button>
</a>

<a href="<c:url value='/user/money/deposit'/>">
    <button type="button">Deposit</button>
</a>

<a href="<c:url value='/user/money/withdraw'/>">
    <button type="button">Withdraw</button>
</a>

</body>
</html>
