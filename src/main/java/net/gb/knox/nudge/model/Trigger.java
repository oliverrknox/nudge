package net.gb.knox.nudge.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.gb.knox.nudge.constraint.PeriodSpan;
import net.gb.knox.nudge.domain.Communication;
import net.gb.knox.nudge.domain.Period;

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