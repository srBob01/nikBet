package ru.arsentiev.entity;

import lombok.*;

@Getter
@Setter
@Builder
public class Team {
    private long idTeam;
    private String title;
    private String abbreviation;
}
