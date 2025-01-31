package ru.kolpakovee.taskservice.entities;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kolpakovee.taskservice.enums.TaskCategory;
import ru.kolpakovee.taskservice.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Size(max = 100)
    private String title;

    @Size(max = 500)
    private String description;

    @Enumerated(value = EnumType.STRING)
    private TaskStatus status = TaskStatus.CREATED;

    @FutureOrPresent
    private LocalDateTime deadline;

    @NotNull
    private UUID apartmentId;

    @NotNull
    private UUID createdBy;

    @Nullable
    private UUID assignedTo;

    @Enumerated(value = EnumType.STRING)
    private TaskCategory category;
}
