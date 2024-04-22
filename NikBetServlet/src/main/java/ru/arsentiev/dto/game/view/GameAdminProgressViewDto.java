package ru.arsentiev.dto.game.view;

import lombok.Builder;

@Builder
public record GameAdminProgressViewDto(String idGame,
                                       String goalHomeTeam,
                                       String goalGuestTeam,
                                       String coefficientOnHomeTeam,
                                       String coefficientOnDraw,
                                       String coefficientOnGuestTeam) {
}
