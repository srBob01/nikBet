package ru.arsentiev.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class Prediction {
    private final int idPrediction;
    private Game game;
    private User user;
    private LocalDateTime predictionDate;
    private BigDecimal summa;
    private GameResult prediction;
}