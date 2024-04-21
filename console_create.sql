CREATE TABLE users
(
    idUser         BIGSERIAL PRIMARY KEY,
    nickname       TEXT UNIQUE      NOT NULL,
    firstName      TEXT             NOT NULL,
    lastName       TEXT             NOT NULL,
    patronymic     VARCHAR,
    password       TEXT             NOT NULL,
    phoneNumber    CHAR(12) UNIQUE  NOT NULL,
    email          TEXT UNIQUE      NOT NULL,
    birthDate      DATE             NOT NULL,
    accountBalance DOUBLE PRECISION NOT NULL DEFAULT 0,
    role           TEXT             NOT NULL DEFAULT 'USER'--enum
);

CREATE INDEX users_role_idx ON users (role)
    WHERE role <> 'user';

CREATE TABLE teams
(
    idTeam       BIGSERIAL PRIMARY KEY,
    title        TEXT UNIQUE    NOT NULL,
    abbreviation CHAR(3) UNIQUE NOT NULL
);

CREATE TABLE games
(
    idGame                 BIGSERIAL PRIMARY KEY,
    idHomeTeam             BIGINT REFERENCES teams (idTeam)
        ON DELETE CASCADE
        ON UPDATE CASCADE
                                     NOT NULL,
    idGuestTeam            BIGINT REFERENCES teams (idTeam)
        ON DELETE CASCADE
        ON UPDATE CASCADE
                                     NOT NULL,
    goalHomeTeam           INT,
    goalGuestTeam          INT,
    gameDate               TIMESTAMP NOT NULL,
    status                 TEXT      NOT NULL, --enum
    coefficientOnHomeTeam  NUMERIC(4, 2),
    coefficientOnDraw      NUMERIC(4, 2),
    coefficientOnGuestTeam NUMERIC(4, 2),
    time                   TEXT,
    result                 TEXT                --enum
);

CREATE INDEX games_status_date_idx ON games (gameDate);
CREATE INDEX games_status_idx ON games (status) WHERE status <> 'Completed';
CREATE INDEX games_home_team_idx ON games (idHomeTeam);
CREATE INDEX games_guest_team_idx ON games (idGuestTeam);

CREATE TABLE predictions
(
    idPrediction     BIGSERIAL PRIMARY KEY,
    idGame           BIGINT REFERENCES games
        ON DELETE CASCADE
        ON UPDATE CASCADE
                                      NOT NULL,
    idUser           BIGINT REFERENCES users
        ON DELETE CASCADE
        ON UPDATE CASCADE
                                      NOT NULL,
    predictionDate   TIMESTAMP
                                               DEFAULT current_timestamp
        NOT NULL,
    summa            DOUBLE PRECISION NOT NULL,
    predictionStatus TEXT             NOT NULL DEFAULT 'BetNotPlayed',--enum
    prediction       TEXT             NOT NULL,                       --enum
    coefficient      NUMERIC(4, 2)    NOT NULL
);

CREATE INDEX predictions_game_idx ON predictions (idGame);
CREATE INDEX predictions_user_idx ON predictions (idUser);
