package net.gb.knox.nudge.converter;

import net.gb.knox.nudge.domain.GetNudge;
import net.gb.knox.nudge.model.Nudge;

public abstract class NudgeConverter {

    public static GetNudge convert(Nudge nudge) {
        return new GetNudge(nudge.getId(), nudge.getUserId(), nudge.getTitle(), nudge.getDescription(), nudge.getDue(),
                nudge.getTriggers().stream().map(TriggerConverter::convert).toList());
    }
}
