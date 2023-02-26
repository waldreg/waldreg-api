package org.waldreg.repository.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.board.category.Category;

@Repository
public interface JpaCategoryRepository extends JpaRepository<Category,Integer>{

    @Query(value = "SELECT EXISTS (SELECT C.* FROM CATEGORY as C WHERE C.CATEGORY_NAME = :name)",nativeQuery = true)
    boolean isDuplicatedName(@Param("name") String name);
}