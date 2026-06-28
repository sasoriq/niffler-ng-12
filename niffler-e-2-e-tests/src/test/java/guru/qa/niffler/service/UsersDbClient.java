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
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserdataUserJson;
import guru.qa.niffler.utils.RandomDataUtils;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;

public class UsersDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepSpring = new AuthUserRepositoryHibernate();
    private final UserdataUserRepository userdataUserRepSpring = new UserdataUserRepositoryHibernate();

    private final TransactionTemplate txTemplate = new TransactionTemplate(
            new JdbcTransactionManager(
                DataSources.dataSource(CFG.authJdbcUrl())
            )
    );

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
        CFG.authJdbcUrl(),
        CFG.userdataJdbcUrl()
    );

    public UserdataUserJson createUser(String username) {
        return xaTransactionTemplate.execute(() -> {
            AuthUserEntity authUser = authUserEntity(username);

            authUserRepSpring.create(authUser);
            return UserdataUserJson.fromEntity(
                userdataUserRepSpring.create(userEntity(username))
            );
        });
    }

    public void addIncomeInvitation(UserdataUserJson targetUser, int count) {
        if (count > 0) {
            UserdataUserEntity targetEntity = userdataUserRepSpring
                .findById(targetUser.id())
                .orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                    String username = RandomDataUtils.randomUsername();
                    AuthUserEntity authUser = authUserEntity(username);
                    authUserRepSpring.create(authUser);
                    UserdataUserEntity addressee = userdataUserRepSpring.create(userEntity(username));
                    userdataUserRepSpring.addIncomeInvitation(targetEntity, addressee);
                    return null;
                });
            }
        }
    }

    public void addOutcomeInvitation(UserdataUserJson targetUser, int count) {
        if (count > 0) {
            UserdataUserEntity targetEntity = userdataUserRepSpring
                .findById(targetUser.id())
                .orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                    String username = RandomDataUtils.randomUsername();
                    AuthUserEntity authUser = authUserEntity(username);
                    authUserRepSpring.create(authUser);
                    UserdataUserEntity addressee = userdataUserRepSpring.create(userEntity(username));
                    userdataUserRepSpring.addOutcomeInvitation(targetEntity, addressee);
                    return null;
                });
            }
        }
    }

    void addFriend(UserdataUserJson targetUser, int count) {

    }

    private UserdataUserEntity userEntity(String username) {
        UserdataUserEntity entity = new UserdataUserEntity();
        entity.setUsername(username);
        entity.setCurrency(CurrencyValues.RUB);
        return entity;
    }

    public AuthUserEntity authUserEntity(String username) {
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
