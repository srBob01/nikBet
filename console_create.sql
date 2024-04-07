CREATE TABLE users
(
    idUser         SERIAL PRIMARY KEY,
    nickname       TEXT UNIQUE      NOT NULL,
    firstName      TEXT             NOT NULL,
    lastName       TEXT             NOT NULL,
    patronymic     TEXT,
    password       TEXT             NOT NULL,
    phoneNumber    CHAR(12) UNIQUE  NOT NULL,
    email          TEXT UNIQUE      NOT NULL,
    birthDate      DATE             NOT NULL,
    accountBalance DOUBLE PRECISION NOT NULL DEFAULT 0,
    role           TEXT             NOT NULL --enum
);

CREATE INDEX users_role_idx ON users (role)
    WHERE role <> 'user';

CREATE TABLE teams
(
    idTeam       SERIAL PRIMARY KEY,
    title        TEXT UNIQUE    NOT NULL,
    abbreviation CHAR(3) UNIQUE NOT NULL
);

CREATE TABLE games
(
    idGame                 SERIAL PRIMARY KEY,
    idHomeTeam             INT REFERENCES teams (idTeam)
        ON DELETE CASCADE
        ON UPDATE CASCADE
                                       NOT NULL,
    idGuestTeam            INT REFERENCES teams (idTeam)
        ON DELETE CASCADE
        ON UPDATE CASCADE
                                       NOT NULL,
    goalHomeTeam           INT,
    goalGuestTeam          INT,
    gameDate               TIMESTAMPTZ NOT NULL,
    status                 TEXT        NOT NULL, --enum
    coefficientOnHomeTeam  NUMERIC(4, 2),
    coefficientOnGuestTeam NUMERIC(4, 2),
    result                 TEXT                  --enum
);

CREATE INDEX games_status_date_idx ON games (gameDate);
CREATE INDEX games_status_idx ON games (status) WHERE status <> 'Completed';
CREATE INDEX games_home_team_idx ON games (idHomeTeam);
CREATE INDEX games_guest_team_idx ON games (idGuestTeam);

CREATE TABLE predictions
(
    idPrediction   SERIAL PRIMARY KEY,
    idGame         INT REFERENCES games
        ON DELETE CASCADE
        ON UPDATE CASCADE
                         NOT NULL,
    idUser         INT REFERENCES users
        ON DELETE CASCADE
        ON UPDATE CASCADE
                         NOT NULL,
    predictionDate timestamptz
        DEFAULT current_timestamp
                         NOT NULL,
    summa          MONEY NOT NULL,
    prediction     TEXT  NOT NULL --enum
);

CREATE INDEX predictions_game_idx ON predictions (idGame);
CREATE INDEX predictions_user_idx ON predictions (idUser);
