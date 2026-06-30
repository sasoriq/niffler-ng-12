package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.userdata.UserdataUserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.UserdataUserRepositoryHibernate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserdataUserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

public class UsersDbClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRep = new AuthUserRepositoryHibernate();
    private final UserdataUserRepository userdataUserRep = new UserdataUserRepositoryHibernate();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
        CFG.authJdbcUrl(),
        CFG.userdataJdbcUrl()
    );

    @Override
    public UserdataUserJson createUser(String username) {
        return xaTransactionTemplate.execute(() ->
            UserdataUserJson.fromEntity(persistUser(username))
        );
    }

    @Override
    public void addIncomeInvitation(UserdataUserJson targetUser, int count) {
        if (count > 0) {
            UserdataUserEntity targetEntity = userdataUserRep
                .findById(targetUser.id())
                .orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                    UserdataUserEntity addressee = persistRandomUser();
                    userdataUserRep.addIncomeInvitation(targetEntity, addressee);
                    return null;
                });
            }
        }
    }

    @Override
    public void addOutcomeInvitation(UserdataUserJson targetUser, int count) {
        if (count > 0) {
            UserdataUserEntity targetEntity = userdataUserRep
                .findById(targetUser.id())
                .orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                    UserdataUserEntity addressee = persistRandomUser();
                    userdataUserRep.addOutcomeInvitation(targetEntity, addressee);
                    return null;
                });
            }
        }
    }

    @Override
    public void addFriend(UserdataUserJson targetUser, int count) {
        if (count > 0) {
            UserdataUserEntity targetEntity = userdataUserRep
                .findById(targetUser.id())
                .orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                    UserdataUserEntity addressee = persistRandomUser();
                    userdataUserRep.addFriend(targetEntity, addressee);
                    return null;
                });
            }
        }
    }

    private UserdataUserEntity persistUser(String username) {
        AuthUserEntity authUser = createAuthUserEntity(username);
        authUserRep.create(authUser);
        return userdataUserRep.create(createUserdataUserEntity(username));
    }

    private UserdataUserEntity persistRandomUser() {
        return persistUser(randomUsername());
    }

    private UserdataUserEntity createUserdataUserEntity(String username) {
        UserdataUserEntity entity = new UserdataUserEntity();
        entity.setUsername(username);
        entity.setCurrency(CurrencyValues.RUB);
        return entity;
    }

    public AuthUserEntity createAuthUserEntity(String username) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(username);
        authUser.setPassword(pe.encode("12345"));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        authUser.setAuthorities(
            Arrays.stream(Authority.values()).map(
                authority -> {
                    AuthAuthorityEntity userAuthority = new AuthAuthorityEntity();
                    userAuthority.setAuthority(authority);
                    userAuthority.setUser(authUser);
                    return userAuthority;
                }
            ).toList()
        );
        return authUser;
    }
}
