package org.waldreg.repository.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.board.comment.Comment;

@Repository("authJpaCommentRepository")
public interface JpaCommentRepository extends JpaRepository<Comment,Integer>{
}
