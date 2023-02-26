package org.waldreg.repository.board;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.waldreg.board.comment.spi.CommentRepository;
import org.waldreg.repository.board.mapper.CategoryRepositoryMapper;
import org.waldreg.repository.board.mapper.CommentRepositoryMapper;
import org.waldreg.repository.board.mapper.CommentRepositoryServiceProvider;

@DataJpaTest
@ContextConfiguration(classes = {
        CommentRepositoryServiceProvider.class,
        CommentRepositoryMapper.class,
        JpaBoardTestInitializer.class})
@TestPropertySource("classpath:h2-application.properties")
public class CommentRepositoryTest{

    @Autowired
    private CommentRepository commentRepository;


}
