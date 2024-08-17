package net.gb.knox.nudge.constraint;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import net.gb.knox.nudge.domain.Period;
import net.gb.knox.nudge.model.Trigger;

public class PeriodSpanValidator implements ConstraintValidator<PeriodSpan, Trigger> {

    @Override
    public boolean isValid(Trigger trigger, ConstraintValidatorContext context) {
        var span = trigger.getSpan();
        var period = trigger.getPeriod();

        // Null values are treated as valid because responsibility is delegated to @NotNull annotation
        if (span == null || period == null) {
            return true;
        }

        var maxSpan = switch (trigger.getPeriod()) {
            case Period.DAY -> 7;
            case Period.WEEK -> 4;
            case Period.MONTH -> 12;
        };

        return span <= maxSpan && span >= 1;
    }
}
