package net.gb.knox.nudge.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PeriodSpanValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PeriodSpan {

    String message() default "Span must be within boundaries for period";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
