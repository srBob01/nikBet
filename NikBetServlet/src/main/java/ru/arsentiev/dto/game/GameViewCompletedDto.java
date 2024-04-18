package ru.arsentiev.dto.game;

import lombok.Builder;

@Builder
public record GameViewCompletedDto(String idGame,
                                   String homeTeam,
                                   String guestTeam,
                                   String score,
                                   String gameDate,
                                   String result) {
}
