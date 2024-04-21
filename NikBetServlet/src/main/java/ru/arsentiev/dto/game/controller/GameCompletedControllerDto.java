package ru.arsentiev.dto.game.controller;

import lombok.Builder;
import ru.arsentiev.entity.GameResult;

import java.time.LocalDateTime;

@Builder
public record GameCompletedControllerDto(Long idGame,
                                         String homeTeam,
                                         String guestTeam,
                                         Integer goalHomeTeam,
                                         Integer goalGuestTeam,
                                         LocalDateTime gameDate,
                                         GameResult result) {
}
