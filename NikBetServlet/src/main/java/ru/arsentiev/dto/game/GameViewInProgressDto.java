package ru.arsentiev.dto.game;

import lombok.Builder;

@Builder
public record GameViewInProgressDto(String homeTeam,
                                    String guestTeam,
                                    String score,
                                    String coefficientOnHomeTeam,
                                    String coefficientOnDraw,
                                    String coefficientOnGuestTeam,
                                    String time) {
}
