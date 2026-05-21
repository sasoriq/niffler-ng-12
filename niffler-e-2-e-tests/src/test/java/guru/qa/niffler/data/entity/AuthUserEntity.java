package guru.qa.niffler.data.entity;

import guru.qa.niffler.model.AuthUserJson;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class AuthUserEntity implements Serializable {
    private UUID id;
    private String username;
    private String password;
    private Boolean enabled;
    private Boolean accountNonExpired;
    private Boolean accountNonLocked;
    private Boolean credentialsNonExpired;

    public static AuthUserEntity fromJson(AuthUserJson json) {
        AuthUserEntity entity = new AuthUserEntity();
        entity.setUsername(json.username());
        entity.setPassword(json.password());
        entity.setEnabled(json.enabled());
        entity.setAccountNonExpired(json.accountNonExpired());
        entity.setAccountNonLocked(json.accountNonLocked());
        entity.setCredentialsNonExpired(json.credentialsNonExpired());
        return entity;
    }
}
