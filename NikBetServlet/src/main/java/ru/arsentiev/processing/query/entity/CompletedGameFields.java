package ru.arsentiev.processing.query.entity;

import lombok.Builder;

@Builder
public record CompletedGameFields(boolean isCompletedHomeTeam, boolean isCompletedGuestTeam,
                                  boolean isCompletedStatusGame, boolean isCompletedResultGame) {
}
