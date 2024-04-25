package ru.arsentiev.manager;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import ru.arsentiev.mapper.GameMapper;
import ru.arsentiev.mapper.PredictionMapper;
import ru.arsentiev.mapper.TeamMapper;
import ru.arsentiev.mapper.UserMapper;

@UtilityClass
public class MapperManager {
    @Getter
    private static final GameMapper gameMapper;
    @Getter
    private static final PredictionMapper predictionMapper;
    @Getter
    private static final TeamMapper teamMapper;
    @Getter
    private static final UserMapper userMapper;

    static {
        gameMapper = new GameMapper(DateFormatterManager.getTimeStampFormatter());
        predictionMapper = new PredictionMapper(DateFormatterManager.getTimeStampFormatter());
        teamMapper = new TeamMapper();
        userMapper = new UserMapper(DateFormatterManager.getLocalDateFormatter());
    }
}
