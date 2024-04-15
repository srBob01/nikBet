package ru.arsentiev.dto.game;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record GameControllerScheduledDto(String homeTeam,
                                         String guestTeam,
                                         Float coefficientOnHomeTeam,
                                         Float coefficientOnDraw,
                                         Float coefficientOnGuestTeam,
                                         LocalDateTime gameDate) {
}
