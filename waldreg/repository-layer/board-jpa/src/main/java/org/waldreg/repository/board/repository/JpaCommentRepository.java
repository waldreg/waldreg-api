package org.waldreg.repository.board.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.board.comment.Comment;

@Repository
public interface JpaCommentRepository extends JpaRepository<Comment,Integer>{

    @Query(value = "select A.* from (select C.*,U.USER_NAME,U.USER_USER_ID from COMMENT as C LEFT JOIN USER as U WHERE C.USER_ID = U.USER_ID AND C.BOARD_ID = :boardId) as A LIMIT :count OFFSET :start", nativeQuery = true)
    List<Comment> findAllByBoardId(@Param("boardId") Integer boardId, @Param("start") int start, @Param("count") int count);

    @Query("select count(c) from Comment c where c.board.id =:boardId")
    Integer getBoardMaxIdxByBoardId(@Param("boardId") Integer boardId);

}
