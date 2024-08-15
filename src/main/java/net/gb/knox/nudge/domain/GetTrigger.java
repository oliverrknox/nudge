package net.gb.knox.nudge.domain;

import java.util.Optional;

public record GetTrigger(Long id, Period period, Integer span, Optional<Communication> communication) {
}
