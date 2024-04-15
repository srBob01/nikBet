<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Title</title>
</head>
<body>

<c:if test="${not empty requestScope.error}">
    <div style="color: red">
        <span>${requestScope.error.toString()}</span>
    </div>
</c:if>

<form method="post" action="<c:url value='/user/money/deposit'/>">
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
