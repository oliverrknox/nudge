package net.gb.knox.nudge.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.gb.knox.nudge.constraint.IntervalSpan;
import net.gb.knox.nudge.domain.Communication;
import net.gb.knox.nudge.domain.Interval;

@Data
@NoArgsConstructor
@IntervalSpan
@Entity
public class Trigger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Interval interval;

    @NotNull
    private Integer span;

    private Communication communication = Communication.NEUTRAL;

    public Trigger(Interval interval, Integer span) {
        this.interval = interval;
        this.span = span;
    }
}