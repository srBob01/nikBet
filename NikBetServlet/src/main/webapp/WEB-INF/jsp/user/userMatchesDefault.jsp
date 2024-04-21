<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Game Listings</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 20px;
            position: relative;
        }

        .fixed-button-left, .fixed-button-right {
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

        .full-list-button {
            background-color: #5C7AEA;
            color: white;
            padding: 10px 20px;
            text-align: center;
            display: block;
            width: 300px;
            margin: 20px auto;
            border-radius: 5px;
            font-size: 14px;
            text-decoration: none;
        }

        .full-list-button:hover {
            background-color: #3f5bcc;
        }

        h2 {
            color: #333;
            text-align: center;
            margin-bottom: 20px;
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

        .betting-form {
            margin-top: 20px;
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

        button {
            padding: 10px 20px;
            background-color: #5C7AEA;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            transition: background-color 0.3s;
            font-size: 16px;
            text-align: center;
            line-height: 30px;
        }

        button:hover {
            background-color: #3f5bcc;
        }

        input[type="number"], input[type="radio"] {
            margin-right: 5px;
        }

        label {
            margin-right: 15px;
        }
    </style>
</head>
<body>


<a href="<c:url value='/user/default'/>" class="fixed-button-left">
    <button type="button">On default page</button>
</a>

<a href="<c:url value='/user/matches/find'/>" class="fixed-button-right">
    <button type="button">Find game</button>
</a>

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

<h2>Games In Progress</h2>

<a href="<c:url value='/user/matches/all/in-progress'/>" class="full-list-button">
    <button type="button">View All In-Progress Games</button>
</a>

<div class="game-list">
    <c:forEach items="${requestScope.listGameInProgressDto}" var="game">
        <div class="game-item">
            <p>${game.homeTeam()} vs ${game.guestTeam()}</p>
            <p>Score: ${game.score()}</p>
            <p>Time: ${game.time()}</p>
            <form class="betting-form" method="post" action="<c:url value='/user/matches/default'/>">
                <input type="hidden" name="action" value="placeBetInProgress"/>
                <input type="hidden" name="gameId" value="${game.idGame()}"/>
                <input type="hidden" name="homeTeam" value="${game.homeTeam()}"/>
                <input type="hidden" name="guestTeam" value="${game.guestTeam()}"/>
                <input type="hidden" name="coefficientOnHomeTeam" value="${game.coefficientOnHomeTeam()}"/>
                <input type="hidden" name="coefficientOnDraw" value="${game.coefficientOnDraw()}"/>
                <input type="hidden" name="coefficientOnGuestTeam" value="${game.coefficientOnGuestTeam()}"/>
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

<h2>Scheduled Games</h2>

<a href="<c:url value='/user/matches/all/scheduled'/>" class="full-list-button">
    <button type="button">View All Scheduled Games</button>
</a>

<div class="game-list">
    <c:forEach items="${requestScope.listGameScheduledDto}" var="game">
        <div class="game-item">
            <p>${game.homeTeam()} vs ${game.guestTeam()}</p>
            <p>Game Date: ${game.gameDate()}</p>
            <form class="betting-form" method="post" action="<c:url value='/user/matches/default'/>">
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
                <button type="submit">Place Bet</button>
            </form>
        </div>
    </c:forEach>
</div>

<h2>Completed Games</h2>

<a href="<c:url value='/user/matches/all/completed'/>" class="full-list-button">
    <button type="button">View All Completed Games</button>
</a>

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

</body>
</html>
