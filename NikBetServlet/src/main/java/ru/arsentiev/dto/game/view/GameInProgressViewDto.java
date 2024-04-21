package ru.arsentiev.dto.game.view;

import lombok.Builder;

@Builder
public record GameInProgressViewDto(String idGame,
                                    String homeTeam,
                                    String guestTeam,
                                    String score,
                                    String coefficientOnHomeTeam,
                                    String coefficientOnDraw,
                                    String coefficientOnGuestTeam,
                                    String time) {
}
