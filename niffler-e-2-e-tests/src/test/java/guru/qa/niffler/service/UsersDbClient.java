package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.userdata.UserdataUserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.AuthUserRepositorySpringJdbc;
import guru.qa.niffler.data.repository.impl.UserdataUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.UserdataUserRepositorySpringJdbc;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.UserdataUserJson;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;

public class UsersDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepSpring = new AuthUserRepositorySpringJdbc();
    private final UserdataUserRepository userdataUserRepSpring = new UserdataUserRepositorySpringJdbc();

    private final AuthUserRepository authUserRep = new AuthUserRepositoryJdbc();
    private final UserdataUserRepository userdataUserRep = new UserdataUserRepositoryJdbc();

    private final TransactionTemplate txTemplate = new TransactionTemplate(
            new JdbcTransactionManager(
                DataSources.dataSource(CFG.authJdbcUrl())
            )
    );

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
        CFG.authJdbcUrl(),
        CFG.userdataJdbcUrl()
    );

    public UserdataUserJson createUser(UserdataUserJson user) {
        return xaTransactionTemplate.execute(() -> {
            AuthUserEntity authUser = new AuthUserEntity();
            authUser.setUsername(user.username());
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

            authUserRepSpring.create(authUser);
            return UserdataUserJson.fromEntity(
                userdataUserRepSpring.create(UserdataUserEntity.fromJson(user))
            );
        });
    }

    public UserdataUserJson createUserJdbc(UserdataUserJson user) {
        return xaTransactionTemplate.execute(() -> {
            AuthUserEntity authUser = new AuthUserEntity();
            authUser.setUsername(user.username());
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

            authUserRep.create(authUser);
            return UserdataUserJson.fromEntity(
                userdataUserRep.create(UserdataUserEntity.fromJson(user))
            );
        });
    }
}
