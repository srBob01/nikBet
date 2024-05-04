package ru.arsentiev.entity;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Builder
public class Prediction {
    private long idPrediction;
    private Game game;
    private User user;
    private LocalDateTime predictionDate;
    private BigDecimal summa;
    private GameResult prediction;
    private PredictionStatus predictionStatus;
    private float coefficient;

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Prediction that)) {
            return false;
        }
        return getIdPrediction() == that.getIdPrediction()
               && Float.compare(getCoefficient(), that.getCoefficient()) == 0
               && Objects.equals(getGame(), that.getGame())
               && Objects.equals(getUser(), that.getUser())
               && Objects.equals(getPredictionDate(), that.getPredictionDate())
               && Objects.equals(getSumma(), that.getSumma())
               && getPrediction() == that.getPrediction()
               && getPredictionStatus() == that.getPredictionStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdPrediction(), getGame(), getUser(), getPredictionDate(), getSumma(), getPrediction(), getPredictionStatus(), getCoefficient());
    }
}