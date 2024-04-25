package ru.arsentiev.dto.team.controller;

import lombok.Builder;

@Builder
public record TeamControllerDto(long idTeam, String title) {
}
