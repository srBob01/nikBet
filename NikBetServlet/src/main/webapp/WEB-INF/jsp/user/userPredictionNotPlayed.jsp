<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Prediction Listings</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <meta charset="UTF-8">
    <title>Prediction Listings</title>
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
            padding: 0 20px;
        }

        .delete-button {
            background-color: #5C7AEA;
            color: white;
            padding: 10px 20px;
            border-radius: 5px;
            text-decoration: none;
            display: block;
            text-align: center;
            margin: 10px auto;
            width: 300px;
        }

        .delete-button:hover {
            background-color: #3f5bcc;
        }

        .header-link {
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 1000;
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            border-radius: 5px;
            text-decoration: none;
        }

        .header-link:hover {
            background-color: #0056b3;
        }

        .card {
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            margin-top: 20px;
            text-align: center;
        }

        h2 {
            color: #333;
            text-align: center;
            margin: 20px 0;
        }

        .card-title {
            color: #0056b3;
        }

        .card-text {
            margin: 10px 0;
        }

        .btn {
            font-size: 14px;
            line-height: 1.5;
            border-radius: 5px;
            width: 50%;
        }

        .baton {
            font-size: 14px;
            line-height: 1.5;
            border-radius: 5px;
            width: 160px;
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            border: none;
            text-decoration: none;
            display: block;
            margin: 10px auto;
            text-align: center;
            transition: background-color 0.3s;
        }


        form {
            display: flex;
            justify-content: center;
            flex-direction: column;
        }

        .alert {
            text-align: center;
            width: 50%;
            margin: 20px auto;
        }
    </style>
</head>
<body>

<div class="container">

    <a href="<c:url value='/user/prediction/default'/>" class="header-link baton">On default prediction page</a>

    <c:if test="${not empty requestScope.error}">
        <div class="alert alert-danger">
                ${requestScope.error}
        </div>
    </c:if>

    <c:if test="${not empty requestScope.refund}">
        <div class="alert alert-success">
            The bet was successfully deleted. We have refunded it to your account ${requestScope.refund}
        </div>
    </c:if>

    <c:if test="${empty requestScope.predictionBetNotPlayedViewDtoList}">
        <h2>You have no bids. Go ahead for big wins</h2>
    </c:if>


    <c:if test="${not empty requestScope.predictionBetNotPlayedViewDtoList}">
        <h2>Bets Not Played Yet</h2>
        <c:forEach items="${requestScope.predictionBetNotPlayedViewDtoList}" var="prediction">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">${prediction.homeTeam()} vs ${prediction.guestTeam()}</h5>
                    <p class="card-text">
                        <c:if test="${empty prediction.goalHomeTeam() or empty prediction.goalGuestTeam()}">
                            Upcoming Game
                        </c:if>
                        <c:if test="${ not empty prediction.goalHomeTeam() and not empty prediction.goalGuestTeam()}">
                            Score: ${prediction.goalHomeTeam()} - ${prediction.goalGuestTeam()}
                        </c:if>
                    </p>
                    <p class="card-text">Prediction: ${prediction.prediction()}</p>
                    <p class="card-text">Amount: ${prediction.summa()}</p>
                    <p class="card-text">Coefficient: ${prediction.coefficient()}</p>
                    <p class="card-text">Status: ${prediction.predictionStatus()}</p>
                    <form class="delete-form" method="post" action="<c:url value='/user/predictions/notPlayed'/>">
                        <input type="hidden" name="summa" value=" ${prediction.summa()}"/>
                        <input type="hidden" name="coefficient" value=" ${prediction.coefficient()}"/>
                        <input type="hidden" name="idGame" value=" ${prediction.idGame()}"/>
                        <input type="hidden" name="prediction" value=" ${prediction.prediction()}"/>
                        <input type="hidden" name="idPrediction" value="${prediction.idPrediction()}"/>
                        <button type="submit" class="delete-button btn">Delete Prediction</button>
                    </form>
                </div>
            </div>
        </c:forEach>
    </c:if>
</div>

</body>
</html>
