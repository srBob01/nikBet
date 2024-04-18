package ru.arsentiev.dto.game;

import lombok.Builder;
import ru.arsentiev.entity.GameResult;

import java.time.LocalDateTime;

@Builder
public record GameControllerCompletedDto(Long idGame,
                                         String homeTeam,
                                         String guestTeam,
                                         Integer goalHomeTeam,
                                         Integer goalGuestTeam,
                                         LocalDateTime gameDate,
                                         GameResult result) {
}
