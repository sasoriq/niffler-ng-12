package guru.qa.niffler.data;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class Databases {
    private Databases() {
    }

    private static final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();
    private static final Map<Long, Map<String, Connection>> threadConnections = new ConcurrentHashMap<>();

    public record XaFunction<T>(Function<Connection, T> function, String jdbcUrl) {}
    public record XaConsumer(Consumer<Connection> consumer, String jdbcUrl) {}

    public static <T> T transaction(Function<Connection, T> function, String jdbcUrl) {
        return transaction(Connection.TRANSACTION_READ_COMMITTED, function, jdbcUrl);
    }

    public static <T> T transaction(int isolationLevel, Function<Connection, T> function, String jdbcUrl) {
        Connection connection = null;
        try {
            connection = connection(jdbcUrl);
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(isolationLevel);
            T result = function.apply(connection);
            connection.commit();
            return result;
        } catch (Throwable e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    e.addSuppressed(ex);
                }
            }
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException ignored) {
                    // NOP
                }
            }
        }
    }

    @SafeVarargs
    public static <T> T xaTransaction(XaFunction<T>... actions) {
        return xaTransaction(Connection.TRANSACTION_READ_COMMITTED, actions);
    }

    @SafeVarargs
    public static <T> T xaTransaction(int isolationLevel, XaFunction<T>... actions) {
        UserTransaction ut = new UserTransactionImp();
        try {
            ut.begin();
            T result = null;
            for (XaFunction<T> action : actions) {
                try (Connection connection = connection(action.jdbcUrl)) {
                    connection.setTransactionIsolation(isolationLevel);
                    result = action.function.apply(connection);
                }
            }
            ut.commit();
            return result;
        } catch (Throwable e) {
            try {
                ut.rollback();
            } catch (SystemException ex) {
                e.addSuppressed(ex);
            }
            throw new RuntimeException(e);
        }
    }

    public static void transaction(Consumer<Connection> consumer, String jdbcUrl) {
        transaction(Connection.TRANSACTION_READ_COMMITTED, consumer, jdbcUrl);
    }

    public static void transaction(int isolationLevel, Consumer<Connection> consumer, String jdbcUrl) {
        Connection connection = null;
        try {
            connection = connection(jdbcUrl);
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(isolationLevel);
            consumer.accept(connection);
            connection.commit();
        } catch (Throwable e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    e.addSuppressed(ex);
                }
            }
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException ignored) {
                    // NOP
                }
            }
        }
    }

    public static void xaTransaction(XaConsumer... actions) {
        xaTransaction(Connection.TRANSACTION_READ_COMMITTED, actions);
    }

    public static void xaTransaction(int isolationLevel, XaConsumer... actions) {
        UserTransaction ut = new UserTransactionImp();
        try {
            ut.begin();
            for (XaConsumer action : actions) {
                try (Connection connection = connection(action.jdbcUrl)) {
                    connection.setTransactionIsolation(isolationLevel);
                    action.consumer.accept(connection);
                }
            }
            ut.commit();
        } catch (Throwable e) {
            try {
                ut.rollback();
            } catch (SystemException ex) {
                e.addSuppressed(ex);
            }
            throw new RuntimeException(e);
        }
    }

    private static DataSource dataSource(String jdbcUrl) {
        return dataSources.computeIfAbsent(
            jdbcUrl,
            key -> {
                AtomikosDataSourceBean dsBean = new AtomikosDataSourceBean();
                final String uniqId = StringUtils.substringAfter(jdbcUrl, "5432/");
                dsBean.setUniqueResourceName(uniqId);
                dsBean.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource");
                Properties props = new Properties();
                props.put("URL", jdbcUrl);
                props.put("user", "postgres");
                props.put("password", "secret");
                dsBean.setXaProperties(props);
                dsBean.setMaxPoolSize(10);
                return dsBean;
            }
        );
    }

    private static Connection connection(String jdbcUrl) {
        return threadConnections.computeIfAbsent(
            Thread.currentThread().threadId(),
            key -> {
                try {
                    return new HashMap<>(Map.of(
                        jdbcUrl,
                        dataSource(jdbcUrl).getConnection()
                    ));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
        }).computeIfAbsent(
            jdbcUrl,
            key -> {
                try {
                    return dataSource(jdbcUrl).getConnection();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        );
    }

    public static void closeAllConnections() {
        for (Map<String, Connection> connectionMap : threadConnections.values()) {
            for (Connection connection : connectionMap.values()) {
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    // NOP
                }
            }
        }
    }
}