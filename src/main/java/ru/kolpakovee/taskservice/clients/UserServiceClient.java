package ru.kolpakovee.taskservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.kolpakovee.taskservice.models.GetUserResponse;

import java.util.UUID;

@FeignClient(name = "${integration.services.user-service.name}",
        url = "${integration.services.user-service.url}")
public interface UserServiceClient {

    @GetMapping("/api/users/v1/{id}")
    GetUserResponse getUserById(@PathVariable("id") UUID id);
}
