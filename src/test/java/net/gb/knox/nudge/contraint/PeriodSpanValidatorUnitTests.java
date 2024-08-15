package net.gb.knox.nudge.contraint;

import jakarta.validation.ConstraintValidatorContext;
import net.gb.knox.nudge.constraint.PeriodSpanValidator;
import net.gb.knox.nudge.domain.Period;
import net.gb.knox.nudge.model.Trigger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class PeriodSpanValidatorUnitTests {

    private PeriodSpanValidator validator;
    private ConstraintValidatorContext context;

    public static Stream<Arguments> isValidParameterProvider() {
        var periodDayAndSpanZero = Arguments.of(Period.DAY, 0, false);
        var periodDayAndSpanOne = Arguments.of(Period.DAY, 1, true);
        var periodDayAndSpanSeven = Arguments.of(Period.DAY, 7, true);
        var periodDayAndSpanEight = Arguments.of(Period.DAY, 8, false);

        var periodWeekAndSpanZero = Arguments.of(Period.WEEK, 0, false);
        var periodWeekAndSpanOne = Arguments.of(Period.WEEK, 1, true);
        var periodWeekAndSpanFour = Arguments.of(Period.WEEK, 4, true);
        var periodWeekAndSpanFive = Arguments.of(Period.WEEK, 5, false);

        var periodMonthAndSpanZero = Arguments.of(Period.MONTH, 0, false);
        var periodMonthAndSpanOne = Arguments.of(Period.MONTH, 1, true);
        var periodMonthAndSpanFour = Arguments.of(Period.MONTH, 12, true);
        var periodMonthAndSpanFive = Arguments.of(Period.MONTH, 13, false);

        return Stream.of(periodDayAndSpanZero, periodDayAndSpanOne, periodDayAndSpanSeven,
                periodDayAndSpanEight, periodWeekAndSpanZero, periodWeekAndSpanOne, periodWeekAndSpanFour,
                periodWeekAndSpanFive, periodMonthAndSpanZero, periodMonthAndSpanOne, periodMonthAndSpanFour,
                periodMonthAndSpanFive);
    }

    @BeforeEach
    public void setUp() {
        validator = new PeriodSpanValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @ParameterizedTest
    @MethodSource("isValidParameterProvider")
    public void testIsValid(Period period, Integer span, boolean expectedIsValid) {
        var trigger = new Trigger(period, span);

        var isValid = validator.isValid(trigger, context);

        assertEquals(expectedIsValid, isValid);
    }
}
