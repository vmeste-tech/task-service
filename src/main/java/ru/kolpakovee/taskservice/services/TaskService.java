package ru.kolpakovee.taskservice.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kolpakovee.taskservice.entities.TaskEntity;
import ru.kolpakovee.taskservice.mappers.TaskMapper;
import ru.kolpakovee.taskservice.models.CreateTaskRequest;
import ru.kolpakovee.taskservice.models.TaskDto;
import ru.kolpakovee.taskservice.repositories.TaskRepository;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    @Transactional
    public TaskDto create(CreateTaskRequest request) {
        TaskEntity taskEntity = TaskMapper.INSTANCE.toEntity(request);
        taskEntity = taskRepository.save(taskEntity);

        return TaskMapper.INSTANCE.toDto(taskEntity);
    }
}
