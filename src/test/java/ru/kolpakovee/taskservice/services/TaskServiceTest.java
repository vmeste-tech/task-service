package ru.kolpakovee.taskservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kolpakovee.taskservice.clients.RulesServiceClient;
import ru.kolpakovee.taskservice.clients.UserServiceClient;
import ru.kolpakovee.taskservice.entities.TaskEntity;
import ru.kolpakovee.taskservice.enums.RuleStatus;
import ru.kolpakovee.taskservice.enums.TaskStatus;
import ru.kolpakovee.taskservice.enums.UserStatus;
import ru.kolpakovee.taskservice.models.GetUserResponse;
import ru.kolpakovee.taskservice.models.RuleDto;
import ru.kolpakovee.taskservice.models.TaskDto;
import ru.kolpakovee.taskservice.repositories.TaskRepository;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private RulesServiceClient rulesServiceClient;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private TaskService taskService;

    private UUID apartmentId;
    private LocalDate startDate;
    private LocalDate endDate;

    // Правило: каждый вторник в 10:00 UTC (21.03 и 28.03)
    private RuleDto ruleDto;

    // Второе правило: каждый четверг в 15:00 UTC (23.03 и 30.03)
    private RuleDto ruleDto2;

    // Два активных пользователя
    private List<GetUserResponse> activeUsers;

    @BeforeEach
    public void setup() {
        apartmentId = UUID.randomUUID();
        startDate = LocalDate.of(2023, 3, 21);
        endDate = LocalDate.of(2023, 3, 31);

        ruleDto = new RuleDto(
                UUID.randomUUID(),
                "Test Rule",
                "Test Rule Description",
                RuleStatus.ACCEPTED,
                0.0,
                "0 0 10 ? * TUE", // каждый вторник в 10:00 UTC
                "UTC"
        );

        ruleDto2 = new RuleDto(
                UUID.randomUUID(),
                "Test Rule 2",
                "Test Rule 2 Description",
                RuleStatus.ACCEPTED,
                0.0,
                "0 0 15 ? * THU", // каждый четверг в 15:00 UTC
                "UTC"
        );

        GetUserResponse user1 = GetUserResponse.builder()
                .id(UUID.randomUUID())
                .email("user1@example.com")
                .firstName("User")
                .lastName("One")
                .createdAt(LocalDateTime.now())
                .status(UserStatus.ACTIVE)
                .build();

        GetUserResponse user2 = GetUserResponse.builder()
                .id(UUID.randomUUID())
                .email("user2@example.com")
                .firstName("User")
                .lastName("Two")
                .createdAt(LocalDateTime.now())
                .status(UserStatus.ACTIVE)
                .build();

        activeUsers = Arrays.asList(user1, user2);

        // По умолчанию в репозитории нет задач для этой квартиры
        when(taskRepository.findByApartmentId(apartmentId)).thenReturn(Collections.emptyList());

        // Сервис правил возвращает правило по умолчанию (если не переопределено в конкретном тесте)
        when(rulesServiceClient.getApartmentRules(apartmentId)).thenReturn(List.of(ruleDto));

        // Сервис пользователей возвращает список активных пользователей
        when(userServiceClient.getApartmentUsers(apartmentId)).thenReturn(activeUsers);
    }

    /**
     * Тест: несколько правил, и в БД нет существующих задач.
     * Для ruleDto ожидаются задачи на 21 и 28 марта,
     * для ruleDto2 – на 23 и 30 марта.
     */
    @Test
    @DisplayName("Несколько правил, и в БД нет существующих задач")
    public void testGetTasksCreatesNewTasksWithMultipleRules_NoExistingTasks() {
        // Возвращаем оба правила из сервиса правил
        when(rulesServiceClient.getApartmentRules(apartmentId)).thenReturn(List.of(ruleDto, ruleDto2));
        // Репозиторий возвращает пустой список сущностей
        when(taskRepository.findByApartmentId(apartmentId)).thenReturn(Collections.emptyList());

        List<TaskDto> tasks = taskService.getTasks(apartmentId, startDate, endDate);

        // Ожидается 4 задачи (2 для каждого правила)
        assertEquals(4, tasks.size(), "Должно быть создано 4 задачи в сумме.");

        // Разбиваем задачи по правилам
        List<TaskDto> tasksRule1 = tasks.stream()
                .filter(task -> task.ruleId().equals(ruleDto.id()))
                .collect(Collectors.toList());
        List<TaskDto> tasksRule2 = tasks.stream()
                .filter(task -> task.ruleId().equals(ruleDto2.id()))
                .collect(Collectors.toList());

        assertEquals(2, tasksRule1.size(), "Для первого правила должно быть 2 задачи.");
        assertEquals(2, tasksRule2.size(), "Для второго правила должно быть 2 задачи.");

        tasksRule1.sort(Comparator.comparing(TaskDto::scheduledAt));
        tasksRule2.sort(Comparator.comparing(TaskDto::scheduledAt));

        ZonedDateTime expectedRule1First = ZonedDateTime.of(LocalDate.of(2023, 3, 21), LocalTime.of(10, 0), ZoneId.of("UTC"));
        ZonedDateTime expectedRule1Second = ZonedDateTime.of(LocalDate.of(2023, 3, 28), LocalTime.of(10, 0), ZoneId.of("UTC"));

        ZonedDateTime expectedRule2First = ZonedDateTime.of(LocalDate.of(2023, 3, 23), LocalTime.of(15, 0), ZoneId.of("UTC"));
        ZonedDateTime expectedRule2Second = ZonedDateTime.of(LocalDate.of(2023, 3, 30), LocalTime.of(15, 0), ZoneId.of("UTC"));

        // Проверяем ruleDto
        TaskDto r1Task1 = tasksRule1.get(0);
        TaskDto r1Task2 = tasksRule1.get(1);
        assertEquals(expectedRule1First, r1Task1.scheduledAt(), "Первая задача для ruleDto должна быть 21 марта 10:00 UTC.");
        assertEquals(expectedRule1Second, r1Task2.scheduledAt(), "Вторая задача для ruleDto должна быть 28 марта 10:00 UTC.");
        assertEquals(activeUsers.get(0).id(), r1Task1.assignedTo(), "Первая задача ruleDto назначается первому пользователю.");
        assertEquals(activeUsers.get(1).id(), r1Task2.assignedTo(), "Вторая задача ruleDto назначается второму пользователю.");

        // Проверяем ruleDto2
        TaskDto r2Task1 = tasksRule2.get(0);
        TaskDto r2Task2 = tasksRule2.get(1);
        assertEquals(expectedRule2First, r2Task1.scheduledAt(), "Первая задача для ruleDto2 должна быть 23 марта 15:00 UTC.");
        assertEquals(expectedRule2Second, r2Task2.scheduledAt(), "Вторая задача для ruleDto2 должна быть 30 марта 15:00 UTC.");
        assertEquals(activeUsers.get(0).id(), r2Task1.assignedTo(), "Первая задача ruleDto2 назначается первому пользователю.");
        assertEquals(activeUsers.get(1).id(), r2Task2.assignedTo(), "Вторая задача ruleDto2 назначается второму пользователю.");
    }

    /**
     * Тест: несколько правил, при этом для одного из правил уже существует задача.
     * Для ruleDto существует задача на 21 марта, тогда создаётся только новая задача для 28 марта.
     * Для ruleDto2 задач нет, создаются задачи на 23 и 30 марта.
     */
    @Test
    @DisplayName("Несколько правил, при этом для одного из правил уже существует задача")
    public void testGetTasksMultipleRules_WithExistingTasks() {
        // Возвращаем оба правила
        when(rulesServiceClient.getApartmentRules(apartmentId)).thenReturn(List.of(ruleDto, ruleDto2));

        // Создаем существующую задачу для ruleDto как TaskEntity (репозиторий возвращает сущности)
        ZonedDateTime rule1FirstOccurrence = ZonedDateTime.of(LocalDate.of(2023, 3, 21), LocalTime.of(10, 0), ZoneId.of("UTC"));
        TaskEntity existingTaskEntity = TaskEntity.builder()
                .id(UUID.randomUUID())
                .ruleId(ruleDto.id())
                .apartmentId(apartmentId)
                .title(ruleDto.name())
                .description(ruleDto.description())
                .status(TaskStatus.CREATED)
                .scheduledAt(rule1FirstOccurrence)
                .assignedTo(activeUsers.getFirst().id())
                .build();

        // Симулируем, что репозиторий возвращает уже существующую задачу (TaskEntity)
        when(taskRepository.findByApartmentId(apartmentId)).thenReturn(List.of(existingTaskEntity));

        List<TaskDto> tasks = taskService.getTasks(apartmentId, startDate, endDate);

        // Для ruleDto: 1 существующая (21 марта) + 1 новая (28 марта)
        // Для ruleDto2: 2 новые задачи (23 и 30 марта)
        // Итого 4 задачи
        assertEquals(4, tasks.size(), "Общее количество задач должно быть 4.");

        List<TaskDto> tasksRule1 = tasks.stream()
                .filter(task -> task.ruleId().equals(ruleDto.id()))
                .collect(Collectors.toList());
        List<TaskDto> tasksRule2 = tasks.stream()
                .filter(task -> task.ruleId().equals(ruleDto2.id()))
                .collect(Collectors.toList());

        assertEquals(2, tasksRule1.size(), "Для ruleDto должно быть 2 задачи (1 существующая и 1 новая).");
        assertEquals(2, tasksRule2.size(), "Для ruleDto2 должно быть 2 задачи.");

        tasksRule1.sort(Comparator.comparing(TaskDto::scheduledAt));
        tasksRule2.sort(Comparator.comparing(TaskDto::scheduledAt));

        ZonedDateTime expectedRule1Second = ZonedDateTime.of(LocalDate.of(2023, 3, 28), LocalTime.of(10, 0), ZoneId.of("UTC"));
        ZonedDateTime expectedRule2First = ZonedDateTime.of(LocalDate.of(2023, 3, 23), LocalTime.of(15, 0), ZoneId.of("UTC"));
        ZonedDateTime expectedRule2Second = ZonedDateTime.of(LocalDate.of(2023, 3, 30), LocalTime.of(15, 0), ZoneId.of("UTC"));

        // Проверяем ruleDto
        TaskDto r1Task1 = tasksRule1.get(0);
        TaskDto r1Task2 = tasksRule1.get(1);
        assertEquals(rule1FirstOccurrence, r1Task1.scheduledAt(), "Первая задача ruleDto должна быть 21 марта (существующая).");
        assertEquals(expectedRule1Second, r1Task2.scheduledAt(), "Вторая задача ruleDto должна быть 28 марта.");
        // Если для ruleDto уже существует задача с назначением первого пользователя, новая задача должна быть назначена второму пользователю.
        assertEquals(activeUsers.get(0).id(), r1Task1.assignedTo(), "Существующая задача ruleDto назначена первому пользователю.");
        assertEquals(activeUsers.get(1).id(), r1Task2.assignedTo(), "Новая задача ruleDto должна быть назначена второму пользователю.");

        // Проверяем ruleDto2
        TaskDto r2Task1 = tasksRule2.get(0);
        TaskDto r2Task2 = tasksRule2.get(1);
        assertEquals(expectedRule2First, r2Task1.scheduledAt(), "Первая задача ruleDto2 должна быть 23 марта.");
        assertEquals(expectedRule2Second, r2Task2.scheduledAt(), "Вторая задача ruleDto2 должна быть 30 марта.");
        // Распределение для ruleDto2 начинается с первого пользователя, так как задач ранее не было.
        assertEquals(activeUsers.get(0).id(), r2Task1.assignedTo(), "Первая задача ruleDto2 назначается первому пользователю.");
        assertEquals(activeUsers.get(1).id(), r2Task2.assignedTo(), "Вторая задача ruleDto2 назначается второму пользователю.");
    }

    @Test
    @DisplayName("Корректное распределение задач при наличии будущей задачи")
    public void testGetTasksWithExistingFutureTask() {
        // Возвращаем правило для обоих тестов (только одно правило в данном тесте)
        when(rulesServiceClient.getApartmentRules(apartmentId)).thenReturn(List.of(ruleDto));

        // Создаем существующую задачу для ruleDto на 28 марта 10:00 UTC, назначенную первому пользователю.
        ZonedDateTime futureOccurrence = ZonedDateTime.of(LocalDate.of(2023, 3, 28), LocalTime.of(10, 0), ZoneId.of("UTC"));
        TaskEntity existingTaskEntity = TaskEntity.builder()
                .id(UUID.randomUUID())
                .ruleId(ruleDto.id())
                .apartmentId(apartmentId)
                .title(ruleDto.name())
                .description(ruleDto.description())
                .status(TaskStatus.CREATED)
                .scheduledAt(futureOccurrence)
                .assignedTo(activeUsers.getFirst().id())  // Назначена первому пользователю
                .build();

        // Симулируем, что репозиторий возвращает уже существующую задачу
        when(taskRepository.findByApartmentId(apartmentId)).thenReturn(List.of(existingTaskEntity));

        // Выполняем метод получения задач для периода с 21 по 31 марта
        List<TaskDto> tasks = taskService.getTasks(apartmentId, startDate, endDate);

        // Для ruleDto с cron "0 0 10 ? * TUE" ожидаются два срабатывания:
        // 21 марта и 28 марта. При этом задача на 28 марта уже существует.
        // Таким образом, должна быть создана новая задача только для 21 марта.
        assertEquals(2, tasks.size(), "Должно быть 2 задачи (1 существующая и 1 новая).");

        // Разбиваем задачи по правилу
        List<TaskDto> tasksRule = tasks.stream()
                .filter(task -> task.ruleId().equals(ruleDto.id()))
                .toList();

        assertEquals(2, tasksRule.size(), "Для ruleDto должно быть 2 задачи.");

        ZonedDateTime expectedNewOccurrence = ZonedDateTime.of(LocalDate.of(2023, 3, 21), LocalTime.of(10, 0), ZoneId.of("UTC"));

        TaskDto newTask = tasksRule.get(0); // Новая задача для 21 марта
        TaskDto existingTask = tasksRule.get(1); // Уже существующая задача для 28 марта

        // Проверяем новую задачу на 21 марта
        assertEquals(expectedNewOccurrence, newTask.scheduledAt(), "Новая задача должна быть запланирована на 21 марта 10:00 UTC.");

        // Проверяем, что существующая задача не изменена
        assertEquals(futureOccurrence, existingTask.scheduledAt(), "Существующая задача должна быть запланирована на 28 марта 10:00 UTC.");
        assertEquals(activeUsers.get(0).id(), existingTask.assignedTo(), "Существующая задача должна оставаться назначенной первому пользователю.");
    }

}
