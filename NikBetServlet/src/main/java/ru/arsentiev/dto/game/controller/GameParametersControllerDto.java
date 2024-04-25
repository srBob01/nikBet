package ru.arsentiev.dto.game.controller;

import lombok.Builder;
import ru.arsentiev.entity.GameResult;
import ru.arsentiev.entity.GameStatus;

@Builder
public record GameParametersControllerDto(long homeTeamId, long guestTeamId, GameStatus status, GameResult result) {
}