package org.tbank.service.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tbank.client.KudaGoClient;
import org.tbank.dao.UniversalDAO;
import org.tbank.models.Category;

import java.util.Optional;

@Slf4j
@Component
public class CategoryInitializationCommand implements InitializationCommand {
    private final UniversalDAO<Integer, Category> categoryDAO;
    private final KudaGoClient kudaGoClient;


    public CategoryInitializationCommand( UniversalDAO<Integer, Category> categoryDAO, KudaGoClient kudaGoClient) {
        this.categoryDAO = categoryDAO;
        this.kudaGoClient = kudaGoClient;
    }

    @Override
    public Void execute() {
        log.info("Инициализация категорий...");
        Optional<Category[]> categories = kudaGoClient.requestCategories();
        categories.ifPresent(cat -> {
            for (Category category : cat) {
                categoryDAO.put(category.getId(), category);
                log.info("Инициализированные категории: {}", category.getId());
            }
            log.info("Категории успешно инициализированы.");
        });
        return null;
    }
}
