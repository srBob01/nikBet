<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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

    <c:if test="${empty requestScope.predictionBetNotPlayedViewDtoList and empty requestScope.predictionBetPlayedViewDtoList}">
        <h2>You have no bids. Go ahead for big wins</h2>
    </c:if>


    <c:if test="${not empty requestScope.predictionBetPlayedViewDtoList}">
        <h2>Bets Played</h2>
        <c:forEach items="${requestScope.predictionBetPlayedViewDtoList}" var="prediction">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">${prediction.homeTeam()} vs ${prediction.guestTeam()}</h5>
                    <p class="card-text">Score: ${prediction.goalHomeTeam()} - ${prediction.goalGuestTeam()}</p>
                    <p class="card-text">Prediction: ${prediction.prediction()}</p>
                    <p class="card-text">Amount: ${prediction.summa()}</p>
                    <p class="card-text">Coefficient: ${prediction.coefficient()}</p>
                    <p class="card-text">Status: ${prediction.predictionStatus()}</p>
                    <p class="card-text">Result: ${prediction.result()}</p>
                </div>
            </div>
        </c:forEach>
    </c:if>
</div>

</body>
</html>
