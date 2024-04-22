<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Manage In-Progress Matches</title>
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
        }

        .card {
            margin-top: 20px;
            background-color: #fff;
            padding: 20px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            border-radius: 8px;
        }

        .card-header {
            background-color: #007bff;
            color: white;
            padding: 10px;
            border-radius: 5px;
        }

        .form-group {
            margin-top: 10px;
        }

        .btn {
            width: auto;
            margin-top: 10px;
        }

        .form-control {
            display: inline-block;
            width: auto;
            margin-right: 10px;
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

    <a href="<c:url value='/admin/default'/>" class="fixed-button fixed-button-left action-button">Admin Home</a>
    <a href="<c:url value='/admin/match/start'/>" class="fixed-button fixed-button-right action-button">
        Start hot match</a>

    <c:if test="${not empty requestScope.listGameInProgressDto}">
        <h2>Manage In-Progress Matches</h2>

        <c:forEach items="${requestScope.listGameInProgressDto}" var="game">
            <div class="card">
                <div class="card-header">
                        ${game.homeTeam()} vs ${game.guestTeam()} - ${game.time()}
                </div>
                <form method="post" action="<c:url value='/admin/match/change'/>">
                    <input type="hidden" name="idGame" value="${game.idGame()}"/>
                    <input type="hidden" name="action" value="updateMatch"/>
                    <div class="form-group">
                        <label>Home Goals:</label>
                        <label>
                            <input type="number" name="goalHomeTeam" class="form-control" required min="0" step="1"
                                   value="${game.score().split('-')[0].trim()}"/>
                        </label>
                        <label>Guest Goals:</label>
                        <label>
                            <input type="number" name="goalGuestTeam" class="form-control" required min="0" step="1"
                                   value="${game.score().split('-')[1].trim()}"/>
                        </label>
                    </div>
                    <div class="form-group">
                        <p>
                            <label>Home Team Coefficient (${game.coefficientOnHomeTeam()}):</label>
                            <label>
                                <input type="number" name="coefficientOnHomeTeam" class="form-control" required min="1"
                                       step="0.1"/>
                            </label>
                        </p>
                        <p>
                            <label>Draw Coefficient (${game.coefficientOnDraw()}):</label>
                            <label>
                                <input type="number" name="coefficientOnDraw" class="form-control" required min="1"
                                       step="0.1"/>
                            </label>
                        </p>
                        <p>
                            <label>Guest Team Coefficient (${game.coefficientOnGuestTeam()}):</label>
                            <label>
                                <input type="number" name="coefficientOnGuestTeam" class="form-control" required min="1"
                                       step="0.1"/>
                            </label>
                        </p>
                    </div>
                    <button type="submit" class="btn btn-primary">Update Match</button>
                </form>
                <form method="post" action="<c:url value='/admin/match/change'/>" class="form-group">
                    <input type="hidden" name="idGame" value="${game.idGame()}"/>
                    <input type="hidden" name="score" value="${game.score()}"/>
                    <c:choose>
                        <c:when test="${game.time() == 'Time1'}">
                            <button type="submit" name="action" value="startSecondTime" class="btn btn-warning">
                                Start Second Half
                            </button>
                        </c:when>

                        <c:when test="${game.time() == 'Time2'}">
                            <button type="submit" name="action" value="finishMatch" class="btn btn-danger">
                                Finish Match
                            </button>
                        </c:when>
                    </c:choose>
                </form>
            </div>
        </c:forEach>
    </c:if>

    <c:if test="${empty requestScope.listGameInProgressDto}">
        <h2>
            There are no current matches
        </h2>
    </c:if>

</div>

</body>
</html>
