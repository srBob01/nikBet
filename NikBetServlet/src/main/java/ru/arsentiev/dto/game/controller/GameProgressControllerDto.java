package ru.arsentiev.dto.game.controller;

import lombok.Builder;
import ru.arsentiev.entity.GameTime;

@Builder
public record GameProgressControllerDto(Long idGame,
                                        String homeTeam,
                                        String guestTeam,
                                        Integer goalHomeTeam,
                                        Integer goalGuestTeam,
                                        Float coefficientOnHomeTeam,
                                        Float coefficientOnDraw,
                                        Float coefficientOnGuestTeam,
                                        GameTime time) {
}
