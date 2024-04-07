INSERT INTO users (nickname, firstName, lastName, patronymic, password, phoneNumber, email, birthDate, accountBalance,
                   role)
VALUES ('AlexCool', 'Alex', 'Johnson', 'Michaelovich', 'password123!', '+11234567890',
        'alex.johnson@example.com', '1985-03-12', 1500.00, 'USER'),
       ('BettyCrafter', 'Betty', 'Smith', NULL, 'bettySecurePass!', '+11234567891',
        'betty.smith@example.com', '1992-07-08', 2400.00, 'USER'),
       ('CharlieGolf', 'Charlie', 'Garcia', 'Davidovna', 'charliePass!', '+11234567892',
        'charlie.garcia@example.com', '1988-05-15', 3200.00, 'USER'),
       ('DanaScully', 'Dana', 'Scully', NULL, 'truthIsOutThere', '+11234567893',
        'dana.scully@example.com', '1979-02-23', 4100.00, 'USER'),
       ('EvanBright', 'Evan', 'Bright', 'Ivanovich', 'brightSidePass', '+11234567894',
        'evan.bright@example.com', '1990-10-30', 1250.00, 'USER'),
       ('FionaGreen', 'Fiona', 'Green', 'Petrovna', 'fionaGreen123', '+11234567895',
        'fiona.green@example.com', '1995-12-17', 3300.00, 'USER'),
       ('GeorgeKite', 'George', 'Kite', 'Alexandrovich', 'kiteGeorge!', '+11234567896',
        'george.kite@example.com', '1983-08-11', 2100.00, 'USER'),
       ('HannahStar', 'Hannah', 'Star', NULL, 'starBright', '+11234567897',
        'hannah.star@example.com', '1993-09-09', 1900.00, 'USER'),
       ('IanVoyager', 'Ian', 'Voyager', 'Nikolaevich', 'voyagerIan!', '+11234567898',
        'ian.voyager@example.com', '1987-04-04', 2600.00, 'USER');

INSERT INTO teams (title, abbreviation)
VALUES ('Manchester United', 'MUN'),
       ('Liverpool', 'LIV'),
       ('Chelsea', 'CHE'),
       ('Arsenal', 'ARS'),
       ('Manchester City', 'MCI'),
       ('Tottenham Hotspur', 'TOT'),
       ('Everton', 'EVE'),
       ('Leicester City', 'LEI'),
       ('West Ham United', 'WHU'),
       ('Wolverhampton Wanderers', 'WOL');

INSERT INTO games (idHomeTeam, idGuestTeam, goalHomeTeam, goalGuestTeam, gameDate, status, coefficientOnHomeTeam,
                   coefficientOnGuestTeam, result)
VALUES ((SELECT idTeam FROM teams WHERE abbreviation = 'MUN'), (SELECT idTeam FROM teams WHERE abbreviation = 'LIV'),
        2, 1, '2024-04-08 15:00:00+00', 'Completed', 1.95, 3.25, 'HomeWin'),
       ((SELECT idTeam FROM teams WHERE abbreviation = 'CHE'), (SELECT idTeam FROM teams WHERE abbreviation = 'ARS'),
        1, 1, '2024-04-09 15:00:00+00', 'Completed', 2.10, 3.10, 'Draw'),
       ((SELECT idTeam FROM teams WHERE abbreviation = 'MCI'), (SELECT idTeam FROM teams WHERE abbreviation = 'TOT'),
        3, 2, '2024-04-10 15:00:00+00', 'Completed', 1.75, 4.00, 'HomeWin'),
       ((SELECT idTeam FROM teams WHERE abbreviation = 'EVE'), (SELECT idTeam FROM teams WHERE abbreviation = 'LEI'),
        0, 2, '2024-04-11 15:00:00+00', 'Completed', 2.50, 2.75, 'AwayWin'),
       ((SELECT idTeam FROM teams WHERE abbreviation = 'WHU'), (SELECT idTeam FROM teams WHERE abbreviation = 'WOL'),
        2, 3, '2024-04-12 15:00:00+00', 'Completed', 2.80, 2.50, 'AwayWin'),
       ((SELECT idTeam FROM teams WHERE abbreviation = 'LIV'), (SELECT idTeam FROM teams WHERE abbreviation = 'CHE'),
        2, 0, '2024-04-13 15:00:00+00', 'Completed', 2.00, 3.50, 'HomeWin'),
       ((SELECT idTeam FROM teams WHERE abbreviation = 'ARS'), (SELECT idTeam FROM teams WHERE abbreviation = 'MCI'),
        1, 3, '2024-04-14 15:00:00+00', 'Completed', 3.40, 1.90, 'AwayWin'),
       ((SELECT idTeam FROM teams WHERE abbreviation = 'TOT'), (SELECT idTeam FROM teams WHERE abbreviation = 'EVE'),
        1, 1, '2024-04-15 15:00:00+00', 'Completed', 2.20, 3.30, 'Draw'),
       ((SELECT idTeam FROM teams WHERE abbreviation = 'LEI'), (SELECT idTeam FROM teams WHERE abbreviation = 'WHU'),
        1, 2, '2024-04-16 15:00:00+00', 'Completed', 2.60, 2.80, 'AwayWin'),
       ((SELECT idTeam FROM teams WHERE abbreviation = 'WOL'), (SELECT idTeam FROM teams WHERE abbreviation = 'MUN'),
        0, 1, '2024-04-17 15:00:00+00', 'Completed', 3.75, 1.85, 'AwayWin');


INSERT INTO predictions (idGame, idUser, summa, prediction)
VALUES ((SELECT idGame
         FROM games
         WHERE idHomeTeam = (SELECT idTeam FROM teams WHERE abbreviation = 'MUN')
           AND idGuestTeam = (SELECT idTeam FROM teams WHERE abbreviation = 'LIV')),
        (SELECT idUser FROM users WHERE nickname = 'AlexCool'), 100.00, 'HomeWin'),
       ((SELECT idGame
         FROM games
         WHERE idHomeTeam = (SELECT idTeam FROM teams WHERE abbreviation = 'CHE')
           AND idGuestTeam = (SELECT idTeam FROM teams WHERE abbreviation = 'ARS')),
        (SELECT idUser FROM users WHERE nickname = 'BettyCrafter'), 50.00, 'Draw'),
       ((SELECT idGame
         FROM games
         WHERE idHomeTeam = (SELECT idTeam FROM teams WHERE abbreviation = 'MCI')
           AND idGuestTeam = (SELECT idTeam FROM teams WHERE abbreviation = 'TOT')),
        (SELECT idUser FROM users WHERE nickname = 'CharlieGolf'), 75.00, 'HomeWin'),
       ((SELECT idGame
         FROM games
         WHERE idHomeTeam = (SELECT idTeam FROM teams WHERE abbreviation = 'EVE')
           AND idGuestTeam = (SELECT idTeam FROM teams WHERE abbreviation = 'LEI')),
        (SELECT idUser FROM users WHERE nickname = 'DanaScully'), 60.00, 'AwayWin'),
       ((SELECT idGame
         FROM games
         WHERE idHomeTeam = (SELECT idTeam FROM teams WHERE abbreviation = 'WHU')
           AND idGuestTeam = (SELECT idTeam FROM teams WHERE abbreviation = 'WOL')),
        (SELECT idUser FROM users WHERE nickname = 'EvanBright'), 120.00, 'AwayWin'),
       ((SELECT idGame
         FROM games
         WHERE idHomeTeam = (SELECT idTeam FROM teams WHERE abbreviation = 'LIV')
           AND idGuestTeam = (SELECT idTeam FROM teams WHERE abbreviation = 'CHE')),
        (SELECT idUser FROM users WHERE nickname = 'FionaGreen'), 90.00, 'HomeWin'),
       ((SELECT idGame
         FROM games
         WHERE idHomeTeam = (SELECT idTeam FROM teams WHERE abbreviation = 'ARS')
           AND idGuestTeam = (SELECT idTeam FROM teams WHERE abbreviation = 'MCI')),
        (SELECT idUser FROM users WHERE nickname = 'GeorgeKite'), 110.00, 'AwayWin'),
       ((SELECT idGame
         FROM games
         WHERE idHomeTeam = (SELECT idTeam FROM teams WHERE abbreviation = 'TOT')
           AND idGuestTeam = (SELECT idTeam FROM teams WHERE abbreviation = 'EVE')),
        (SELECT idUser FROM users WHERE nickname = 'HannahStar'), 80.00, 'Draw'),
       ((SELECT idGame
         FROM games
         WHERE idHomeTeam = (SELECT idTeam FROM teams WHERE abbreviation = 'LEI')
           AND idGuestTeam = (SELECT idTeam FROM teams WHERE abbreviation = 'WHU')),
        (SELECT idUser FROM users WHERE nickname = 'IanVoyager'), 65.00, 'AwayWin'),
       ((SELECT idGame
         FROM games
         WHERE idHomeTeam = (SELECT idTeam FROM teams WHERE abbreviation = 'WOL')
           AND idGuestTeam = (SELECT idTeam FROM teams WHERE abbreviation = 'MUN')),
        (SELECT idUser FROM users WHERE nickname = 'AlexCool'), 95.00, 'AwayWin');
