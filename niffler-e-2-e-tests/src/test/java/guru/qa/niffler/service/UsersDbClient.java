package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.AuthUserEntity;
import guru.qa.niffler.data.entity.Authority;
import guru.qa.niffler.data.entity.UserdataUserEntity;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.UserdataUserJson;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.List;

public class UsersDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserDao authUserDao = new AuthUserDaoJdbc();
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoJdbc();
    private final UserdataUserDao userdataUserDao = new UserdataUserDaoSpringJdbc();

    private final TransactionTemplate txTemplate = new TransactionTemplate(
        new JdbcTransactionManager(
            DataSources.dataSource(CFG.authJdbcUrl())
        )
    );

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
        CFG.authJdbcUrl(),
        CFG.userdataJdbcUrl()
    );

    public UserdataUserJson createUserSpringJdbc(UserdataUserJson user) {
        return xaTransactionTemplate.execute(() -> {
            AuthUserEntity authUser = new AuthUserEntity();
            authUser.setUsername(user.username());
            authUser.setPassword(pe.encode("12345"));
            authUser.setEnabled(true);
            authUser.setAccountNonExpired(true);
            authUser.setAccountNonLocked(true);
            authUser.setCredentialsNonExpired(true);

            authUserDao.create(authUser);

            List<AuthAuthorityEntity> authorities = Arrays.stream(Authority.values()).map(
                authority -> {
                    AuthAuthorityEntity userAuthority = new AuthAuthorityEntity();
                    userAuthority.setAuthority(authority);
                    userAuthority.setUser(authUser);
                    return userAuthority;
                }
            ).toList();

            authAuthorityDao.create(authorities);
            return UserdataUserJson.fromEntity(
                userdataUserDao.create(UserdataUserEntity.fromJson(user))
            );
        });
    }
}
