package ru.arsentiev.servicelayer.service.entity.game;

import lombok.Builder;
import ru.arsentiev.dto.game.controller.GameCompletedControllerDto;
import ru.arsentiev.dto.game.controller.GameProgressControllerDto;
import ru.arsentiev.dto.game.controller.GameScheduledControllerDto;

import java.util.List;

@Builder
public record TripleListOfGameControllerDto(List<GameProgressControllerDto> gameViewInProgressDtoList,
                                            List<GameScheduledControllerDto> gameViewScheduledDtoList,
                                            List<GameCompletedControllerDto> gameViewCompletedDtoList) {
}
