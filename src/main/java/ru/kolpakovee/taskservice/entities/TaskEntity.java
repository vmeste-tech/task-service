package ru.kolpakovee.taskservice.entities;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kolpakovee.taskservice.enums.TaskStatus;

import java.time.ZonedDateTime;
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
    @Builder.Default
    private TaskStatus status = TaskStatus.CREATED;

    @NotNull
    private UUID apartmentId;

    @Nullable
    private UUID ruleId;

    @Column(nullable = false)
    private ZonedDateTime scheduledAt;

    @Nullable
    private UUID assignedTo;

    @NotNull
    @Builder.Default
    @Column(name = "is_penalty_created")
    private boolean penaltyCreated = false;
}
