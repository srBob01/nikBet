<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Update Password</title>
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
            margin-bottom: 5px;
        }

        input[type="password"] {
            padding: 8px;
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
            display: inline-block;
            width: auto;
            margin: 10px 5px;
        }

        button:hover {
            background-color: #3f5bcc;
        }

        a {
            text-decoration: none;
            display: inline-block; /* Make sure the anchor behaves like a button */
        }
    </style>
</head>
<body>

<c:if test="${not empty requestScope.error}">
    <div class="error-message">
        <span>${requestScope.error.toString()}</span>
    </div>
</c:if>

<form method="post" action="<c:url value='/user/update/password'/>">
    <div>
        <label for="oldPassword">Old password:</label>
        <input type="password" id="oldPassword" name="oldPassword" required>
    </div>
    <div>
        <label for="newPassword">New password:</label>
        <input type="password" id="newPassword" name="newPassword" required>
    </div>
    <div>
        <button type="submit">Update</button>
        <a href="<c:url value='/user/default'/>">
            <button type="button">On default page</button>
        </a>
    </div>
</form>

</body>
</html>
