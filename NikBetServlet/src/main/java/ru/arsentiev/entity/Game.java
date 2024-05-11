package ru.arsentiev.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Builder
public class Game {
    private long idGame;
    private Team homeTeam;
    private Team guestTeam;
    private Integer goalHomeTeam;
    private Integer goalGuestTeam;
    private LocalDateTime gameDate;
    private GameStatus status;
    private float coefficientOnHomeTeam;
    private float coefficientOnDraw;
    private float coefficientOnGuestTeam;
    private GameTime time;
    private GameResult result;

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Game game)) {
            return false;
        }
        return getIdGame() == game.getIdGame()
               && Float.compare(getCoefficientOnHomeTeam(), game.getCoefficientOnHomeTeam()) == 0
               && Float.compare(getCoefficientOnDraw(), game.getCoefficientOnDraw()) == 0
               && Float.compare(getCoefficientOnGuestTeam(), game.getCoefficientOnGuestTeam()) == 0
               && Objects.equals(getHomeTeam(), game.getHomeTeam())
               && Objects.equals(getGuestTeam(), game.getGuestTeam())
               && Objects.equals(getGoalHomeTeam(), game.getGoalHomeTeam())
               && Objects.equals(getGoalGuestTeam(), game.getGoalGuestTeam())
               && Objects.equals(getGameDate(), game.getGameDate())
               && getStatus() == game.getStatus() && getTime() == game.getTime()
               && getResult() == game.getResult();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdGame(), getHomeTeam(), getGuestTeam(), getGoalHomeTeam(), getGoalGuestTeam(), getGameDate(),
                getStatus(), getCoefficientOnHomeTeam(), getCoefficientOnDraw(), getCoefficientOnGuestTeam(), getTime(), getResult());
    }

    @Override
    public String toString() {
        return "Game{" +
               "idGame=" + idGame +
               ", homeTeam=" + homeTeam +
               ", guestTeam=" + guestTeam +
               ", goalHomeTeam=" + goalHomeTeam +
               ", goalGuestTeam=" + goalGuestTeam +
               ", gameDate=" + gameDate +
               ", status=" + status +
               ", coefficientOnHomeTeam=" + coefficientOnHomeTeam +
               ", coefficientOnDraw=" + coefficientOnDraw +
               ", coefficientOnGuestTeam=" + coefficientOnGuestTeam +
               ", time=" + time +
               ", result=" + result +
               '}';
    }
}
