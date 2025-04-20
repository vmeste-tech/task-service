package ru.kolpakovee.taskservice.models;

import lombok.Builder;
import ru.kolpakovee.taskservice.enums.NotificationCategory;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record NotificationEvent(
        UUID userId,
        String message,
        NotificationCategory category,
        LocalDateTime timestamp
) {
}
