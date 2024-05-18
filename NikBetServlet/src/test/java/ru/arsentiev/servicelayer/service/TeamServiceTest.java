package ru.arsentiev.servicelayer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.arsentiev.dto.team.controller.TeamControllerDto;
import ru.arsentiev.entity.Team;
import ru.arsentiev.mapper.TeamMapper;
import ru.arsentiev.repository.TeamRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TeamServiceTest {
    private final List<Team> teams;
    private final List<TeamControllerDto> expectedDtoList;
    private TeamRepository teamRepository;
    private final TeamMapper teamMapper;
    private TeamService teamService;

    {
        teamMapper = new TeamMapper();
        teams = Arrays.asList(
                Team.builder().idTeam(1).title("Team A").abbreviation("TA").build(),
                Team.builder().idTeam(2).title("Team B").abbreviation("TB").build()
        );
        expectedDtoList = teams.stream()
                .map(team -> new TeamControllerDto(team.getIdTeam(), team.getTitle()))
                .collect(Collectors.toList());
    }

    @BeforeEach
    void createService() {
        teamRepository = mock(TeamRepository.class);
        teamService = new TeamService(teamRepository, teamMapper);
    }

    @Test
    void selectAllTeam_Valid() {
        when(teamRepository.selectAll()).thenReturn(teams);

        List<TeamControllerDto> result = teamService.selectAllTeam();

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedDtoList);
    }

    @Test
    void selectAllTeam_Empty() {
        when(teamRepository.selectAll()).thenReturn(Collections.emptyList());

        List<TeamControllerDto> result = teamService.selectAllTeam();

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }
}