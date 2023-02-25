package org.waldreg.repository.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.board.Board;

@Repository
public interface JpaBoardRepository extends JpaRepository<Board, Integer>{

    @Query(value = "select A.* from (select B.*, C.CATEGORY_NAME,U.USER_NAME,U.USER_USER_ID from BOARD as B LEFT JOIN CATEGORY as C, USER as U WHERE B.CATEGORY_ID = C.CATEGORY_ID AND B.USER_ID = U.USER_ID) as A LIMIT :count OFFSET :start", nativeQuery = true)
    List<Board> findAll(@Param("start") int start, @Param("count") int count);

    @Query(value = "select A.* from (select B.*, C.CATEGORY_NAME,U.USER_NAME,U.USER_USER_ID from BOARD as B LEFT JOIN CATEGORY as C, USER as U WHERE B.CATEGORY_ID = C.CATEGORY_ID AND B.USER_ID = U.USER_ID AND B.BOARD_TITLE LIKE %:title%) as A LIMIT :count OFFSET :start", nativeQuery = true)
    List<Board> findByTitle(@Param("title") String title, @Param("start") int start, @Param("count") int count);

    @Query(value = "select A.* from (select B.*, C.CATEGORY_NAME,U.USER_NAME,U.USER_USER_ID from BOARD as B LEFT JOIN CATEGORY as C, USER as U WHERE B.CATEGORY_ID = C.CATEGORY_ID AND B.USER_ID = U.USER_ID AND B.BOARD_CONTENT LIKE %:content%) as A LIMIT :count OFFSET :start", nativeQuery = true)
    List<Board> findByContent(@Param("content") String content, @Param("start") int start, @Param("count") int count);

    @Query(value = "select A.* from (select B.*, C.CATEGORY_NAME,U.USER_NAME,U.USER_USER_ID from BOARD as B LEFT JOIN CATEGORY as C, USER as U WHERE B.CATEGORY_ID = C.CATEGORY_ID AND B.USER_ID = U.USER_ID AND U.USER_USER_ID LIKE %:userId%) as A LIMIT :count OFFSET :start", nativeQuery = true)
    List<Board> findByUserId(@Param("userId") String userId, @Param("start") int start, @Param("count") int count);

    @Query(value = "select A.* from (select B.*, C.CATEGORY_NAME,U.USER_NAME,U.USER_USER_ID from BOARD as B LEFT JOIN CATEGORY as C, USER as U WHERE B.CATEGORY_ID = C.CATEGORY_ID AND B.USER_ID = U.USER_ID AND B.CATEGORY_ID = :categoryId) as A LIMIT :count OFFSET :start", nativeQuery = true)
    List<Board> findByCategoryId(@Param("categoryId") Integer categoryId, @Param("start") int start, @Param("count") int count);

    @Query("select count(b) from Board b where b.title like %:title%")
    Integer getBoardMaxIdxByTitle(@Param("title") String title);

    @Query("select count(b) from Board b where b.content like %:content%")
    Integer getBoardMaxIdxByContent(@Param("content") String content);

    @Query("select count(b) from Board b where b.user.userInfo.userId like %:userId%")
    Integer getBoardMaxIdxByUserId(@Param("userId") String userId);

    @Query("select count(b) from Board b where b.category.id =:categoryId")
    Integer getBoardMaxIdxByCategoryId(@Param("categoryId") Integer categoryId);

}
