package ru.arsentiev.dto.team.view;

import lombok.Builder;

@Builder
public record TeamViewDto(String idTeam, String title) {
}
