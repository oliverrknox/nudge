package net.gb.knox.nudge.converter;

import lombok.SneakyThrows;
import net.gb.knox.nudge.domain.CreateTrigger;
import net.gb.knox.nudge.domain.GetTrigger;
import net.gb.knox.nudge.exception.InvalidConversionException;
import net.gb.knox.nudge.model.Trigger;

import java.util.Optional;

public abstract class TriggerConverter {

    public static GetTrigger convert(Trigger trigger) {
        return new GetTrigger(trigger.getId(), trigger.getPeriod(), trigger.getSpan(),
                Optional.ofNullable(trigger.getCommunication()));
    }

    @SneakyThrows(InvalidConversionException.class)
    public static Trigger convert(CreateTrigger trigger) {
        if (trigger.communication().isEmpty()) {
            throw new InvalidConversionException("Communication property in Trigger must not be null");
        }
        return new Trigger(trigger.period(), trigger.span(), trigger.communication().get());
    }
}
