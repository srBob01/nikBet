package ru.arsentiev.dto.game;

import lombok.Builder;
import ru.arsentiev.entity.GameResult;

import java.time.LocalDateTime;

@Builder
public record GameControllerCompletedDto(String homeTeam,
                                         String guestTeam,
                                         String score,
                                         LocalDateTime gameDate,
                                         GameResult result) {
}
