<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Withdraw Funds</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 20px;
            text-align: center;
        }

        .error-message {
            color: #D8000C;
            background-color: #FFD2D2;
            padding: 10px;
            border-radius: 5px;
            margin: 20px auto;
            width: 50%;
            font-weight: bold;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        form {
            background-color: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            width: 50%;
            margin: 20px auto;
        }

        label {
            display: block;
            margin-bottom: 10px;
        }

        input[type="number"] {
            padding: 10px;
            width: 95%;
            margin-bottom: 20px;
        }

        button {
            padding: 10px 20px;
            background-color: #5C7AEA;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            transition: background-color 0.3s;
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

<c:if test="${not empty requestScope.error}">
    <div class="error-message">
        <span>${requestScope.error}</span>
    </div>
</c:if>

<form method="post" action="<c:url value='/user/money/withdraw'/>">
    <div>
        <label for="amount">Input summa for withdraw:</label>
        <input type="number" id="amount" name="amount" required min="1" step="0.5" placeholder="Summa">
    </div>
    <div>
        <button type="submit">Withdraw</button>
    </div>
</form>

<a href="<c:url value='/user/money/default'/>">
    <button type="button">Look balance</button>
</a>

</body>
</html>
