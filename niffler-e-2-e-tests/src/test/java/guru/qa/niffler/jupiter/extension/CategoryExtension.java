package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.service.SpendDbClient;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import static guru.qa.niffler.jupiter.extension.TestMethodContextExtension.context;
import static guru.qa.niffler.utils.RandomDataUtils.randomCategoryName;

public class CategoryExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public final static ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
    private final SpendDbClient spendClient = new SpendDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(
            context.getRequiredTestMethod(),
            User.class
        ).ifPresent(
            anno -> {
                if (anno.categories().length > 0) {
                    Category category = anno.categories()[0];
                    String categoryName = category.name().isBlank() ? randomCategoryName() : category.name();
                    CategoryJson categoryJson = new CategoryJson(
                        null,
                        categoryName,
                        anno.username(),
                        category.archived()
                    );
                    CategoryJson createdCategory = spendClient.createCategory(categoryJson);
                    if (createdCategory.archived()) {
                        CategoryJson archivedCategory = new CategoryJson(
                            createdCategory.id(),
                            createdCategory.name(),
                            createdCategory.username(),
                            true
                        );
                        createdCategory = spendClient.updateCategory(archivedCategory);
                    }
                    context.getStore(NAMESPACE).put(
                        context.getUniqueId(),
                        createdCategory
                    );
                }
            }
        );
    }

    @Override
    public void afterEach(ExtensionContext context) {
        CategoryJson categoryJson = context().getStore(NAMESPACE)
            .get(context().getUniqueId(), CategoryJson.class);
        if (categoryJson != null && !categoryJson.archived()) {
            categoryJson = new CategoryJson(
                categoryJson.id(),
                categoryJson.name(),
                categoryJson.username(),
                true
            );
            spendClient.updateCategory(categoryJson);
        }
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