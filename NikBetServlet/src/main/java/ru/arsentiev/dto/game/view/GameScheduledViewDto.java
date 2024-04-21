package ru.arsentiev.dto.game.view;

import lombok.Builder;

@Builder
public record GameScheduledViewDto(String idGame,
                                   String homeTeam,
                                   String guestTeam,
                                   String coefficientOnHomeTeam,
                                   String coefficientOnDraw,
                                   String coefficientOnGuestTeam,
                                   String gameDate) {
}
