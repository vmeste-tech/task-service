package ru.kolpakovee.taskservice.models;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.kolpakovee.taskservice.enums.TaskStatus;

import java.time.ZonedDateTime;
import java.util.UUID;

public record CreateTaskRequest(
        @Size(max = 100) String title,
        @Size(max = 500) String description,
        TaskStatus status,
        @NotNull UUID apartmentId,
        @Nullable UUID ruleId,
        @NotNull ZonedDateTime scheduledAt,
        @Nullable UUID assignedTo
) {
}
