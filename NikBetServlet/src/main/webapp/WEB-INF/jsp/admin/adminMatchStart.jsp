<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Hot Games Listing</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            padding: 20px;
        }

        .container {
            width: 80%;
            margin: auto;
        }

        .card {
            margin-top: 20px;
            box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
            text-align: center;
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
        }

        .btn-start {
            color: white;
            background-color: #28a745;
            border-radius: 5px;
            padding: 10px 20px;
            margin: 10px;
            text-decoration: none;
            display: inline-block;
        }

        .btn-start:hover {
            background-color: #218838;
        }

        form {
            display: inline;
        }

        .fixed-button {
            position: fixed;
            top: 20px;
            z-index: 1000;
            width: 160px;
            height: 100px;
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
            font-size: 16px;
            line-height: 1.5;
        }

        .action-button:hover {
            background-color: #3f5bcc;
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
    <a href="<c:url value='/admin/match/change'/>" class="fixed-button fixed-button-right action-button">
        Change current matches</a>

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
    <h2>Hot Games to Start</h2>

    <c:forEach items="${requestScope.listGameScheduledDto}" var="game">
        <div class="card">
            <div class="card-body">
                <h5 class="card-title">${game.homeTeam()} vs ${game.guestTeam()}</h5>
                <p class="card-text">Scheduled Date: ${game.gameDate()}</p>
                <p class="card-text">Odds - Home: ${game.coefficientOnHomeTeam()}, Draw: ${game.coefficientOnDraw()},
                    Away: ${game.coefficientOnGuestTeam()}</p>
                <form action="<c:url value='/admin/match/start'/>" method="post">
                    <input type="hidden" name="idGame" value="${game.idGame()}"/>
                    <button type="submit" class="btn btn-start">Start Game</button>
                </form>
            </div>
        </div>
    </c:forEach>

    <c:if test="${empty requestScope.listGameScheduledDto}">
        <div class="alert alert-info">No hot games to start at the moment.</div>
    </c:if>
</div>
</body>
</html>
