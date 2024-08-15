package net.gb.knox.nudge.domain;

import java.time.LocalDate;
import java.util.List;

public record GetNudge(Long id, String userId, String title, String description, LocalDate due,
                       List<GetTrigger> triggers) {
}
