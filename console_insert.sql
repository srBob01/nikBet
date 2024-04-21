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
                   coefficientondraw, coefficientOnGuestTeam, time, result)
VALUES ((SELECT idTeam FROM teams WHERE abbreviation = 'MUN'), (SELECT idTeam FROM teams WHERE abbreviation = 'LIV'),
        2, 1, '2024-04-08 15:00:00+00', 'InProgress', 1.95, 3.25, 1.95, 'Time1', NULL),
       ((SELECT idTeam FROM teams WHERE abbreviation = 'CHE'), (SELECT idTeam FROM teams WHERE abbreviation = 'ARS'),
        1, 1, '2024-04-09 15:00:00+00', 'InProgress', 2.10, 3.10, 1.95, 'Time2', NULL),
       ((SELECT idTeam FROM teams WHERE abbreviation = 'MCI'), (SELECT idTeam FROM teams WHERE abbreviation = 'TOT'),
        NULL, NULL, '2024-04-10 15:00:00+00', 'Scheduled', 1.75, 4.00, 1.95, NULL, NULL),
       ((SELECT idTeam FROM teams WHERE abbreviation = 'EVE'), (SELECT idTeam FROM teams WHERE abbreviation = 'LEI'),
        0, 2, '2024-04-11 15:00:00+00', 'Scheduled', 2.50, 2.75, 1.95, NULL, 'AwayWin'),
       ((SELECT idTeam FROM teams WHERE abbreviation = 'WHU'), (SELECT idTeam FROM teams WHERE abbreviation = 'WOL'),
        2, 3, '2024-04-12 15:00:00+00', 'Completed', 2.80, 2.50, 1.95, NULL, 'AwayWin'),
       ((SELECT idTeam FROM teams WHERE abbreviation = 'LIV'), (SELECT idTeam FROM teams WHERE abbreviation = 'CHE'),
        2, 0, '2024-04-13 15:00:00+00', 'Completed', 2.00, 3.50, 1.95, NULL, 'HomeWin');
