package ru.kolpakovee.taskservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.kolpakovee.taskservice.models.RuleDto;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "${integration.services.rules-service.name}",
        url = "${integration.services.rules-service.url}")
public interface RulesServiceClient {

    @GetMapping("/api/v1/rules/{apartmentId}")
    List<RuleDto> getApartmentRules(@PathVariable UUID apartmentId);
}
