package ru.arsentiev.dto.game;

import java.time.LocalDateTime;

public record GameScheduledDto(String homeTeam,
                               String guestTeam,
                               Float coefficientOnHomeTeam,
                               Float coefficientOnDraw,
                               Float coefficientOnGuestTeam,
                               LocalDateTime gameDate) {
}
