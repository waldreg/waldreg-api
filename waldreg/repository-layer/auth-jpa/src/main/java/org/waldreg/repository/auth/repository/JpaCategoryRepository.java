package org.waldreg.repository.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.board.category.Category;

@Repository
public interface JpaCategoryRepository extends JpaRepository<Category,Integer>{
}
