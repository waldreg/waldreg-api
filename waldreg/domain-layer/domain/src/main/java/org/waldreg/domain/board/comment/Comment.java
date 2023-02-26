package org.waldreg.domain.board.comment;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.waldreg.domain.board.Board;
import org.waldreg.domain.user.User;

@Entity
@Table(name = "COMMENT")
public final class Comment{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_ID")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BOARD_ID", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @Column(name = "COMMENT_CREATED_AT", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "COMMENT_LAST_MODIFIED_AT", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime lastModifiedAt;

    @Column(name = "COMMENT_CONTENT", nullable = false, length = 1000)
    private String content;

    private Comment(){}

    private Comment(Builder builder){
        this.board = builder.board;
        this.user = builder.user;
        this.createdAt = builder.createdAt;
        this.lastModifiedAt = builder.lastModifiedAt;
        this.content = builder.content;
    }

    public static Builder builder(){
        return new Builder();
    }

    public Integer getId(){
        return id;
    }

    public Board getBoard(){
        return board;
    }

    public User getUser(){
        return user;
    }

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }

    public LocalDateTime getLastModifiedAt(){
        return lastModifiedAt;
    }

    public String getContent(){
        return content;
    }

    public void setContent(String content){
        this.content = content;
    }

    public static final class Builder{

        private Board board;
        private User user;
        private LocalDateTime createdAt;
        private LocalDateTime lastModifiedAt;
        private String content;

        private Builder(){
            createdAt = LocalDateTime.now();
            this.lastModifiedAt = createdAt;
        }

        public Builder board(Board board){
            this.board = board;
            return this;
        }

        public Builder user(User user){
            this.user = user;
            return this;
        }

        public Builder lastModifiedAt(LocalDateTime lastModifiedAt){
            this.lastModifiedAt = lastModifiedAt;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt){
            this.createdAt = createdAt;
            return this;
        }

        public Builder content(String content){
            this.content = content;
            return this;
        }

        public Comment build(){
            return new Comment(this);
        }

    }

}
