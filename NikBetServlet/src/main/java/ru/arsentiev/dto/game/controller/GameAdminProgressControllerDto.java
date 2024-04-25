package ru.arsentiev.dto.game.controller;

import lombok.Builder;

@Builder
public record GameAdminProgressControllerDto(long idGame,
                                             int goalHomeTeam,
                                             int goalGuestTeam,
                                             float coefficientOnHomeTeam,
                                             float coefficientOnDraw,
                                             float coefficientOnGuestTeam) {
}
