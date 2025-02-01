package io.oliverknox.nudge.converter;

import io.oliverknox.nudge.domain.GetNudge;
import io.oliverknox.nudge.model.Nudge;

public abstract class NudgeConverter {

    public static GetNudge convert(Nudge nudge) {
        return new GetNudge(nudge.getId(), nudge.getUserId(), nudge.getTitle(), nudge.getDescription(), nudge.getDue(),
                nudge.getTriggers().stream().map(TriggerConverter::convert).toList());
    }
}
