package guru.qa.niffler.data.entity;

import guru.qa.niffler.model.AuthAuthorityJson;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AuthAuthorityEntity {
    private UUID id;
    private Authority authority;
    private AuthUserEntity user;

    public static AuthAuthorityEntity fromJson(AuthAuthorityJson json) {
        AuthAuthorityEntity entity = new AuthAuthorityEntity();
        entity.setAuthority(json.authority());
        entity.setUser(AuthUserEntity.fromJson(json.user()));
        return entity;
    }
}
