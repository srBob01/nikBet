package ru.arsentiev.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.arsentiev.entity.Team;
import ru.arsentiev.exception.RepositoryException;
import ru.arsentiev.processing.connection.ConnectionGetter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeamRepository implements BaseRepository<Long, Team> {
    private static final Logger logger = LogManager.getLogger();
    private final ConnectionGetter connectionGetter;

    public TeamRepository(ConnectionGetter connectionGetter) {
        this.connectionGetter = connectionGetter;
    }

    //language=PostgreSQL
    private static final String INSERT_TEAM = "INSERT INTO teams (title, abbreviation) VALUES (?, ?);";
    //language=PostgreSQL
    private static final String SELECT_ALL_TEAMS = "SELECT idteam, title, abbreviation FROM teams;";
    //language=PostgreSQL
    private static final String SELECT_TEAM_BY_ID = "SELECT idteam, title, abbreviation FROM teams WHERE idTeam = ?;";
    //language=PostgreSQL
    private static final String DELETE_TEAM = "DELETE FROM teams WHERE idTeam = ?;";
    //language=PostgreSQL
    private static final String UPDATE_TEAM = "UPDATE teams SET title = ?, abbreviation = ? WHERE idTeam = ?;";

    @Override
    public boolean insert(Team team) {
        try (Connection connection = connectionGetter.get();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_TEAM, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, team.getTitle());
            preparedStatement.setString(2, team.getAbbreviation());
            boolean res = preparedStatement.executeUpdate() > 0;
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                team.setIdTeam(generatedKeys.getLong(1));
            }
            return res;
        } catch (SQLException | InterruptedException | NullPointerException e) {
            logger.error("Failed to insert team: " + team.toString() + ". Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
    }

    @Override
    public List<Team> selectAll() {
        List<Team> teams = new ArrayList<>();
        try (Connection connection = connectionGetter.get();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_TEAMS);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                teams.add(mapResultSetToTeam(resultSet));
            }
        } catch (SQLException | InterruptedException | NullPointerException e) {
            logger.error("Failed to select teams. Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
        return teams;
    }

    @Override
    public Optional<Team> selectById(Long id) {
        try (Connection connection = connectionGetter.get()) {
            return selectById(id, connection);
        } catch (SQLException | InterruptedException | NullPointerException e) {
            logger.error("Failed to select team by id: " + id + ". Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
    }

    public Optional<Team> selectById(Long id, Connection connection) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_TEAM_BY_ID)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Team team = null;
            if (resultSet.next()) {
                team = mapResultSetToTeam(resultSet);
            }
            return Optional.ofNullable(team);
        } catch (SQLException | NullPointerException e) {
            logger.error("Failed to select team by id: " + id + ". Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try (Connection connection = connectionGetter.get();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_TEAM)) {
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException | InterruptedException | NullPointerException e) {
            logger.error("Failed to delete team by id: " + id + ". Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
    }

    @Override
    public boolean update(Team team) {
        try (Connection connection = connectionGetter.get();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_TEAM)) {
            preparedStatement.setString(1, team.getTitle());
            preparedStatement.setString(2, team.getAbbreviation());
            preparedStatement.setLong(3, team.getIdTeam());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException | InterruptedException | NullPointerException e) {
            logger.error("Failed to update team: " + team.toString() + ". Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
    }

    private Team mapResultSetToTeam(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("idTeam");
        String title = resultSet.getString("title");
        String abbreviation = resultSet.getString("abbreviation");
        return Team.builder()
                .idTeam(id)
                .title(title)
                .abbreviation(abbreviation)
                .build();
    }
}
