package ru.arsentiev.dto.game.view;

import lombok.Builder;

@Builder
public record GameAdminScheduledViewDto(String idHomeTeam,
                                        String idGuestTeam,
                                        String coefficientOnHomeTeam,
                                        String coefficientOnDraw,
                                        String coefficientOnGuestTeam,
                                        String gameDate) {
}
