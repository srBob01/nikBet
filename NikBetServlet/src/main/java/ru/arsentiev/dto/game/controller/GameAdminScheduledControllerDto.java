package ru.arsentiev.dto.game.controller;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record GameAdminScheduledControllerDto(long idHomeTeam,
                                              long idGuestTeam,
                                              float coefficientOnHomeTeam,
                                              float coefficientOnDraw,
                                              float coefficientOnGuestTeam,
                                              LocalDateTime gameDate) {
}
