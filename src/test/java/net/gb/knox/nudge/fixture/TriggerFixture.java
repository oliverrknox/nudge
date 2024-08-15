package net.gb.knox.nudge.fixture;

import net.gb.knox.nudge.domain.Communication;
import net.gb.knox.nudge.domain.Period;
import net.gb.knox.nudge.model.Trigger;

public class TriggerFixture {

    public static final Trigger TRIGGER = new Trigger(1L, Period.MONTH, 1, Communication.ASSERTIVE);

    public static final Trigger TRIGGER_2 = new Trigger(1L, Period.MONTH, 1, Communication.ASSERTIVE);
}
