package ru.arsentiev.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class Team {
    private Long idTeam;
    private String title;
    private String abbreviation;
}
