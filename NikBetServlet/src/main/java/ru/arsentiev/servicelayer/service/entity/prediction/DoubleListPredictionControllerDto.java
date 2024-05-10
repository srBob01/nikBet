package ru.arsentiev.servicelayer.service.entity.prediction;

import lombok.Builder;
import ru.arsentiev.dto.prediction.controller.PredictionNotPlayedControllerDto;
import ru.arsentiev.dto.prediction.controller.PredictionPlayedControllerDto;

import java.util.List;

@Builder
public record DoubleListPredictionControllerDto(List<PredictionNotPlayedControllerDto> predictionControllerBetNotPlayedDtoList,
                                                List<PredictionPlayedControllerDto> predictionControllerBetPlayedDtoList) {
}
