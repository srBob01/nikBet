package ru.arsentiev.dto.game.view;

import lombok.Builder;

@Builder
public record GameParametersViewDto(String homeTeamId, String guestTeamId, String status, String result) {
}
