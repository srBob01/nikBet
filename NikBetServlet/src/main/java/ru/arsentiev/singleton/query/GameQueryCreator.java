package ru.arsentiev.singleton.query;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.arsentiev.entity.Game;
import ru.arsentiev.singleton.query.entity.CompletedGameFields;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameQueryCreator {
    private static final GameQueryCreator INSTANCE = new GameQueryCreator();

    public static GameQueryCreator getInstance() {
        return INSTANCE;
    }

    @NonNull
    public String createGameSelectQuery(Game game, CompletedGameFields fields) {
        StringBuilder sql = new StringBuilder("SELECT idgame, idhometeam, idguestteam," +
                                              " goalhometeam, goalguestteam, gamedate," +
                                              " status, coefficientonhometeam, coefficientondraw," +
                                              " coefficientonguestteam, time," +
                                              " result FROM games WHERE ");
        boolean isFirstCondition = true;

        if (fields.isCompletedHomeTeam()) {
            sql.append("idHomeTeam = ").append(game.getHomeTeam().getIdTeam());
            isFirstCondition = false;
        }
        if (fields.isCompletedGuestTeam()) {
            if (!isFirstCondition) {
                sql.append(" AND ");
            }
            sql.append("idGuestTeam = ").append(game.getGuestTeam().getIdTeam());
            isFirstCondition = false;
        }
        if (fields.isCompletedStatusGame()) {
            if (!isFirstCondition) {
                sql.append(" AND ");
            }
            sql.append("status = '").append(game.getStatus()).append("'");
            isFirstCondition = false;
        }

        if (fields.isCompletedResultGame()) {
            if (!isFirstCondition) {
                sql.append(" AND ");
            }
            sql.append("result = '").append(game.getResult().name()).append("'");
            isFirstCondition = false;
        }

        if (isFirstCondition) {
            sql.setLength(sql.length() - 7);
        }
        sql.append(";");

        return sql.toString();
    }
}