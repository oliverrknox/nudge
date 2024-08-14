package net.gb.knox.nudge.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = IntervalSpanValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface IntervalSpan {

    String message() default "Span must be within boundaries for interval";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
