package ru.kolpakovee.taskservice.models;


import com.cronutils.model.Cron;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import ru.kolpakovee.taskservice.enums.RuleStatus;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.cronutils.model.CronType.QUARTZ;
import static com.cronutils.model.definition.CronDefinitionBuilder.instanceDefinitionFor;

public record RuleDto(
        UUID id,
        String name,
        String description,
        RuleStatus status,
        double penaltyAmount,
        String cronExpression,
        String timeZone
) {
    /**
     * Вычисляет все временные метки (ZonedDateTime) в интервале [startDate, endDate],
     * когда правило должно сработать согласно cron-выражению.
     */
    public List<ZonedDateTime> getOccurrenceDateTimes(LocalDate startDate, LocalDate endDate) {
        CronDefinition cronDefinition = instanceDefinitionFor(QUARTZ);
        CronParser parser = new CronParser(cronDefinition);
        Cron cron = parser.parse(this.cronExpression);
        ExecutionTime executionTime = ExecutionTime.forCron(cron);

        List<ZonedDateTime> dateTimes = new ArrayList<>();
        ZoneId zoneId = (timeZone != null && !timeZone.isEmpty()) ? ZoneId.of(timeZone) : ZoneId.systemDefault();

        ZonedDateTime nextExecution = startDate.atStartOfDay(zoneId);
        Optional<ZonedDateTime> next = executionTime.nextExecution(nextExecution);
        while (next.isPresent() && !next.get().toLocalDate().isAfter(endDate)) {
            ZonedDateTime execution = next.get();
            dateTimes.add(execution);
            next = executionTime.nextExecution(execution);
        }
        return dateTimes;
    }
}
