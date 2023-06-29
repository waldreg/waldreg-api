package org.waldreg.repository.board;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.board.Board;

@Repository
public class BoardCommander{

    private final EntityManager entityManager;

    @Autowired
    BoardCommander(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    public List<Board> inquiryAllBoard(int from, int to){
        return entityManager.createQuery("select B from Board as B order by B.createdAt desc", Board.class)
                .setFirstResult(from - 1)
                .setMaxResults(to - from + 1)
                .getResultList();
    }

    public List<Board> inquiryBoardByCategoryId(int categoryId, int from, int to){
        return entityManager.createQuery("select B from Board as B where B.category.id = ?1 order by B.createdAt desc", Board.class)
                .setParameter(1, categoryId)
                .setFirstResult(from - 1)
                .setMaxResults(to - from + 1)
                .getResultList();
    }

    public List<Board> searchBoardByTitle(String title, int from, int to){
        return entityManager.createQuery("select B from Board as B where B.title like concat('%',?1,'%') order by B.createdAt desc", Board.class)
                .setParameter(1, title)
                .setFirstResult(from - 1)
                .setMaxResults(to - from + 1)
                .getResultList();
    }

    public List<Board> searchBoardByContent(String content, int from, int to){
        return entityManager.createQuery("select B from Board as B where B.content like concat('%',?1,'%') order by B.createdAt desc", Board.class)
                .setParameter(1, content)
                .setFirstResult(from - 1)
                .setMaxResults(to - from + 1)
                .getResultList();
    }

    public List<Board> searchBoardByUserId(String userId, int from, int to){
        return entityManager.createQuery("select B from Board as B where B.user.userInfo.userId like concat('%',?1,'%') order by B.createdAt desc", Board.class)
                .setParameter(1, userId)
                .setFirstResult(from - 1)
                .setMaxResults(to - from + 1)
                .getResultList();
    }

}
