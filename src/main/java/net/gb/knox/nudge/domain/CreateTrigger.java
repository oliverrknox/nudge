package net.gb.knox.nudge.domain;

import java.util.Optional;

public record CreateTrigger(Period period, Integer span, Optional<Communication> communication) {
}
