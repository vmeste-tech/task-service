package ru.kolpakovee.taskservice.models;

import java.time.LocalDateTime;

public record TaskCreateRequest(
        String title,
        String description,
        LocalDateTime deadline,
        Long responsibleUserId
) {
}
