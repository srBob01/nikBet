<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>UpdatePassword</title>
</head>
<body>

<c:if test="${not empty requestScope.error}">
    <div style="color: red">
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
    </div>

    <a href="<c:url value='/user/default'/>">
        <button type="button">On default page</button>
    </a>
</form>

</body>
</html>
