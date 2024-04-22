package ru.arsentiev.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.arsentiev.dto.game.controller.*;
import ru.arsentiev.dto.game.view.*;
import ru.arsentiev.entity.*;
import ru.arsentiev.utils.TimeStampFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameMapper {
    private static final GameMapper INSTANCE = new GameMapper();

    public static GameMapper getInstance() {
        return INSTANCE;
    }

    public GameCompletedControllerDto mapGameToControllerCompleted(Game game) {
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

    public GameProgressControllerDto mapGameToControllerInProgress(Game game) {
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

    public GameScheduledControllerDto mapGameToControllerScheduled(Game game) {
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
                .gameDate(dto.gameDate().format(TimeStampFormatter.ISO_FORMATTER))
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
                .gameDate(dto.gameDate().format(TimeStampFormatter.ISO_FORMATTER))
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

    public GameAdminScheduledControllerDto mapAdminScheduledGameViewToController(GameAdminScheduledViewDto game) {
        return GameAdminScheduledControllerDto.builder()
                .idGuestTeam(Long.parseLong(game.idGuestTeam()))
                .idHomeTeam(Long.parseLong(game.idHomeTeam()))
                .coefficientOnHomeTeam(Float.parseFloat(game.coefficientOnHomeTeam()))
                .coefficientOnDraw(Float.parseFloat(game.coefficientOnDraw()))
                .coefficientOnGuestTeam(Float.parseFloat(game.coefficientOnGuestTeam()))
                .gameDate(TimeStampFormatter.formatISO(game.gameDate()))
                .build();
    }

    public Game mapAdminScheduledControllerToGame(GameAdminScheduledControllerDto game) {
        return Game.builder()
                .homeTeam(Team.builder()
                        .idTeam(game.idHomeTeam())
                        .build())
                .guestTeam(Team.builder()
                        .idTeam(game.idGuestTeam())
                        .build())
                .coefficientOnHomeTeam(game.coefficientOnHomeTeam())
                .coefficientOnDraw(game.coefficientOnDraw())
                .coefficientOnGuestTeam(game.coefficientOnGuestTeam())
                .gameDate(game.gameDate())
                .build();
    }

    public GameAdminProgressControllerDto mapAdminProgressControllerToViewGame(GameAdminProgressViewDto
                                                                                       gameAdminProgressViewDto) {
        return GameAdminProgressControllerDto.builder()
                .goalGuestTeam(Integer.parseInt(gameAdminProgressViewDto.goalGuestTeam()))
                .goalHomeTeam(Integer.parseInt(gameAdminProgressViewDto.goalHomeTeam()))
                .coefficientOnHomeTeam(Float.parseFloat(gameAdminProgressViewDto.coefficientOnHomeTeam()))
                .coefficientOnDraw(Float.parseFloat(gameAdminProgressViewDto.coefficientOnDraw()))
                .coefficientOnGuestTeam(Float.parseFloat(gameAdminProgressViewDto.coefficientOnGuestTeam()))
                .idGame(Long.parseLong(gameAdminProgressViewDto.idGame()))
                .build();
    }

    public Game mapAdminProgressControllerToGame(GameAdminProgressControllerDto gameAdminProgressControllerDto) {
        return Game.builder()
                .goalHomeTeam(gameAdminProgressControllerDto.goalHomeTeam())
                .goalGuestTeam(gameAdminProgressControllerDto.goalGuestTeam())
                .coefficientOnHomeTeam(gameAdminProgressControllerDto.coefficientOnHomeTeam())
                .coefficientOnDraw(gameAdminProgressControllerDto.coefficientOnDraw())
                .coefficientOnGuestTeam(gameAdminProgressControllerDto.coefficientOnGuestTeam())
                .idGame(gameAdminProgressControllerDto.idGame())
                .build();
    }

    public GameAdminCompletedControllerDto mapAdminCompletedViewToControllerGame(GameAdminCompletedViewDto gameAdminCompletedViewDto) {
        int goalHomeTeam = Integer.parseInt(gameAdminCompletedViewDto.goalHomeTeam());
        int goalGuestTeam = Integer.parseInt(gameAdminCompletedViewDto.goalGuestTeam());
        GameResult result = determineGameResult(goalHomeTeam, goalGuestTeam);

        return GameAdminCompletedControllerDto.builder()
                .idGame(Long.parseLong(gameAdminCompletedViewDto.idGame()))
                .result(result)
                .build();
    }

    private GameResult determineGameResult(int goalHomeTeam, int goalGuestTeam) {
        if (goalHomeTeam > goalGuestTeam) {
            return GameResult.HomeWin;
        } else if (goalHomeTeam < goalGuestTeam) {
            return GameResult.AwayWin;
        } else {
            return GameResult.Draw;
        }
    }

    public Game mapAdminCompletedControllerToGame(GameAdminCompletedControllerDto gameAdminCompletedControllerDto) {
        return Game.builder()
                .idGame(gameAdminCompletedControllerDto.idGame())
                .result(gameAdminCompletedControllerDto.result())
                .build();
    }
}
