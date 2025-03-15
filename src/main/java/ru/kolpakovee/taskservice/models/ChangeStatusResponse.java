package ru.kolpakovee.taskservice.models;

import ru.kolpakovee.taskservice.enums.TaskStatus;

import java.util.UUID;

public record ChangeStatusResponse(
        UUID taskId,
        TaskStatus status
) {
}
