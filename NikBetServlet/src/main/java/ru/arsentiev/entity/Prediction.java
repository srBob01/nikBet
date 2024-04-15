package ru.arsentiev.entity;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class Prediction {
    private Long idPrediction;
    private Game game;
    private User user;
    private LocalDateTime predictionDate;
    private BigDecimal summa;
    private GameResult prediction;
    private PredictionStatus predictionStatus;
}