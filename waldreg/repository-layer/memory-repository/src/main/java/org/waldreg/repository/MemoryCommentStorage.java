package org.waldreg.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.board.comment.Comment;

@Repository
public class MemoryCommentStorage{

    private final AtomicInteger atomicInteger;

    private final Map<Integer, Comment> storage;

    {
        storage = new ConcurrentHashMap<>();
        atomicInteger = new AtomicInteger(1);
    }

    public Comment createComment(Comment comment){
        comment.setId(atomicInteger.getAndIncrement());
        storage.put(comment.getId(), comment);
        return comment;
    }

    public void deleteAllComment(){
        storage.clear();
    }

}
