package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.userdata.UserdataUserEntity;

import java.util.UUID;

public record UserdataUserJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("username")
    String username,
    @JsonProperty("currency")
    CurrencyValues currency,
    @JsonProperty("firstname")
    String firstname,
    @JsonProperty("surname")
    String surname,
    @JsonProperty("full_name")
    String fullName,
    @JsonProperty("photo")
    byte[] photo,
    @JsonProperty("photo_small")
    byte[] photoSmall

) {
    public static UserdataUserJson fromEntity(UserdataUserEntity entity) {
        return new UserdataUserJson(
            entity.getId(),
            entity.getUsername(),
            entity.getCurrency(),
            entity.getFirstname(),
            entity.getSurname(),
            entity.getFullName(),
            entity.getPhoto(),
            entity.getPhotoSmall()
        );
    }
}
