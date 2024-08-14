package net.gb.knox.nudge.contraint;

import jakarta.validation.ConstraintValidatorContext;
import net.gb.knox.nudge.constraint.IntervalSpanValidator;
import net.gb.knox.nudge.domain.Interval;
import net.gb.knox.nudge.model.Trigger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class IntervalSpanValidatorUnitTests {

    private IntervalSpanValidator validator;
    private ConstraintValidatorContext context;

    public static Stream<Arguments> isValidParameterProvider() {
        var intervalDayAndSpanZero = Arguments.of(Interval.DAY, 0, false);
        var intervalDayAndSpanOne = Arguments.of(Interval.DAY, 1, true);
        var intervalDayAndSpanSeven = Arguments.of(Interval.DAY, 7, true);
        var intervalDayAndSpanEight = Arguments.of(Interval.DAY, 8, false);

        var intervalWeekAndSpanZero = Arguments.of(Interval.WEEK, 0, false);
        var intervalWeekAndSpanOne = Arguments.of(Interval.WEEK, 1, true);
        var intervalWeekAndSpanFour = Arguments.of(Interval.WEEK, 4, true);
        var intervalWeekAndSpanFive = Arguments.of(Interval.WEEK, 5, false);

        var intervalMonthAndSpanZero = Arguments.of(Interval.MONTH, 0, false);
        var intervalMonthAndSpanOne = Arguments.of(Interval.MONTH, 1, true);
        var intervalMonthAndSpanFour = Arguments.of(Interval.MONTH, 12, true);
        var intervalMonthAndSpanFive = Arguments.of(Interval.MONTH, 13, false);

        return Stream.of(intervalDayAndSpanZero, intervalDayAndSpanOne, intervalDayAndSpanSeven,
                intervalDayAndSpanEight, intervalWeekAndSpanZero, intervalWeekAndSpanOne, intervalWeekAndSpanFour,
                intervalWeekAndSpanFive, intervalMonthAndSpanZero, intervalMonthAndSpanOne, intervalMonthAndSpanFour,
                intervalMonthAndSpanFive);
    }

    @BeforeEach
    public void setUp() {
        validator = new IntervalSpanValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @ParameterizedTest
    @MethodSource("isValidParameterProvider")
    public void testIsValid(Interval interval, Integer span, boolean expectedIsValid) {
        var trigger = new Trigger(interval, span);

        var isValid = validator.isValid(trigger, context);

        assertEquals(expectedIsValid, isValid);
    }
}
