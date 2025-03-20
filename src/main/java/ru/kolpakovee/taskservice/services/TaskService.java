package ru.kolpakovee.taskservice.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kolpakovee.taskservice.clients.RulesServiceClient;
import ru.kolpakovee.taskservice.clients.UserServiceClient;
import ru.kolpakovee.taskservice.entities.TaskEntity;
import ru.kolpakovee.taskservice.enums.RuleStatus;
import ru.kolpakovee.taskservice.enums.TaskStatus;
import ru.kolpakovee.taskservice.enums.UserStatus;
import ru.kolpakovee.taskservice.mappers.TaskMapper;
import ru.kolpakovee.taskservice.models.*;
import ru.kolpakovee.taskservice.repositories.TaskRepository;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private final RulesServiceClient rulesServiceClient;
    private final UserServiceClient userServiceClient;

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

    public List<TaskDto> getTasks(UUID apartmentId, LocalDate startDate, LocalDate endDate) {
        List<RuleDto> rules = rulesServiceClient.getApartmentRules(apartmentId).stream()
                .filter(r -> r.status().equals(RuleStatus.ACCEPTED))
                .toList();

        List<GetUserResponse> activeUsers = userServiceClient.getApartmentUsers(apartmentId).stream()
                .filter(u -> u.status().equals(UserStatus.ACTIVE))
                .toList();

        List<TaskDto> tasks = new ArrayList<>(taskRepository.findByApartmentId(apartmentId).stream()
                .map(TaskMapper.INSTANCE::toDto)
                .toList());

        for (RuleDto rule : rules) {
            List<ZonedDateTime> occurrenceDates = rule.getOccurrenceDateTimes(startDate, endDate);
            Collections.sort(occurrenceDates);

            for (ZonedDateTime date : occurrenceDates) {
                if (!taskExists(tasks, rule.id(), date)) {
                    GetUserResponse assignedUser = assignUser(rule, date, activeUsers, tasks);
                    TaskDto newTask = TaskDto.builder()
                            .status(TaskStatus.CREATED)
                            .ruleId(rule.id())
                            .apartmentId(apartmentId)
                            .title(rule.name())
                            .assignedTo(assignedUser.id())
                            .scheduledAt(date)
                            .description(rule.description())
                            .build();
                    tasks.add(newTask);
                }
            }
        }

        tasks.sort(Comparator.comparing(TaskDto::scheduledAt));

        return tasks;
    }

    /**
     * Проверяет, существует ли уже задача для данного правила в указанное время.
     */
    private boolean taskExists(List<TaskDto> tasks, UUID ruleId, ZonedDateTime occurrence) {
        return tasks.stream()
                .anyMatch(task -> task.ruleId().equals(ruleId)
                        && task.scheduledAt().equals(occurrence));
    }

    /**
     * Динамическое распределение пользователей.
     * Логика:
     * 1. Находим последнюю задачу для данного правила (с scheduledAt равным или до currentOccurrence).
     * 2. Если такая задача найдена, определяем индекс пользователя, которому она назначена, как отправную точку.
     * 3. Считаем общее количество задач для данного правила.
     * 4. Вычисляем индекс следующего пользователя: (startIndex + tasksCountForRule) % activeUsers.size().
     */
    private GetUserResponse assignUser(RuleDto rule, ZonedDateTime currentOccurrence, List<GetUserResponse> activeUsers, List<TaskDto> existingTasks) {
        Optional<TaskDto> lastTaskOpt = existingTasks.stream()
                .filter(task -> task.ruleId().equals(rule.id())
                        && (task.scheduledAt().isEqual(currentOccurrence) || task.scheduledAt().isBefore(currentOccurrence)))
                .max(Comparator.comparing(TaskDto::scheduledAt));

        int startIndex = 0;
        if (lastTaskOpt.isPresent()) {
            UUID startingUser = lastTaskOpt.get().assignedTo();
            startIndex = IntStream.range(0, activeUsers.size())
                    .filter(i -> activeUsers.get(i).id().equals(startingUser))
                    .findFirst()
                    .orElse(0);
        }
        long tasksCountForRule = existingTasks.stream()
                .filter(task -> task.ruleId().equals(rule.id()))
                .count();
        int userIndex = (startIndex + (int) tasksCountForRule) % activeUsers.size();
        return activeUsers.get(userIndex);
    }
}

// Хочу узнать все задачи на текущий месяц
// правило: мыть полы по ПТ, пылесосить по ПНД
// пользователи: Е, Т
// задачи: помыть пол 21.03 Е, пылесосить 24 Т
// для каждого правила выписываем все дни за период времени
// мыть полы: 21 28 4 11, пылесосить 24 31 7 14

// list<task> tasks = getTasks()
// for (r: rules)
//    list<day> days = r.getDays()
//    for (d: days)
//       for (activeUser: users)
//          tasks.contains(r, d) ? skip : tasks.add()
//
