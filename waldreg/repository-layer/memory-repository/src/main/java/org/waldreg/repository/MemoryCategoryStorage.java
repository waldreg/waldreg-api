package org.waldreg.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Repository;
import org.waldreg.board.dto.CategoryDto;
import org.waldreg.domain.category.Category;

@Repository
public class MemoryCategoryStorage{

    private final AtomicInteger atomicInteger;

    private final Map<Integer, Category> storage;

    {
        storage = new ConcurrentHashMap<>();
        atomicInteger = new AtomicInteger(1);
    }

    public void createCategory(Category category){
        category.setId(atomicInteger.getAndIncrement());
        storage.put(category.getId(),category);
    }

}
