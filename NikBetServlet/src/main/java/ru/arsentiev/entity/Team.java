package ru.arsentiev.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class Team {
    private final int idTeam;
    private String title;
    private String abbreviation;
}
