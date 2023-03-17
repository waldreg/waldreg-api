package org.waldreg.repository.board;

import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.board.comment.Comment;

@Repository
public class CommentCommander{

    private final EntityManager entityManager;

    @Autowired
    CommentCommander(EntityManager entityManager1){
        this.entityManager = entityManager1;
    }

    public List<Comment> findAllByBoardId(int boardId, int from, int to){
        return entityManager.createQuery("select C from Comment as C where C.board.id = ?1", Comment.class)
                .setParameter(1, boardId)
                .setFirstResult(from - 1)
                .setMaxResults(to - from + 1)
                .getResultList();
    }


}
