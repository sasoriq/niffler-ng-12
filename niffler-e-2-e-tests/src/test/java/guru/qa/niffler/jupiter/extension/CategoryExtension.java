package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.service.SpendDbClient;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

public class CategoryExtension implements BeforeEachCallback, ParameterResolver {

    private final static ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
    private final SpendDbClient spendClient = new SpendDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(
            context.getRequiredTestMethod(),
            Category.class
        ).ifPresent(
            anno -> {
                CategoryJson category = new CategoryJson(
                    null,
                    anno.name(),
                    anno.username(),
                    anno.archived()
                );
                context.getStore(NAMESPACE).put(
                    context.getUniqueId(),
                    spendClient.createCategory(category)
                );
            }
        );
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter()
            .getType()
            .equals(CategoryJson.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE)
            .get(extensionContext.getUniqueId(), CategoryJson.class);
    }
}
