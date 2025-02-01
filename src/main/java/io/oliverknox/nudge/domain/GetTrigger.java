package io.oliverknox.nudge.domain;

import java.util.Optional;

public record GetTrigger(Long id, Period period, Integer span, Optional<Communication> communication) {
}
