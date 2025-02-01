package io.oliverknox.nudge.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import io.oliverknox.nudge.constraint.PeriodSpan;
import io.oliverknox.nudge.domain.Communication;
import io.oliverknox.nudge.domain.Period;

@Data
@NoArgsConstructor
@AllArgsConstructor
@PeriodSpan
@Entity
public class Trigger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Period period;

    @NotNull
    private Integer span;

    private Communication communication = Communication.NEUTRAL;

    public Trigger(Period period, Integer span) {
        this.period = period;
        this.span = span;
    }

    public Trigger(Period period, Integer span, @NonNull Communication communication) {
        this.period = period;
        this.span = span;
        this.communication = communication;
    }
}