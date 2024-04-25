package ru.arsentiev.dto.game.controller;

import lombok.Builder;
import ru.arsentiev.entity.GameTime;

@Builder
public record GameProgressControllerDto(long idGame,
                                        String homeTeam,
                                        String guestTeam,
                                        int goalHomeTeam,
                                        int goalGuestTeam,
                                        float coefficientOnHomeTeam,
                                        float coefficientOnDraw,
                                        float coefficientOnGuestTeam,
                                        GameTime time) {
}
