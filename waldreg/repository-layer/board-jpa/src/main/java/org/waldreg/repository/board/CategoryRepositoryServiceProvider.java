package org.waldreg.repository.board;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.waldreg.board.category.spi.CategoryRepository;
import org.waldreg.board.dto.CategoryDto;
import org.waldreg.domain.board.Board;
import org.waldreg.domain.board.category.Category;
import org.waldreg.repository.board.mapper.CategoryRepositoryMapper;
import org.waldreg.repository.board.repository.JpaBoardRepository;
import org.waldreg.repository.board.repository.JpaCategoryRepository;

@Repository
public class CategoryRepositoryServiceProvider implements CategoryRepository{

    private final JpaCategoryRepository jpaCategoryRepository;
    private final JpaBoardRepository jpaBoardRepository;
    private final CategoryRepositoryMapper categoryRepositoryMapper;

    @Autowired
    public CategoryRepositoryServiceProvider(JpaCategoryRepository jpaCategoryRepository, JpaBoardRepository jpaBoardRepository, CategoryRepositoryMapper categoryRepositoryMapper){
        this.jpaCategoryRepository = jpaCategoryRepository;
        this.jpaBoardRepository = jpaBoardRepository;
        this.categoryRepositoryMapper = categoryRepositoryMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isExistCategory(int categoryId){
        return jpaCategoryRepository.existsById(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto inquiryCategoryById(int id){
        Category category = jpaCategoryRepository.findById(id).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find category id \"" + id + "\"");}
        );
        return categoryRepositoryMapper.categoryDomainToCategoryDto(category);
    }

    @Override
    @Transactional
    public void createCategory(CategoryDto categoryDto){
        Category category = categoryRepositoryMapper.categoryDtoToCategoryDomain(categoryDto);
        jpaCategoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> inquiryAllCategory(){
        List<Category> categoryList = jpaCategoryRepository.findAll();
        return categoryRepositoryMapper.categoryDomainListToCategoryDtoList(categoryList);
    }

    @Override
    @Transactional
    public void modifyCategory(CategoryDto categoryDto){
        Category category = categoryRepositoryMapper.categoryDtoToCategoryDomain(categoryDto);
        jpaCategoryRepository.save(category);
    }

    @Override
    @Transactional
    public void deleteCategory(int id){
        int maxIdx = jpaBoardRepository.getBoardMaxIdxByCategoryId(id);
        List<Board> boardList = jpaBoardRepository.findByCategoryId(id,0,maxIdx);
        jpaBoardRepository.deleteAll(boardList);
        jpaCategoryRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isDuplicateCategoryName(String categoryName){
        return jpaCategoryRepository.isDuplicatedName(categoryName);
    }

}
