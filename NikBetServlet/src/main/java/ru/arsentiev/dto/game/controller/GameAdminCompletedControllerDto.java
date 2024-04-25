package ru.arsentiev.dto.game.controller;

import lombok.Builder;
import ru.arsentiev.entity.GameResult;

@Builder
public record GameAdminCompletedControllerDto(long idGame, GameResult result) {
}
