package ru.arsentiev.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.arsentiev.dto.team.controller.TeamControllerDto;
import ru.arsentiev.manager.DaoManager;
import ru.arsentiev.mapper.TeamMapper;
import ru.arsentiev.repository.TeamDao;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamService {
    private static final TeamService INSTANCE = new TeamService();
    private final TeamDao teamDao = DaoManager.getTeamDao();
    private final TeamMapper teamMapper = TeamMapper.getInstance();

    public static TeamService getInstance() {
        return INSTANCE;
    }

    public List<TeamControllerDto> selectAllTeam() {
        return teamDao.selectAll().stream().map(teamMapper::map).collect(Collectors.toList());
    }

}
