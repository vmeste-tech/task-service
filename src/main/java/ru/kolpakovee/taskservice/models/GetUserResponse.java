package ru.kolpakovee.taskservice.models;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Value
@Builder
@Jacksonized
public class GetUserResponse {

    @NotNull
    String id;

    @NotEmpty
    String username;

    @Email
    String email;

    @NotEmpty
    String firstName;

    @NotEmpty
    String lastName;

    @Nullable
    String profilePictureUrl;

    LocalDateTime createdAt;
}