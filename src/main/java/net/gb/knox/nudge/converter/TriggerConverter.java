package net.gb.knox.nudge.converter;

import net.gb.knox.nudge.domain.Communication;
import net.gb.knox.nudge.domain.CreateTrigger;
import net.gb.knox.nudge.domain.GetTrigger;
import net.gb.knox.nudge.model.Trigger;

import java.util.Optional;

public abstract class TriggerConverter {

    public static GetTrigger convert(Trigger trigger) {
        return new GetTrigger(trigger.getId(), trigger.getPeriod(), trigger.getSpan(),
                Optional.ofNullable(trigger.getCommunication()));
    }

    public static Trigger convert(CreateTrigger trigger) {
        return new Trigger(trigger.period(), trigger.span(), trigger.communication().orElse(Communication.NEUTRAL));
    }
}
