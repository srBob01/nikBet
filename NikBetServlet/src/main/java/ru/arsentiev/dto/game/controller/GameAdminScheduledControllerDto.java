package ru.arsentiev.dto.game.controller;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record GameAdminScheduledControllerDto(Long idHomeTeam,
                                              Long idGuestTeam,
                                              Float coefficientOnHomeTeam,
                                              Float coefficientOnDraw,
                                              Float coefficientOnGuestTeam,
                                              LocalDateTime gameDate) {
}
