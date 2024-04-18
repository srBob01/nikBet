package ru.arsentiev.dto.game;

import lombok.Builder;

@Builder
public record GameViewScheduledDto(String idGame,
                                   String homeTeam,
                                   String guestTeam,
                                   String coefficientOnHomeTeam,
                                   String coefficientOnDraw,
                                   String coefficientOnGuestTeam,
                                   String gameDate) {
}
