package guru.qa.niffler.data;

import guru.qa.niffler.config.Config;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Databases {
    private Databases() {
    }

    private static final Config CFG = Config.getInstance();
    private static final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();

    private static DataSource dataSource(String jdbcUrl) {
        return dataSources.computeIfAbsent(
                jdbcUrl,
                key -> {
                    PGSimpleDataSource ds = new PGSimpleDataSource();
                    ds.setUser(CFG.dbUsername());
                    ds.setPassword(CFG.dbPassword());
                    ds.setUrl(key);
                    return ds;
                }
        );
    }

    public static Connection connection(String jdbcUrl) throws SQLException {
        return dataSource(jdbcUrl).getConnection();
    }
}