package ru.arsentiev.dto.game;

import ru.arsentiev.entity.GameResult;

import java.time.LocalDateTime;

public record GameCompletedDto(String homeTeam,
                               String guestTeam,
                               String score,
                               LocalDateTime gameDate,
                               GameResult result) {
}
