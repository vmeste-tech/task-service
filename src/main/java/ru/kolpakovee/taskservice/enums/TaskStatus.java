package ru.kolpakovee.taskservice.enums;

import ru.kolpakovee.taskservice.models.TaskDto;

public enum TaskStatus {
    CREATED, IN_PROGRESS, COMPLETED, CANCELED, OVERDUE;

    public static boolean isOverdue(TaskDto t) {
        return t.status().equals(CREATED) ||
                t.status().equals(IN_PROGRESS) ||
                t.status().equals(OVERDUE);
    }
}
