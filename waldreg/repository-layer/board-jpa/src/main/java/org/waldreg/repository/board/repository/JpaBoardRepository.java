package org.waldreg.repository.board.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.board.Board;

@Repository
public interface JpaBoardRepository extends JpaRepository<Board, Integer>{

    @Query("select b from Board b join fetch b.category join fetch b.user where b.title like %:title%")
    List<Board> findByTitle(@Param("title") String title);

    @Query("select b from Board b join fetch b.category join fetch b.user where b.content like %:content%")
    List<Board> findByContent(@Param("content") String content);

    @Query("select b from Board b join fetch b.category join fetch b.user where b.user.userInfo.userId like %:userId%")
    List<Board> findByUserId(@Param("userId") String userId);

    @Query("select b from Board b join fetch b.category join fetch b.user where b.category.id = :categoryId")
    List<Board> findByCategoryId(@Param("categoryId") Integer categoryId);

    @Query("select count(b) from Board b where b.title like %:title%")
    Integer getBoardMaxIdxByTitle(@Param("title") String title);

    @Query("select count(b) from Board b where b.content like %:content%")
    Integer getBoardMaxIdxByContent(@Param("content") String content);

    @Query("select count(b) from Board b where b.user.userInfo.userId like %:userId%")
    Integer getBoardMaxIdxByUserId(@Param("userId") String userId);

    @Query("select count(b) from Board b where b.category.id =:categoryId")
    Integer getBoardMaxIdxByCategoryId(@Param("categoryId") Integer categoryId);
}
