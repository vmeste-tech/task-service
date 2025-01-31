package ru.kolpakovee.taskservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kolpakovee.taskservice.clients.UserServiceClient;
import ru.kolpakovee.taskservice.models.GetUserResponse;

import java.util.UUID;

@RestController
@RequestMapping("/api/tasks/v1")
@RequiredArgsConstructor
public class TaskController {

    private final UserServiceClient client;

    @GetMapping("/user/{userId}")
    public GetUserResponse getTask(@PathVariable UUID userId) {
        return client.getUserById(userId);
    }
}
