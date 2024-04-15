<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Title</title>
</head>
<body>

<div style="color: red">
    <p>Balance: ${requestScope.balance}</p>
</div>

<a href="<c:url value='/user/default'/>">
    <button type="button">On default page</button>
</a>
<br/>

<a href="<c:url value='/user/money/deposit'/>">
    <button type="button">Deposit</button>
</a>
<br/>

<a href="<c:url value='/user/money/withdraw'/>">
    <button type="button">Withdraw</button>
</a>

</body>
</html>
