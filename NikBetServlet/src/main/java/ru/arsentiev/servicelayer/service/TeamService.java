package ru.arsentiev.servicelayer.service;

import ru.arsentiev.dto.team.controller.TeamControllerDto;
import ru.arsentiev.mapper.TeamMapper;
import ru.arsentiev.repository.TeamRepository;

import java.util.List;
import java.util.stream.Collectors;

public class TeamService {
    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;

    public TeamService(TeamRepository teamRepository, TeamMapper teamMapper) {
        this.teamRepository = teamRepository;
        this.teamMapper = teamMapper;
    }

    public List<TeamControllerDto> selectAllTeam() {
        return teamRepository.selectAll().stream().map(teamMapper::map).collect(Collectors.toList());
    }

}
