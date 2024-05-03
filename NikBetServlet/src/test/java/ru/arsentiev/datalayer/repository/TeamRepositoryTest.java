package ru.arsentiev.datalayer.repository;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.arsentiev.datalayer.TestConnectionGetter;
import ru.arsentiev.entity.Team;
import ru.arsentiev.exception.RepositoryException;
import ru.arsentiev.repository.TeamRepository;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TeamRepositoryTest {
    private final TestConnectionGetter connectionGetter = TestConnectionGetter.getInstance();
    private final TeamRepository teamRepository = new TeamRepository(connectionGetter);

    private static Stream<List<Team>> generateValidTeamList() {
        Team team1 = Team.builder()
                .title("Liverpool")
                .abbreviation("LIV")
                .build();

        Team team2 = Team.builder()
                .title("Arsenal")
                .abbreviation("ARS")
                .build();

        return Stream.of(List.of(team1, team2), List.of(team1));
    }

    private static Stream<Team> generateValidTeam() {
        Team team1 = Team.builder()
                .title("Liverpool")
                .abbreviation("LIV")
                .build();

        Team team2 = Team.builder()
                .title("Arsenal")
                .abbreviation("ARS")
                .build();

        return Stream.of(team1, team2);
    }

    private static Stream<Team> generateInvalidTeam() {
        Team team1 = Team.builder()
                .abbreviation("LIV")
                .build();

        Team team2 = Team.builder()
                .title("Arsenal")
                .abbreviation("ARSENAL")
                .build();

        Team team3 = Team.builder()
                .build();

        return Stream.of(team1, team2, team3);
    }

    private static Team defaultTeam() {
        return Team.builder()
                .title("Liverpool")
                .abbreviation("LIV")
                .build();
    }

    @SneakyThrows
    @BeforeEach
    public void clear() {
        try (Connection connection = connectionGetter.get()) {
            //language=PostgreSQL
            String CLEAR_TABLE = "TRUNCATE nikbet_test.public.teams RESTART IDENTITY CASCADE";
            connection.prepareStatement(CLEAR_TABLE).executeUpdate();
        }
    }

    @ParameterizedTest
    @MethodSource("generateValidTeam")
    void insertValidTeamTest(Team team) {
        assertThat(teamRepository.insert(team)).isTrue();
        assertThat(team.getIdTeam()).isNotZero();

        Optional<Team> teamOptional = teamRepository.selectById(team.getIdTeam());
        assertThat(teamOptional.isPresent()).isTrue();
        Team teamInsert = teamOptional.get();
        assertThat(teamInsert).isEqualTo(team);
    }

    @ParameterizedTest
    @MethodSource("generateInvalidTeam")
    void insertInvalidTeamTest(Team team) {
        assertThatThrownBy(() -> teamRepository.insert(team)).isInstanceOf(RepositoryException.class);
    }

    @ParameterizedTest
    @MethodSource("generateValidTeamList")
    void selectAllTeamTest(List<Team> teams) {
        for (var team : teams) {
            teamRepository.insert(team);
        }
        List<Team> teamsFromRepository = teamRepository.selectAll();
        assertThat(teamsFromRepository).isEqualTo(teams);
    }

    @Test
    void selectTeamByValidSomethingTest() {
        Team team = defaultTeam();

        teamRepository.insert(team);

        Optional<Team> teamOptionalId = teamRepository.selectById(team.getIdTeam());
        assertThat(teamOptionalId.isPresent()).isTrue();
        Team teamFromRepositoryId = teamOptionalId.get();
        assertThat(teamFromRepositoryId).isEqualTo(team);
    }

    @Test
    void selectTeamByInvalidSomethingTest() {
        long wrongId = -1L;

        assertThat(teamRepository.selectById(wrongId)).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("generateValidTeamList")
    void deleteValidTeamTest(List<Team> teams) {
        for (var team : teams) {
            teamRepository.insert(team);
        }
        long idUser = teams.get(0).getIdTeam();

        assertThat(teamRepository.delete(idUser)).isTrue();

        assertThat(teamRepository.selectById(idUser)).isEmpty();
    }

    @Test
    void deleteInvalidTeamTest() {
        long wrongIdUser = -1L;

        assertThat(teamRepository.delete(wrongIdUser)).isFalse();
    }

    @Test
    void updateTeamTest() {
        Team team = defaultTeam();
        teamRepository.insert(team);

        team.setTitle("Chelsea");
        team.setAbbreviation("CHE");
        assertThat(teamRepository.update(team)).isTrue();

        Optional<Team> optionalTeam = teamRepository.selectById(team.getIdTeam());
        assertThat(optionalTeam.isPresent()).isTrue();
        Team newTeam = optionalTeam.get();

        assertThat(newTeam).isEqualTo(team);
    }
}