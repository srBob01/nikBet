package ru.arsentiev.servicelayer.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.arsentiev.dto.team.controller.TeamControllerDto;
import ru.arsentiev.entity.Team;
import ru.arsentiev.mapper.TeamMapper;
import ru.arsentiev.repository.TeamRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {
    private static List<Team> teams;
    private static List<TeamControllerDto> expectedDtoList;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private TeamMapper teamMapper;
    @InjectMocks
    private TeamService teamService;

    @BeforeAll
    static void setUp() {
        teams = Arrays.asList(
                Team.builder().idTeam(1).title("Team A").abbreviation("TA").build(),
                Team.builder().idTeam(2).title("Team B").abbreviation("TB").build()
        );
        expectedDtoList = teams.stream()
                .map(team -> new TeamControllerDto(team.getIdTeam(), team.getTitle()))
                .collect(Collectors.toList());
    }

    @Test
    void selectAllTeam_Valid() {
        when(teamRepository.selectAll()).thenReturn(teams);
        when(teamMapper.map(any(Team.class))).thenAnswer(invocation -> {
            Team team = invocation.getArgument(0);
            return new TeamControllerDto(team.getIdTeam(), team.getTitle());
        });

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