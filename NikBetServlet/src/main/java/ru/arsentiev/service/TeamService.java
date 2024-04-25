package ru.arsentiev.service;

import ru.arsentiev.dto.team.controller.TeamControllerDto;
import ru.arsentiev.mapper.TeamMapper;
import ru.arsentiev.repository.TeamDao;

import java.util.List;
import java.util.stream.Collectors;

public class TeamService {
    private final TeamDao teamDao;
    private final TeamMapper teamMapper;

    public TeamService(TeamDao teamDao, TeamMapper teamMapper) {
        this.teamDao = teamDao;
        this.teamMapper = teamMapper;
    }

    public List<TeamControllerDto> selectAllTeam() {
        return teamDao.selectAll().stream().map(teamMapper::map).collect(Collectors.toList());
    }

}
