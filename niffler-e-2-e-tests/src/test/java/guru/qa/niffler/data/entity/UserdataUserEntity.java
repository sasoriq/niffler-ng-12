package guru.qa.niffler.data.entity;

import guru.qa.niffler.model.CurrencyValues;
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
}
