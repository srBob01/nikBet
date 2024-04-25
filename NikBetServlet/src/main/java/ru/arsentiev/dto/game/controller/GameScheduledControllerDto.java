package ru.arsentiev.dto.game.controller;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record GameScheduledControllerDto(long idGame,
                                         String homeTeam,
                                         String guestTeam,
                                         float coefficientOnHomeTeam,
                                         float coefficientOnDraw,
                                         float coefficientOnGuestTeam,
                                         LocalDateTime gameDate) {
}
