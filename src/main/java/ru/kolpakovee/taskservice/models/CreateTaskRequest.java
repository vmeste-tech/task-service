package ru.kolpakovee.taskservice.models;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateTaskRequest(

        @NotBlank
        @Size(max = 100)
        String title,

        @Size(max = 500)
        String description,

        @FutureOrPresent
        LocalDateTime deadline,

        @NotNull
        UUID apartmentId,

        @NotNull
        UUID createdBy,

        UUID assignedTo
) {
}
