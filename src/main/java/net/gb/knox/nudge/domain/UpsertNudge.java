package net.gb.knox.nudge.domain;

import java.time.LocalDate;
import java.util.List;

public record UpsertNudge(String title, String description, LocalDate due, List<CreateTrigger> triggers) {
}
