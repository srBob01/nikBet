package ru.arsentiev.dto.game.controller;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record GameScheduledControllerDto(Long idGame,
                                         String homeTeam,
                                         String guestTeam,
                                         Float coefficientOnHomeTeam,
                                         Float coefficientOnDraw,
                                         Float coefficientOnGuestTeam,
                                         LocalDateTime gameDate) {
}
