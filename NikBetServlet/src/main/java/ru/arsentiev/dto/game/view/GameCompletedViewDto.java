package ru.arsentiev.dto.game.view;

import lombok.Builder;

@Builder
public record GameCompletedViewDto(String idGame,
                                   String homeTeam,
                                   String guestTeam,
                                   String score,
                                   String gameDate,
                                   String result) {
}
