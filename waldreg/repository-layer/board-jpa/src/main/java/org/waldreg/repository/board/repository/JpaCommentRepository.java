package org.waldreg.repository.board.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.board.comment.Comment;

@Repository("boardJpaCommentRepository")
public interface JpaCommentRepository extends JpaRepository<Comment,Integer>{

    @Query("select count(c) from Comment c where c.board.id =:boardId")
    Integer getBoardMaxIdxByBoardId(@Param("boardId") Integer boardId);

}
