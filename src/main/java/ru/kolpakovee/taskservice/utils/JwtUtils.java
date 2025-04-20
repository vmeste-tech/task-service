package ru.kolpakovee.taskservice.utils;

import lombok.experimental.UtilityClass;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

@UtilityClass
public class JwtUtils {

    public UUID getUserId(Jwt jwt) {
        String userId = jwt.getSubject();
        return UUID.fromString(userId);
    }
}
