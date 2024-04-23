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
VALUES ((SELECT idTeam FROM teams WHERE abbreviation = 'MCI'), (SELECT idTeam FROM teams WHERE abbreviation = 'TOT'),
         NULL, NULL, '2024-04-22 15:00:00+00', 'Scheduled', 1.75, 4.00, 1.95, NULL, NULL),
        ((SELECT idTeam FROM teams WHERE abbreviation = 'EVE'), (SELECT idTeam FROM teams WHERE abbreviation = 'LEI'),
         0, 2, '2024-04-23 15:00:00+00', 'Scheduled', 2.50, 2.75, 1.95, NULL, 'AwayWin'),
        ((SELECT idTeam FROM teams WHERE abbreviation = 'WHU'), (SELECT idTeam FROM teams WHERE abbreviation = 'WOL'),
         2, 3, '2024-04-12 15:00:00+00', 'Completed', 2.80, 2.50, 1.95, NULL, 'AwayWin'),
        ((SELECT idTeam FROM teams WHERE abbreviation = 'LIV'), (SELECT idTeam FROM teams WHERE abbreviation = 'CHE'),
         2, 0, '2024-04-13 15:00:00+00', 'Completed', 2.00, 3.50, 1.95, NULL, 'HomeWin');

INSERT INTO users
(nickname, firstName, lastName, patronymic, password, phoneNumber,
 email, birthDate, salt, role)
VALUES ('coolAdmin', 'Nikita', 'Arsentiev', 'Sergeevich',
        'dff72c8df2937e0e902e84dd8f739d263bc6af59aeef8ccea6231273cd4a4c13',
        '+79969291133', 'admin228@gmail.com', TO_DATE('1999-01-01', 'YYYY-MM-DD'), 'f2ba5dd85d40b5a022ca9ec355bffc6e',
        'ADMIN');

INSERT INTO users
(nickname, firstName, lastName, patronymic, password, phoneNumber,
 email, birthDate, salt)
VALUES ('userNikitka', 'Nikita', 'Arsentiev', 'Sergeevich',
        'b39a63bd12eca3d2955c5381a3b65c073fad8cd430d9d351492037974d3618c3',
        '+79969291100', 'user1251@gmail.com', TO_DATE('1999-01-01', 'YYYY-MM-DD'), 'ac95ff808e44f1d35fcab30229d0a451');

INSERT INTO users (nickname, firstName, lastName, patronymic, password, salt, phoneNumber, email, birthDate,
                   accountBalance, role)
VALUES ('johnDoe', 'John', 'Doe', 'Middle', 'hashedPassword1', 'salt1', '12345678901', 'johndoe1@example.com',
        '1980-01-01', 100.00, 'USER'),
       ('janeDoe', 'Jane', 'Doe', NULL, 'hashedPassword2', 'salt2', '12345678902', 'janedoe2@example.com', '1985-02-02',
        150.00, 'USER'),
       ('bobSmith', 'Bob', 'Smith', 'Middle', 'hashedPassword3', 'salt3', '12345678903', 'bobsmith3@example.com',
        '1990-03-03', 200.00, 'USER'),
       ('aliceJones', 'Alice', 'Jones', NULL, 'hashedPassword4', 'salt4', '12345678904', 'alicejones4@example.com',
        '1982-04-04', 250.00, 'USER'),
       ('charlieBrown', 'Charlie', 'Brown', 'Middle', 'hashedPassword5', 'salt5', '12345678905',
        'charliebrown5@example.com', '1978-05-05', 300.00, 'USER'),
       ('davidLee', 'David', 'Lee', NULL, 'hashedPassword6', 'salt6', '12345678906', 'davidlee6@example.com',
        '1992-06-06', 350.00, 'USER'),
       ('emmaWilson', 'Emma', 'Wilson', 'Middle', 'hashedPassword7', 'salt7', '12345678907', 'emmawilson7@example.com',
        '1987-07-07', 400.00, 'USER'),
       ('frankMoore', 'Frank', 'Moore', NULL, 'hashedPassword8', 'salt8', '12345678908', 'frankmoore8@example.com',
        '1993-08-08', 450.00, 'USER'),
       ('graceLee', 'Grace', 'Lee', 'Middle', 'hashedPassword9', 'salt9', '12345678909', 'gracelee9@example.com',
        '1991-09-09', 500.00, 'USER'),
       ('henryDavis', 'Henry', 'Davis', NULL, 'hashedPassword10', 'salt10', '12345678910', 'henrydavis10@example.com',
        '1979-10-10', 550.00, 'USER');
