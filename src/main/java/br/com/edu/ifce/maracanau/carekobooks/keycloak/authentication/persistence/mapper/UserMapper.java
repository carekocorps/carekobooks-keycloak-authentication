package br.com.edu.ifce.maracanau.carekobooks.keycloak.authentication.persistence.mapper;

import br.com.edu.ifce.maracanau.carekobooks.keycloak.authentication.persistence.model.User;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserMapper {

    private UserMapper() {
    }

    public static User from(UUID keycloakId, String username) {
        var now = LocalDateTime.now();
        return User
                .builder()
                .keycloakId(keycloakId)
                .username(username)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

}
