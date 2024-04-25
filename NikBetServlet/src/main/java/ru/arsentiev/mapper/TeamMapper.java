package ru.arsentiev.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.arsentiev.dto.team.controller.TeamControllerDto;
import ru.arsentiev.dto.team.view.TeamViewDto;
import ru.arsentiev.entity.Team;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class TeamMapper {
    public TeamViewDto map(TeamControllerDto teamControllerDto) {
        return TeamViewDto.builder()
                .idTeam(String.valueOf(teamControllerDto.idTeam()))
                .title(teamControllerDto.title())
                .build();
    }

    public TeamControllerDto map(TeamViewDto teamViewDto) {
        return TeamControllerDto.builder()
                .idTeam(Long.parseLong(teamViewDto.idTeam()))
                .title(teamViewDto.title())
                .build();
    }

    public TeamControllerDto map(Team team) {
        return TeamControllerDto.builder()
                .idTeam(team.getIdTeam())
                .title(team.getTitle())
                .build();
    }
}
