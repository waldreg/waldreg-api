package org.waldreg.repository.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.board.category.Category;

@Repository("boardJpaCategoryRepository")
public interface JpaCategoryRepository extends JpaRepository<Category,Integer>{

    boolean existsByCategoryName(String name);
}
