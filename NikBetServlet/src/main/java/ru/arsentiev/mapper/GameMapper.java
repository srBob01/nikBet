package ru.arsentiev.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.arsentiev.dto.game.controller.*;
import ru.arsentiev.dto.game.view.GameCompletedViewDto;
import ru.arsentiev.dto.game.view.GameInProgressViewDto;
import ru.arsentiev.dto.game.view.GameParametersViewDto;
import ru.arsentiev.dto.game.view.GameScheduledViewDto;
import ru.arsentiev.entity.*;
import ru.arsentiev.utils.LocalDateFormatter;
import ru.arsentiev.utils.TimeStampFormatter;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameMapper {
    private static final GameMapper INSTANCE = new GameMapper();

    public static GameMapper getInstance() {
        return INSTANCE;
    }

    public GameCompletedControllerDto mapControllerCompletedGameToController(Game game) {
        return GameCompletedControllerDto.builder()
                .idGame(game.getIdGame())
                .homeTeam(game.getHomeTeam().getTitle())
                .guestTeam(game.getGuestTeam().getTitle())
                .goalHomeTeam(game.getGoalHomeTeam())
                .goalGuestTeam(game.getGoalGuestTeam())
                .gameDate(game.getGameDate())
                .result(game.getResult())
                .build();
    }

    public GameProgressControllerDto mapControllerInProgressGameToController(Game game) {
        return GameProgressControllerDto.builder()
                .idGame(game.getIdGame())
                .homeTeam(game.getHomeTeam().getTitle())
                .guestTeam(game.getGuestTeam().getTitle())
                .goalHomeTeam(game.getGoalHomeTeam())
                .goalGuestTeam(game.getGoalGuestTeam())
                .coefficientOnHomeTeam(game.getCoefficientOnHomeTeam())
                .coefficientOnDraw(game.getCoefficientOnDraw())
                .coefficientOnGuestTeam(game.getCoefficientOnGuestTeam())
                .time(game.getTime())
                .build();
    }

    public GameScheduledControllerDto mapControllerScheduledGameToController(Game game) {
        return GameScheduledControllerDto.builder()
                .idGame(game.getIdGame())
                .homeTeam(game.getHomeTeam().getTitle())
                .guestTeam(game.getGuestTeam().getTitle())
                .coefficientOnHomeTeam(game.getCoefficientOnHomeTeam())
                .coefficientOnDraw(game.getCoefficientOnDraw())
                .coefficientOnGuestTeam(game.getCoefficientOnGuestTeam())
                .gameDate(game.getGameDate())
                .build();
    }

    public GameCompletedViewDto mapCompletedControllerToView(GameCompletedControllerDto dto) {
        String score = dto.goalHomeTeam() + " - " + dto.goalGuestTeam();
        return GameCompletedViewDto.builder()
                .idGame(String.valueOf(dto.idGame()))
                .homeTeam(dto.homeTeam())
                .guestTeam(dto.guestTeam())
                .score(score)
                .gameDate(dto.gameDate().format(LocalDateFormatter.FORMATTER))
                .result(dto.result().name())
                .build();
    }

    public GameInProgressViewDto mapProgressControllerToView(GameProgressControllerDto dto) {
        String score = dto.goalHomeTeam() + " - " + dto.goalGuestTeam();
        return GameInProgressViewDto.builder()
                .idGame(String.valueOf(dto.idGame()))
                .homeTeam(dto.homeTeam())
                .guestTeam(dto.guestTeam())
                .score(score)
                .coefficientOnHomeTeam(String.format("%.2f", dto.coefficientOnHomeTeam()))
                .coefficientOnDraw(String.format("%.2f", dto.coefficientOnDraw()))
                .coefficientOnGuestTeam(String.format("%.2f", dto.coefficientOnGuestTeam()))
                .time(dto.time().name())
                .build();
    }

    public GameScheduledViewDto mapScheduledControllerToView(GameScheduledControllerDto dto) {
        return GameScheduledViewDto.builder()
                .idGame(String.valueOf(dto.idGame()))
                .homeTeam(dto.homeTeam())
                .guestTeam(dto.guestTeam())
                .coefficientOnHomeTeam(String.format("%.2f", dto.coefficientOnHomeTeam()))
                .coefficientOnDraw(String.format("%.2f", dto.coefficientOnDraw()))
                .coefficientOnGuestTeam(String.format("%.2f", dto.coefficientOnGuestTeam()))
                .gameDate(dto.gameDate().format(LocalDateFormatter.FORMATTER))
                .build();
    }

    public GameCompletedControllerDto map(GameCompletedViewDto gameCompletedViewDto) {
        return GameCompletedControllerDto.builder()
                .idGame(Long.parseLong(gameCompletedViewDto.idGame()))
                .homeTeam(gameCompletedViewDto.homeTeam())
                .guestTeam(gameCompletedViewDto.guestTeam())
                .goalHomeTeam(parseGoals(gameCompletedViewDto.score())[0])
                .goalGuestTeam(parseGoals(gameCompletedViewDto.score())[1])
                .gameDate(TimeStampFormatter.format(gameCompletedViewDto.gameDate()))
                .result(GameResult.valueOf(gameCompletedViewDto.result()))
                .build();
    }

    public GameProgressControllerDto map(GameInProgressViewDto gameInProgressViewDto) {
        return GameProgressControllerDto.builder()
                .idGame(Long.parseLong(gameInProgressViewDto.idGame()))
                .homeTeam(gameInProgressViewDto.homeTeam())
                .guestTeam(gameInProgressViewDto.guestTeam())
                .goalHomeTeam(parseGoals(gameInProgressViewDto.score())[0])
                .goalGuestTeam(parseGoals(gameInProgressViewDto.score())[1])
                .coefficientOnHomeTeam(Float.parseFloat(gameInProgressViewDto.coefficientOnHomeTeam()))
                .coefficientOnDraw(Float.parseFloat(gameInProgressViewDto.coefficientOnDraw()))
                .coefficientOnGuestTeam(Float.parseFloat(gameInProgressViewDto.coefficientOnGuestTeam()))
                .time(GameTime.valueOf(gameInProgressViewDto.time()))
                .build();
    }

    public GameScheduledControllerDto map(GameScheduledViewDto gameScheduledViewDto) {
        return GameScheduledControllerDto.builder()
                .idGame(Long.parseLong(gameScheduledViewDto.idGame()))
                .homeTeam(gameScheduledViewDto.homeTeam())
                .guestTeam(gameScheduledViewDto.guestTeam())
                .coefficientOnHomeTeam(Float.parseFloat(gameScheduledViewDto.coefficientOnHomeTeam()))
                .coefficientOnDraw(Float.parseFloat(gameScheduledViewDto.coefficientOnDraw()))
                .coefficientOnGuestTeam(Float.parseFloat(gameScheduledViewDto.coefficientOnGuestTeam()))
                .gameDate(TimeStampFormatter.format(gameScheduledViewDto.gameDate()))
                .build();
    }


    public Game mapCompletedControllerToGame(GameCompletedControllerDto gameCompletedControllerDto) {
        return Game.builder()
                .idGame(gameCompletedControllerDto.idGame())
                .homeTeam(Team.builder()
                        .title(gameCompletedControllerDto.homeTeam())
                        .build())
                .guestTeam(Team.builder()
                        .title(gameCompletedControllerDto.guestTeam())
                        .build())
                .goalHomeTeam(gameCompletedControllerDto.goalHomeTeam())
                .goalGuestTeam(gameCompletedControllerDto.goalGuestTeam())
                .gameDate(gameCompletedControllerDto.gameDate())
                .status(GameStatus.Completed)
                .result(gameCompletedControllerDto.result())
                .build();
    }

    public Game mapScedudeludControllerToGame(GameScheduledControllerDto gameScheduledControllerDto) {
        return Game.builder()
                .idGame(gameScheduledControllerDto.idGame())
                .homeTeam(Team.builder()
                        .title(gameScheduledControllerDto.homeTeam())
                        .build())
                .guestTeam(Team.builder()
                        .title(gameScheduledControllerDto.guestTeam())
                        .build())
                .gameDate(gameScheduledControllerDto.gameDate())
                .status(GameStatus.Scheduled)
                .coefficientOnHomeTeam(gameScheduledControllerDto.coefficientOnHomeTeam())
                .coefficientOnDraw(gameScheduledControllerDto.coefficientOnDraw())
                .coefficientOnGuestTeam(gameScheduledControllerDto.coefficientOnGuestTeam())
                .build();
    }

    public Game mapProgressControllerToGame(GameProgressControllerDto gameProgressControllerDto) {
        return Game.builder()
                .idGame(gameProgressControllerDto.idGame())
                .homeTeam(Team.builder()
                        .title(gameProgressControllerDto.homeTeam())
                        .build())
                .guestTeam(Team.builder()
                        .title(gameProgressControllerDto.guestTeam())
                        .build())
                .goalHomeTeam(gameProgressControllerDto.goalHomeTeam())
                .goalGuestTeam(gameProgressControllerDto.goalGuestTeam())
                .gameDate(LocalDateTime.now())
                .status(GameStatus.InProgress)
                .coefficientOnHomeTeam(gameProgressControllerDto.coefficientOnHomeTeam())
                .coefficientOnDraw(gameProgressControllerDto.coefficientOnDraw())
                .coefficientOnGuestTeam(gameProgressControllerDto.coefficientOnGuestTeam())
                .time(gameProgressControllerDto.time())
                .build();
    }

    public Game map(GameParametersControllerDto gameParametersControllerDto) {
        return Game.builder()
                .homeTeam(Team.builder()
                        .idTeam(gameParametersControllerDto.homeTeamId())
                        .build())
                .guestTeam(Team.builder()
                        .idTeam(gameParametersControllerDto.guestTeamId())
                        .build())
                .status(gameParametersControllerDto.status())
                .result(gameParametersControllerDto.result())
                .build();
    }

    public GameParametersControllerDto map(GameParametersViewDto gameParametersViewDto) {
        return GameParametersControllerDto.builder()
                .guestTeamId(gameParametersViewDto.guestTeamId().equals("none") ? null
                        : Long.parseLong(gameParametersViewDto.guestTeamId()))
                .homeTeamId(gameParametersViewDto.homeTeamId().equals("none") ? null
                        : Long.parseLong(gameParametersViewDto.homeTeamId()))
                .result(gameParametersViewDto.result().equals("none") ? null :
                        GameResult.valueOf(gameParametersViewDto.result()))
                .status(gameParametersViewDto.status().equals("none") ? null :
                        GameStatus.valueOf(gameParametersViewDto.status()))
                .build();
    }

    private int[] parseGoals(String score) {
        String[] parts = score.split(" - ");
        return new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
    }
}
