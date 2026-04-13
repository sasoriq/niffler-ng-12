package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class UsersQueueExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension.class);

    public record StaticUser(String username, String password, String friend, String income, String outcome) {}

    private static final Queue<StaticUser> EMPTY_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> USERS_WITH_FRIEND = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> USERS_WITH_INCOME_REQUEST = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> USERS_WITH_OUTCOME_REQUEST = new ConcurrentLinkedQueue<>();

    static {
        EMPTY_USERS.add(new StaticUser("duck", "12345", null, null, null));
        USERS_WITH_FRIEND.add(new StaticUser("pig", "12345", "", null, null));
        USERS_WITH_INCOME_REQUEST.add(new StaticUser("dog", "12345",  null, "", null));
        USERS_WITH_OUTCOME_REQUEST.add(new StaticUser("cat", "12345",  null, null, ""));
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface UserType {
        Type value() default Type.EMPTY;

        enum Type {
            EMPTY, WITH_FRIEND, WITH_INCOME_REQUEST, WITH_OUTCOME_REQUEST
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void beforeTestExecution(ExtensionContext context) {
        Arrays.stream(context.getRequiredTestMethod().getParameters())
                .filter(p -> AnnotationSupport.isAnnotated(p, UserType.class))
                .map(p -> p.getAnnotation(UserType.class))
                .forEach(ut -> {
                    Optional<StaticUser> user = Optional.empty();
                    StopWatch sw = StopWatch.createStarted();
                    while (user.isEmpty() && sw.getTime(TimeUnit.SECONDS) < 30) {
                        user = switch (ut.value()) {
                            case EMPTY -> Optional.ofNullable(EMPTY_USERS.poll());
                            case WITH_FRIEND -> Optional.ofNullable(USERS_WITH_FRIEND.poll());
                            case WITH_INCOME_REQUEST -> Optional.ofNullable(USERS_WITH_INCOME_REQUEST.poll());
                            case WITH_OUTCOME_REQUEST -> Optional.ofNullable(USERS_WITH_OUTCOME_REQUEST.poll());
                        };
                    }
                    Allure.getLifecycle().updateTestCase(testCase ->
                            testCase.setStart(new Date().getTime())
                    );

                    context.getStore(NAMESPACE)
                            .getOrComputeIfAbsent(
                                    context.getUniqueId(),
                                    key -> new HashMap<>(),
                                    Map.class
                            ).put(ut, user.orElseThrow(() -> new IllegalStateException("Static user was not found in 30 seconds")));
                });
    }

    @SuppressWarnings("unchecked")
    @Override
    public void afterTestExecution(ExtensionContext context) {
        Map<UserType, StaticUser> map = context.getStore(NAMESPACE).get(context.getUniqueId(), Map.class);
        if (map == null) return;
        for (Map.Entry<UserType, StaticUser> entry : map.entrySet()) {
            switch (entry.getKey().value()) {
                case EMPTY -> EMPTY_USERS.add(entry.getValue());
                case WITH_FRIEND -> USERS_WITH_FRIEND.add(entry.getValue());
                case WITH_INCOME_REQUEST -> USERS_WITH_INCOME_REQUEST.add(entry.getValue());
                case WITH_OUTCOME_REQUEST -> USERS_WITH_OUTCOME_REQUEST.add(entry.getValue());
            }
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
                && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), StaticUser.class);
    }
}
