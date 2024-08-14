package net.gb.knox.nudge.constraint;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import net.gb.knox.nudge.domain.Interval;
import net.gb.knox.nudge.model.Trigger;

public class IntervalSpanValidator implements ConstraintValidator<IntervalSpan, Trigger> {

    @Override
    public boolean isValid(Trigger trigger, ConstraintValidatorContext context) {
        var span = trigger.getSpan();
        var maxSpan = switch (trigger.getInterval()) {
            case Interval.DAY -> 7;
            case Interval.WEEK -> 4;
            case Interval.MONTH -> 12;
        };

        return span <= maxSpan && span >= 1;
    }
}
