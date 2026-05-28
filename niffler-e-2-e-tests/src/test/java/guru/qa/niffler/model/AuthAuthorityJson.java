package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.Authority;

import java.util.UUID;

public record AuthAuthorityJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("authority")
    Authority authority,
    @JsonProperty("user_id")
    AuthUserJson user
) {
    public static AuthAuthorityJson fromEntity(AuthAuthorityEntity entity) {
        return new AuthAuthorityJson(
            entity.getId(),
            entity.getAuthority(),
            AuthUserJson.fromEntity(entity.getUser())
        );
    }
}
