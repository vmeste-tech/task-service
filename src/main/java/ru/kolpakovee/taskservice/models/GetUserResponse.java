package ru.kolpakovee.taskservice.models;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ru.kolpakovee.taskservice.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record GetUserResponse(
        @NotNull UUID id,
        @Email String email,
        @NotEmpty
        String firstName,
        @NotEmpty
        String lastName,
        @Nullable String profilePictureUrl,
        LocalDateTime createdAt,
        @NotNull UserStatus status
) {
}
