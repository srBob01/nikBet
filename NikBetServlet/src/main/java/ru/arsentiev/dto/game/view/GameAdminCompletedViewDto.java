package ru.arsentiev.dto.game.view;

import lombok.Builder;

@Builder
public record GameAdminCompletedViewDto(String idGame, String goalHomeTeam, String goalGuestTeam) {
}
