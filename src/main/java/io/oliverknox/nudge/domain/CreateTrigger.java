package io.oliverknox.nudge.domain;

import java.util.Optional;

public record CreateTrigger(Period period, Integer span, Optional<Communication> communication) {
}
