<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Search Matches</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 20px;
            position: relative;
        }

        h2 {
            color: #333;
            text-align: center;
            margin-bottom: 20px;
        }

        form {
            background-color: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            width: 50%;
            margin: 20px auto;
            text-align: left;
        }

        label {
            display: block;
            margin-bottom: 10px;
        }

        select, button {
            width: 100%;
            padding: 8px;
            margin-bottom: 20px;
        }

        button {
            background-color: #5C7AEA;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            transition: background-color 0.3s;
            display: block;
            text-align: center;
        }

        button:hover {
            background-color: #3f5bcc;
        }

        .game-list {
            display: flex;
            flex-wrap: wrap;
            justify-content: center;
            margin-bottom: 30px;
        }

        .game-item {
            background-color: #fff;
            margin: 5px;
            padding: 20px;
            width: calc(50% - 10px);
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            border-radius: 8px;
            text-align: center;
        }

        .default-page-link {
            background-color: #5C7AEA;
            color: white;
            padding: 10px 20px;
            border-radius: 5px;
            text-decoration: none;
            display: block;
            text-align: center;
            margin: 20px auto;
            width: 200px;
            transition: background-color 0.3s;
        }

        .default-page-link:hover {
            background-color: #3f5bcc;
        }

        .message-error, .message-success {
            width: 40%;
            margin: 10px auto;
            padding: 10px;
            border-radius: 5px;
            font-weight: bold;
            text-align: center;
        }

        .message-error {
            color: #D8000C;
            background-color: #FFD2D2;
        }

        .message-success {
            color: #4F8A10;
            background-color: #DFF2BF;
        }

    </style>
</head>
<body>

<c:if test="${not empty requestScope.error}">
<div class="message-error">
    <span>${requestScope.error}</span>
</div>
</c:if>

<c:if test="${not empty requestScope.predictionResultViewDto}">
<div class="message-success">
    <p>You bet on the match: ${requestScope.predictionResultViewDto.homeTeam()} vs
            ${requestScope.predictionResultViewDto.guestTeam()}
    </p>
    <p>Expected result: ${requestScope.predictionResultViewDto.winner()}</p>
    <p>Expected gain: ${requestScope.predictionResultViewDto.possibleWin()}</p>
</div>
</c:if>

<a href="<c:url value='/user/matches/default'/>" class="default-page-link">On default matches page</a>

<h2>Search for Matches</h2>
<form action="<c:url value='/user/matches/find'/>" method="post">
    <div>
        <label for="homeTeam">Home Team:</label>
        <select name="homeTeam" id="homeTeam">
            <option value="none">None team</option>
            <c:forEach items="${requestScope.teams}" var="team">
                <option value="${team.idTeam()}">${team.title()}</option>
            </c:forEach>
        </select>
    </div>

    <div>
        <label for="guestTeam">Guest Team:</label>
        <select name="guestTeam" id="guestTeam">
            <option value="none">None team</option>
            <c:forEach items="${requestScope.teams}" var="team">
                <option value="${team.idTeam()}">${team.title()}</option>
            </c:forEach>
        </select>
    </div>

    <div>
        <label for="status">Match Status:</label>
        <select name="status" id="status">
            <option value="none">None Status</option>
            <c:forEach var="status" items="${requestScope.gameStatuses}">
                <option value="${status}">${status}</option>
            </c:forEach>
        </select>
    </div>

    <div>
        <label for="result">Match Result:</label>
        <select name="result" id="result">
            <option value="none">None Result</option>
            <c:forEach var="result" items="${requestScope.gameResults}">
                <option value="${result}">${result}</option>
            </c:forEach>
        </select>
    </div>

    <button type="submit">Find Matches</button>
</form>


<c:if test="${empty requestScope.listGameInProgressDto && empty requestScope.listGameScheduledDto && empty requestScope.listGameCompletedDto}">
    <h2>Games is empty</h2>
</c:if>

<c:if test="${not empty requestScope.listGameInProgressDto}">

<h2>Games In Progress</h2>

<div class="game-list">
    <c:forEach items="${requestScope.listGameInProgressDto}" var="game">
        <div class="game-item">
            <p>${game.homeTeam()} vs ${game.guestTeam()}</p>
            <p>Score: ${game.score()}</p>
            <p>Time: ${game.time()}</p>
            <form class="betting-form" method="post" action="<c:url value='/user/matches/find/bet'/>">
                <input type="hidden" name="action" value="placeBetInProgress"/>
                <input type="hidden" name="gameId" value="${game.idGame()}"/>
                <p>
                    <label><input type="radio" name="betType" value="HomeWin" checked> ${game.homeTeam()}
                        - ${game.coefficientOnHomeTeam()}</label>
                </p>
                <p>
                    <label><input type="radio" name="betType" value="Draw"> Draw - ${game.coefficientOnDraw()}</label>
                </p>
                <p>
                    <label><input type="radio" name="betType" value="AwayWin"> ${game.guestTeam()}
                        - ${game.coefficientOnGuestTeam()}</label>
                </p>
                <p>
                    <label>
                        <input type="number" name="betAmount" placeholder="Enter bet amount" required min="1"/>
                    </label>
                </p>
                <button type="submit" name="submit" value="betInProgress">Place Bet</button>
            </form>
        </div>
    </c:forEach>
</div>

</c:if>


<c:if test="${not empty requestScope.listGameScheduledDto}">


<h2>Scheduled Games</h2>

<div class="game-list">
    <c:forEach items="${requestScope.listGameScheduledDto}" var="game">
        <div class="game-item">
            <p>${game.homeTeam()} vs ${game.guestTeam()}</p>
            <p>Game Date: ${game.gameDate()}</p>
            <form class="betting-form" method="post" action="<c:url value='/user/matches/find/bet'/>">
                <input type="hidden" name="action" value="placeBetInProgress"/>
                <input type="hidden" name="gameId" value="${game.idGame()}"/>>
                <p>
                    <label><input type="radio" name="betType" value="HomeWin" checked> ${game.homeTeam()}
                        - ${game.coefficientOnHomeTeam()}</label>
                </p>
                <p>
                    <label><input type="radio" name="betType" value="Draw"> Draw - ${game.coefficientOnDraw()}</label>
                </p>
                <p>
                    <label><input type="radio" name="betType" value="AwayWin"> ${game.guestTeam()}
                        - ${game.coefficientOnGuestTeam()}</label>
                </p>
                <p>
                    <label>
                        <input type="number" name="betAmount" placeholder="Enter bet amount" required min="1"/>
                    </label>
                </p>
                <button type="submit">Place Bet</button>
            </form>
        </div>
    </c:forEach>
</div>

</c:if>

<c:if test="${not empty requestScope.listGameCompletedDto}">


<h2>Completed Games</h2>

<div class="game-list">
    <c:forEach items="${requestScope.listGameCompletedDto}" var="game">
        <div class="game-item">
            <p>${game.homeTeam()} vs ${game.guestTeam()}</p>
            <p>Score: ${game.score()}</p>
            <p>Game Date: ${game.gameDate()}</p>
            <p>
                <c:choose>
                    <c:when test="${game.result() == 'HomeWin'}">Winner: ${game.homeTeam()}</c:when>
                    <c:when test="${game.result() == 'AwayWin'}">Winner: ${game.guestTeam()}</c:when>
                    <c:otherwise>Draw</c:otherwise>
                </c:choose>
            </p>
        </div>
    </c:forEach>
</div>

</c:if>

</html>
