package com.travixa.common;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Component
public class DateTimeProvider {
    private final Clock clock;

    public DateTimeProvider() {
        this(Clock.systemDefaultZone());
    }

    public DateTimeProvider(Clock clock) {
        this.clock = clock;
    }

    public Instant now() {
        return Instant.now(clock);
    }

    public LocalDate today() {
        return LocalDate.now(clock);
    }

    public ZoneId zoneId() {
        return clock.getZone();
    }
}
