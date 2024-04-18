package ru.arsentiev.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.arsentiev.dto.game.*;
import ru.arsentiev.entity.*;
import ru.arsentiev.utils.TimeStampFormatter;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameMapper {
    private static final GameMapper INSTANCE = new GameMapper();

    public static GameMapper getInstance() {
        return INSTANCE;
    }

    public GameViewCompletedDto mapViewCompleted(Game game) {
        return GameViewCompletedDto.builder()
                .idGame(game.getIdGame().toString())
                .homeTeam(game.getHomeTeam().getTitle())
                .guestTeam(game.getGuestTeam().getTitle())
                .score(game.getGoalHomeTeam() + " - " + game.getGoalGuestTeam())
                .gameDate(game.getGameDate().format(TimeStampFormatter.FORMATTER))
                .result(game.getResult().name())
                .build();
    }

    public GameViewInProgressDto mapViewInProgress(Game game) {
        return GameViewInProgressDto.builder()
                .idGame(game.getIdGame().toString())
                .homeTeam(game.getHomeTeam().getTitle())
                .guestTeam(game.getGuestTeam().getTitle())
                .score(game.getGoalHomeTeam() + " - " + game.getGoalGuestTeam())
                .coefficientOnHomeTeam(String.valueOf(game.getCoefficientOnHomeTeam()))
                .coefficientOnDraw(String.valueOf(game.getCoefficientOnDraw()))
                .coefficientOnGuestTeam(String.valueOf(game.getCoefficientOnGuestTeam()))
                .time(game.getTime().name())
                .build();
    }

    public GameViewScheduledDto mapViewScheduled(Game game) {
        return GameViewScheduledDto.builder()
                .idGame(game.getIdGame().toString())
                .homeTeam(game.getHomeTeam().getTitle())
                .guestTeam(game.getGuestTeam().getTitle())
                .coefficientOnHomeTeam(String.valueOf(game.getCoefficientOnHomeTeam()))
                .coefficientOnDraw(String.valueOf(game.getCoefficientOnDraw()))
                .coefficientOnGuestTeam(String.valueOf(game.getCoefficientOnGuestTeam()))
                .gameDate(game.getGameDate().format(TimeStampFormatter.FORMATTER))
                .build();
    }

    public GameControllerCompletedDto map(GameViewCompletedDto gameViewCompletedDto) {
        return GameControllerCompletedDto.builder()
                .idGame(Long.parseLong(gameViewCompletedDto.idGame()))
                .homeTeam(gameViewCompletedDto.homeTeam())
                .guestTeam(gameViewCompletedDto.guestTeam())
                .goalHomeTeam(parseGoals(gameViewCompletedDto.score())[0])
                .goalGuestTeam(parseGoals(gameViewCompletedDto.score())[1])
                .gameDate(TimeStampFormatter.format(gameViewCompletedDto.gameDate()))
                .result(GameResult.valueOf(gameViewCompletedDto.result()))
                .build();
    }

    public GameControllerProgressDto map(GameViewInProgressDto gameViewInProgressDto) {
        return GameControllerProgressDto.builder()
                .idGame(Long.parseLong(gameViewInProgressDto.idGame()))
                .homeTeam(gameViewInProgressDto.homeTeam())
                .guestTeam(gameViewInProgressDto.guestTeam())
                .goalHomeTeam(parseGoals(gameViewInProgressDto.score())[0])
                .goalGuestTeam(parseGoals(gameViewInProgressDto.score())[1])
                .coefficientOnHomeTeam(Float.parseFloat(gameViewInProgressDto.coefficientOnHomeTeam()))
                .coefficientOnDraw(Float.parseFloat(gameViewInProgressDto.coefficientOnDraw()))
                .coefficientOnGuestTeam(Float.parseFloat(gameViewInProgressDto.coefficientOnGuestTeam()))
                .time(GameTime.valueOf(gameViewInProgressDto.time()))
                .build();
    }

    public GameControllerScheduledDto map(GameViewScheduledDto gameViewScheduledDto) {
        return GameControllerScheduledDto.builder()
                .idGame(Long.parseLong(gameViewScheduledDto.idGame()))
                .homeTeam(gameViewScheduledDto.homeTeam())
                .guestTeam(gameViewScheduledDto.guestTeam())
                .coefficientOnHomeTeam(Float.parseFloat(gameViewScheduledDto.coefficientOnHomeTeam()))
                .coefficientOnDraw(Float.parseFloat(gameViewScheduledDto.coefficientOnDraw()))
                .coefficientOnGuestTeam(Float.parseFloat(gameViewScheduledDto.coefficientOnGuestTeam()))
                .gameDate(TimeStampFormatter.format(gameViewScheduledDto.gameDate()))
                .build();
    }


    public Game map(GameControllerCompletedDto gameControllerCompletedDto) {
        return Game.builder()
                .idGame(gameControllerCompletedDto.idGame())
                .homeTeam(Team.builder()
                        .title(gameControllerCompletedDto.homeTeam())
                        .build())
                .guestTeam(Team.builder()
                        .title(gameControllerCompletedDto.guestTeam())
                        .build())
                .goalHomeTeam(gameControllerCompletedDto.goalHomeTeam())
                .goalGuestTeam(gameControllerCompletedDto.goalGuestTeam())
                .gameDate(gameControllerCompletedDto.gameDate())
                .status(GameStatus.Completed)
                .result(gameControllerCompletedDto.result())
                .build();
    }

    public Game map(GameControllerScheduledDto gameControllerScheduledDto) {
        return Game.builder()
                .idGame(gameControllerScheduledDto.idGame())
                .homeTeam(Team.builder()
                        .title(gameControllerScheduledDto.homeTeam())
                        .build())
                .guestTeam(Team.builder()
                        .title(gameControllerScheduledDto.guestTeam())
                        .build())
                .gameDate(gameControllerScheduledDto.gameDate())
                .status(GameStatus.Scheduled)
                .coefficientOnHomeTeam(gameControllerScheduledDto.coefficientOnHomeTeam())
                .coefficientOnDraw(gameControllerScheduledDto.coefficientOnDraw())
                .coefficientOnGuestTeam(gameControllerScheduledDto.coefficientOnGuestTeam())
                .build();
    }

    public Game map(GameControllerProgressDto gameControllerProgressDto) {
        return Game.builder()
                .idGame(gameControllerProgressDto.idGame())
                .homeTeam(Team.builder()
                        .title(gameControllerProgressDto.homeTeam())
                        .build())
                .guestTeam(Team.builder()
                        .title(gameControllerProgressDto.guestTeam())
                        .build())
                .goalHomeTeam(gameControllerProgressDto.goalHomeTeam())
                .goalGuestTeam(gameControllerProgressDto.goalGuestTeam())
                .gameDate(LocalDateTime.now())
                .status(GameStatus.InProgress)
                .coefficientOnHomeTeam(gameControllerProgressDto.coefficientOnHomeTeam())
                .coefficientOnDraw(gameControllerProgressDto.coefficientOnDraw())
                .coefficientOnGuestTeam(gameControllerProgressDto.coefficientOnGuestTeam())
                .time(gameControllerProgressDto.time())
                .build();
    }


    private int[] parseGoals(String score) {
        String[] parts = score.split(" - ");
        return new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
    }
}
