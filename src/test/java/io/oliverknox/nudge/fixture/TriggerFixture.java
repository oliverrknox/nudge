package io.oliverknox.nudge.fixture;

import io.oliverknox.nudge.domain.Communication;
import io.oliverknox.nudge.domain.Period;
import io.oliverknox.nudge.model.Trigger;

public class TriggerFixture {

    public static final Trigger TRIGGER = new Trigger(1L, Period.MONTH, 1, Communication.ASSERTIVE);

    public static final Trigger TRIGGER_2 = new Trigger(1L, Period.MONTH, 1, Communication.ASSERTIVE);
}
