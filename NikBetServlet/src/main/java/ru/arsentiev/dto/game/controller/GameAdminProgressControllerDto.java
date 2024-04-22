package ru.arsentiev.dto.game.controller;

import lombok.Builder;

@Builder
public record GameAdminProgressControllerDto(Long idGame,
                                             Integer goalHomeTeam,
                                             Integer goalGuestTeam,
                                             Float coefficientOnHomeTeam,
                                             Float coefficientOnDraw,
                                             Float coefficientOnGuestTeam) {
}
