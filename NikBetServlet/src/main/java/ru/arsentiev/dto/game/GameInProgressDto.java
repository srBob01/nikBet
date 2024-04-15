package ru.arsentiev.dto.game;

import ru.arsentiev.entity.GameTime;

public record GameInProgressDto(String homeTeam,
                                String guestTeam,
                                String score,
                                Float coefficientOnHomeTeam,
                                Float coefficientOnDraw,
                                Float coefficientOnGuestTeam,
                                GameTime time) {
}
