package guru.qa.niffler.data.entity;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserdataUserJson;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class UserdataUserEntity implements Serializable {
    private UUID id;
    private String username;
    private CurrencyValues currency;
    private String firstname;
    private String surname;
    private String fullName;
    private byte[] photo;
    private byte[] photoSmall;

    public static UserdataUserEntity fromJson(UserdataUserJson json) {
        UserdataUserEntity entity = new UserdataUserEntity();
        entity.setId(json.id());
        entity.setUsername(json.username());
        entity.setCurrency(json.currency());
        entity.setFirstname(json.firstname());
        entity.setSurname(json.surname());
        entity.setFullName(json.fullName());
        entity.setPhoto(json.photo() != null ? json.photo() : null);
        entity.setPhotoSmall(json.photoSmall() != null ? json.photoSmall() : null);
        return entity;
    }
}
