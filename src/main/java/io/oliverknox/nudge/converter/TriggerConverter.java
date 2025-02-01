package io.oliverknox.nudge.converter;

import io.oliverknox.nudge.domain.Communication;
import io.oliverknox.nudge.domain.CreateTrigger;
import io.oliverknox.nudge.domain.GetTrigger;
import io.oliverknox.nudge.model.Trigger;

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
