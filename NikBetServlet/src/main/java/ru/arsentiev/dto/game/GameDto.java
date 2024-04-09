package ru.arsentiev.dto.game;

import java.time.LocalDateTime;

public record GameDto(
        String homeTeam,
        String guestTeam,
        String score,
        Float coefficientOnHomeTeam,
        Float coefficientOnDraw,
        Float coefficientOnGuestTeam,
        String status,
        LocalDateTime gameDate) {
}
