package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class UsersQueueExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension.class);

    public record StaticUser(String username, String password, String friend, String income, String outcome) {}
    public record StaticUserWithType(StaticUser user, UserType.Type type) {}

    private static final Queue<StaticUser> EMPTY_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> USERS_WITH_FRIEND = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> USERS_WITH_INCOME_REQUEST = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> USERS_WITH_OUTCOME_REQUEST = new ConcurrentLinkedQueue<>();

    static {
        EMPTY_USERS.add(new StaticUser("duck", "12345", null, null, null));
        EMPTY_USERS.add(new StaticUser("horse", "12345", null, null, null));
        USERS_WITH_FRIEND.add(new StaticUser("pig", "12345", "cow", null, null));
        USERS_WITH_INCOME_REQUEST.add(new StaticUser("dog", "12345",  null, "cat", null));
        USERS_WITH_OUTCOME_REQUEST.add(new StaticUser("cat", "12345",  null, null, "dog"));
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
    public void beforeEach(ExtensionContext context) {
        Parameter[] parameters = context.getRequiredTestMethod().getParameters();
        IntStream.range(0, parameters.length)
                .filter(i -> AnnotationSupport.isAnnotated(parameters[i], UserType.class))
                .forEach(i -> {
                    UserType ut = parameters[i].getAnnotation(UserType.class);
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

                    ((Map<Integer, StaticUserWithType>) context.getStore(NAMESPACE)
                        .getOrComputeIfAbsent(
                            context.getUniqueId(),
                            key -> new HashMap<>()
                        )).put(i, new StaticUserWithType(
                        user.orElseThrow(() -> new IllegalStateException("Static user was not found in 30 seconds")),
                        ut.value()));
                });
    }

    @SuppressWarnings("unchecked")
    @Override
    public void afterEach(ExtensionContext context) {
        Map<Integer, StaticUserWithType> map = context.getStore(NAMESPACE).remove(context.getUniqueId(), Map.class);
        if (map == null) return;
        for (Map.Entry<Integer, StaticUserWithType> entry : map.entrySet()) {
            switch (entry.getValue().type()) {
                case EMPTY -> EMPTY_USERS.add(entry.getValue().user());
                case WITH_FRIEND -> USERS_WITH_FRIEND.add(entry.getValue().user());
                case WITH_INCOME_REQUEST -> USERS_WITH_INCOME_REQUEST.add(entry.getValue().user());
                case WITH_OUTCOME_REQUEST -> USERS_WITH_OUTCOME_REQUEST.add(entry.getValue().user());
            }
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(StaticUser.class)
                && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public StaticUser resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return ((Map<Integer, StaticUser>) extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), Map.class))
                .get(parameterContext.getIndex());
    }
}
