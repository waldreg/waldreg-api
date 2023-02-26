package org.waldreg.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.board.Board;
import org.waldreg.domain.board.category.Category;

@Repository
public class MemoryCategoryStorage{

    private final AtomicInteger atomicInteger;

    private final Map<Integer, Category> storage;

    {
        storage = new ConcurrentHashMap<>();
        atomicInteger = new AtomicInteger(1);
    }

    public void createCategory(Category category){
    }

    public List<Category> inquiryAllCategory(){
        List<Category> categoryList = new ArrayList<>();
        for (Map.Entry<Integer, Category> categoryEntry : storage.entrySet()){
            categoryList.add(categoryEntry.getValue());
        }
        return categoryList;
    }

    public void deleteAllCategory(){
        storage.clear();
    }

    public void deleteCategory(int id){
        storage.remove(id);
    }

    public Category inquiryCategoryById(int categoryId){
        return storage.get(categoryId);
    }

    public void addBoardInCategoryBoardList(Board board){
    }

    public void modifyCategory(Category category){
        storage.replace(category.getId(), category);
    }

    public boolean isDuplicatedCategoryName(String categoryName){
        for (Map.Entry<Integer, Category> categoryEntry : storage.entrySet()){
            if( categoryEntry.getValue().getCategoryName().equals(categoryName)){
                return true;
            }
        }
        return false;
    }

}