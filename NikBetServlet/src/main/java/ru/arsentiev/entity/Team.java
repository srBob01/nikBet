package ru.arsentiev.entity;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@Builder
public class Team {
    private long idTeam;
    private String title;
    private String abbreviation;

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Team team)) {
            return false;
        }
        return getIdTeam() == team.getIdTeam()
               && Objects.equals(getTitle(), team.getTitle())
               && Objects.equals(getAbbreviation(), team.getAbbreviation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdTeam(), getTitle(), getAbbreviation());
    }

    @Override
    public String toString() {
        return "Team{" +
               "idTeam=" + idTeam +
               ", title='" + title + '\'' +
               ", abbreviation='" + abbreviation + '\'' +
               '}';
    }
}
