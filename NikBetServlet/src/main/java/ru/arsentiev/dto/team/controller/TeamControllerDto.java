package ru.arsentiev.dto.team.controller;

import lombok.Builder;

@Builder
public record TeamControllerDto(Long idTeam, String title) {
}
