package ru.kolpakovee.taskservice.models;

import ru.kolpakovee.taskservice.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskDto(
        UUID id,
        String title,
        String description,
        TaskStatus status,
        LocalDateTime deadline,
        UUID apartmentId,
        UUID createdBy,
        UUID assignedTo,
        String category
) {
}
