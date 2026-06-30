package guru.qa.niffler.service;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.Optional;

public interface SpendClient {
    SpendJson createSpend(SpendJson spend);

    CategoryJson createCategory(CategoryJson category);

    SpendJson updateSpend(SpendJson spend);

    CategoryJson updateCategory(CategoryJson category);

}
