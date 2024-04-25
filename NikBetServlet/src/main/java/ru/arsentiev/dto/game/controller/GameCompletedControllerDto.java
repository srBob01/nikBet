package ru.arsentiev.dto.game.controller;

import lombok.Builder;
import ru.arsentiev.entity.GameResult;

import java.time.LocalDateTime;

@Builder
public record GameCompletedControllerDto(long idGame,
                                         String homeTeam,
                                         String guestTeam,
                                         int goalHomeTeam,
                                         int goalGuestTeam,
                                         LocalDateTime gameDate,
                                         GameResult result) {
}
