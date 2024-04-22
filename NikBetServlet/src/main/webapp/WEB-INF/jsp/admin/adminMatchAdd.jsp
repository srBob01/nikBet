<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Add New Match</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <style>
        body {
            padding: 20px;
            font-family: Arial, sans-serif;
        }

        .container {
            width: 50%;
            margin: auto;
            background: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .form-group {
            margin-bottom: 15px;
        }

        .form-control {
            display: block;
            width: 100%;
            padding: 0.375rem 0.75rem;
            font-size: 1rem;
            line-height: 1.5;
            color: #495057;
            background-color: #fff;
            background-clip: padding-box;
            border: 1px solid #ced4da;
            border-radius: 0.25rem;
            transition: border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
        }

        .btn-primary {
            color: #fff;
            background-color: #007bff;
            border-color: #007bff;
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
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
    </style>
</head>
<body>
<div class="container">

    <a href="<c:url value='/admin/default'/>" class="fixed-button fixed-button-left action-button">Admin Home</a>
    <a href="<c:url value='/admin/match/start'/>" class="fixed-button fixed-button-right action-button">Start
        matches</a>


    <c:if test="${not empty requestScope.messageGood}">
        <div class="alert alert-success">
                ${requestScope.messageGood}
        </div>
    </c:if>

    <c:if test="${not empty requestScope.messageBad}">
        <div class="alert alert-danger">
                ${requestScope.messageBad}
        </div>
    </c:if>

    <h2>Add New Match</h2>
    <form action="<c:url value='/admin/match/add'/>" method="post">
        <div class="form-group">
            <label for="homeTeam">Home Team:</label>
            <select name="homeTeam" id="homeTeam" class="form-control">
                <c:forEach items="${requestScope.teams}" var="team">
                    <option value="${team.idTeam()}">${team.title()}</option>
                </c:forEach>
            </select>
        </div>
        <div class="form-group">
            <label for="guestTeam">Guest Team:</label>
            <select name="guestTeam" id="guestTeam" class="form-control">
                <c:forEach items="${requestScope.teams}" var="team">
                    <option value="${team.idTeam()}">${team.title()}</option>
                </c:forEach>
            </select>
        </div>
        <div class="form-group">
            <label for="matchTime">Match Time:</label>
            <input type="datetime-local" name="matchTime" id="matchTime" class="form-control">
        </div>

        <div class="form-group">
            <label for="coefficientOnHomeTeam">Coefficient on Home Team Win:</label>
            <input type="number" name="coefficientOnHomeTeam" id="coefficientOnHomeTeam" class="form-control"
                   step="0.1" required>
        </div>
        <div class="form-group">
            <label for="coefficientOnDraw">Coefficient on Draw:</label>
            <input type="number" name="coefficientOnDraw" id="coefficientOnDraw" class="form-control" step="0.1"
                   required>
        </div>
        <div class="form-group">
            <label for="coefficientOnGuestTeam">Coefficient on Guest Team Win:</label>
            <input type="number" name="coefficientOnGuestTeam" id="coefficientOnGuestTeam" class="form-control"
                   step="0.1" required>
        </div>

        <button type="submit" class="btn btn-primary">Add Match</button>
    </form>
</div>
</body>
</html>
