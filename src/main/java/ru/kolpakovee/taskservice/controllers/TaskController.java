package ru.kolpakovee.taskservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.kolpakovee.taskservice.enums.TaskStatus;
import ru.kolpakovee.taskservice.models.ChangeStatusResponse;
import ru.kolpakovee.taskservice.models.CreateTaskRequest;
import ru.kolpakovee.taskservice.models.TaskDto;
import ru.kolpakovee.taskservice.services.TaskService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Управление задачами", description = "API для управления задачами")
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/{apartmentId}/all")
    @Operation(summary = "Получение списка задач",
            description = "Позволяет получить задачи исходя из текущих статусов правил и проживающих")
    public List<TaskDto> getTasks(@PathVariable UUID apartmentId,
                                  @RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return taskService.getTasks(apartmentId, startDate, endDate);
    }

    @GetMapping("/{apartmentId}/overdue")
    @Operation(summary = "Получение списка просроченных задач",
            description = "Позволяет получить список просроченных задач")
    public List<TaskDto> getOverdueTasks(@PathVariable UUID apartmentId) {
        return taskService.getOverdueTasks(apartmentId);
    }


    @PostMapping
    @Operation(summary = "Создание задачи", description = "Позволяет создать задачу")
    public TaskDto create(@RequestBody @Valid CreateTaskRequest request) {
        return taskService.create(request);
    }

    @PatchMapping("/{taskId}/status")
    @Operation(summary = "Изменение статуса задачи", description = "Позволяет создать задачу")
    public ChangeStatusResponse changeStatus(@PathVariable UUID taskId, @RequestParam TaskStatus status) {
        return taskService.changeStatus(taskId, status);
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление задачи",
            description = "Позволяет удалить задачу")
    public void deleteTask(@PathVariable UUID taskId) {
        taskService.deleteTask(taskId);
    }

//    @GetMapping("/{taskId}/history")
//    public List<TaskHistoryDto> getTaskHistory(@PathVariable UUID taskId) {
//        return taskHistoryService.getHistory(taskId);
//    }
}
