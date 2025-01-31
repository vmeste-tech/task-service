package ru.kolpakovee.taskservice.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.kolpakovee.taskservice.enums.TaskCategory;
import ru.kolpakovee.taskservice.enums.TaskStatus;
import ru.kolpakovee.taskservice.models.CreateTaskRequest;
import ru.kolpakovee.taskservice.models.TaskDto;
import ru.kolpakovee.taskservice.services.TaskService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Task Management", description = "API для управления задачами")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public TaskDto create(@RequestBody @Valid CreateTaskRequest request) {
        return taskService.create(request);
    }

//    @PatchMapping("/{taskId}/status")
//    public ChangeStatucResponse changeStatus(@PathVariable UUID taskId, @RequestParam TaskStatus status) {
//        return taskService.changeStatus(taskId, status);
//    }
//
//    @GetMapping("/{apartmentId}")
//    public List<TaskDto> getTasks(@PathVariable UUID apartmentId,
//                                  @RequestParam(required = false) TaskStatus status,
//                                  @RequestParam(required = false) TaskCategory category) {
//        return taskService.getTasks();
//    }
//
//    @GetMapping("/{taskId}/history")
//    public List<TaskHistoryDto> getTaskHistory(@PathVariable UUID taskId) {
//        return taskHistoryService.getHistory(taskId);
//    }
}
