package io.oliverknox.nudge.fixture;

import io.oliverknox.nudge.model.Nudge;

import java.time.LocalDate;
import java.util.List;

public abstract class NudgeFixture {

    public static final String USER_ID = "d30d4015-9985-41d5-a201-b9575db5d239";

    public static final Long MISSING_ID = 999L;

    public static final String MISSING_USER_ID = "9614bcb6-46cc-47c5-a5e4-788f0d3d8816";

    public static final Nudge NUDGE = new Nudge(1L, "d30d4015-9985-41d5-a201-b9575db5d239", "Title",
            "Description", LocalDate.of(2025, 1, 1), List.of(TriggerFixture.TRIGGER));

    public static final Nudge NUDGE_2 = new Nudge(2L, USER_ID, "Title", "Description",
            LocalDate.of(2026, 1, 1), List.of(TriggerFixture.TRIGGER_2));

    public static final List<Nudge> NUDGES = List.of(NUDGE, NUDGE_2);
}
