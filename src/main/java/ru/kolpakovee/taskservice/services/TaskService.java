package ru.kolpakovee.taskservice.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kolpakovee.taskservice.entities.TaskEntity;
import ru.kolpakovee.taskservice.enums.TaskCategory;
import ru.kolpakovee.taskservice.enums.TaskStatus;
import ru.kolpakovee.taskservice.mappers.TaskMapper;
import ru.kolpakovee.taskservice.models.ChangeStatusResponse;
import ru.kolpakovee.taskservice.models.CreateTaskRequest;
import ru.kolpakovee.taskservice.models.TaskDto;
import ru.kolpakovee.taskservice.repositories.TaskRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    @Transactional
    public TaskDto create(CreateTaskRequest request) {
        // TODO: может стоит проверить существование квартиры?
        TaskEntity taskEntity = TaskMapper.INSTANCE.toEntity(request);
        taskEntity = taskRepository.save(taskEntity);

        return TaskMapper.INSTANCE.toDto(taskEntity);
    }

    @Transactional
    public ChangeStatusResponse changeStatus(UUID taskId, TaskStatus status) {
        TaskEntity taskEntity = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Задача для обновления статуса не найдена."));

        taskEntity.setStatus(status);
        taskEntity = taskRepository.save(taskEntity);

        return new ChangeStatusResponse(taskEntity.getId(), taskEntity.getStatus());
    }

    public List<TaskDto> getTasks(UUID apartmentId, TaskStatus status, TaskCategory category) {
        throw new UnsupportedOperationException();
    }
}
