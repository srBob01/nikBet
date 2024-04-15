package ru.arsentiev.dto.game;

import lombok.Builder;
import ru.arsentiev.entity.GameTime;

@Builder
public record GameControllerProgressDto(String homeTeam,
                                        String guestTeam,
                                        String score,
                                        Float coefficientOnHomeTeam,
                                        Float coefficientOnDraw,
                                        Float coefficientOnGuestTeam,
                                        GameTime time) {
}
