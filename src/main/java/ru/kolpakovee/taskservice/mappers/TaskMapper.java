package ru.kolpakovee.taskservice.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import ru.kolpakovee.taskservice.entities.TaskEntity;
import ru.kolpakovee.taskservice.enums.TaskStatus;
import ru.kolpakovee.taskservice.models.CreateTaskRequest;
import ru.kolpakovee.taskservice.models.TaskDto;

@Mapper
public interface TaskMapper {
    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    TaskDto toDto(TaskEntity taskEntity);

    @Mapping(target = "status", ignore = true) // Игнорируем статус при основном маппинге
    TaskEntity toEntity(CreateTaskRequest request);

    @AfterMapping
    default void setDefaultStatus(@MappingTarget TaskEntity taskEntity) {
        if (taskEntity.getStatus() == null) {
            taskEntity.setStatus(TaskStatus.CREATED);
        }
    }
}
