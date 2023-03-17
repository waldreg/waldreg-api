package org.waldreg.repository.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.board.Board;

@Repository("boardJpaBoardRepository")
public interface JpaBoardRepository extends JpaRepository<Board, Integer>{

    @Query("select count(b) from Board b where b.title like %:title%")
    Integer getBoardMaxIdxByTitle(@Param("title") String title);

    @Query("select count(b) from Board b where b.content like %:content%")
    Integer getBoardMaxIdxByContent(@Param("content") String content);

    @Query("select count(b) from Board b where b.user.userInfo.userId like %:userId%")
    Integer getBoardMaxIdxByUserId(@Param("userId") String userId);

    @Query("select count(b) from Board b where b.category.id =:categoryId")
    Integer getBoardMaxIdxByCategoryId(@Param("categoryId") Integer categoryId);

}
