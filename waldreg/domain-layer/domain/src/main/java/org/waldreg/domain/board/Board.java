package org.waldreg.domain.board;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.waldreg.domain.board.comment.Comment;
import org.waldreg.domain.board.reaction.Reaction;
import org.waldreg.domain.board.category.Category;
import org.waldreg.domain.user.User;

@Entity
@Table(name = "BOARD")
public final class Board{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOARD_ID")
    private Integer id;

    @Column(name = "BOARD_TITLE", nullable = false, length = 250)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID", nullable = false)
    private Category category;

    @Lob
    @Column(name = "BOARD_CONTENT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @Column(name = "BOARD_CREATED_AT", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "BOARD_LAST_MODIFIED_AT", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime lastModifiedAt;

    @Column(name = "BOARD_IMAGE_PATH_LIST")
    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> imagePathList;

    @Column(name = "BOARD_FILE_PATH_LIST")
    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> filePathList;

    @OneToMany(mappedBy = "board", orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Reaction> reactionList;

    @OneToMany(mappedBy = "board", orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Comment> commentList;

    @Column(name = "BOARD_VIEWS", nullable = false)
    private Integer views;

    private Board(){}

    private Board(Builder builder){
        this.title = builder.title;
        this.category = builder.category;
        this.content = builder.content;
        this.user = builder.user;
        this.createdAt = builder.createdAt;
        this.lastModifiedAt = builder.lastModifiedAt;
        this.imagePathList = builder.imagePathList;
        this.filePathList = builder.filePathList;
        this.reactionList = builder.reactionList;
        this.commentList = builder.commentList;
        this.views = builder.views;
    }

    public static Builder builder(){
        return new Builder();
    }

    public Integer getId(){
        return id;
    }

    public String getTitle(){
        return title;
    }

    public Category getCategory(){
        return category;
    }

    public String getContent(){
        return content;
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

    public List<String> getImagePathList(){
        return imagePathList;
    }

    public List<String> getFilePathList(){
        return filePathList;
    }

    public List<Reaction> getReactionList(){return reactionList;}

    public List<Comment> getCommentList(){
        return commentList;
    }

    public int getViews(){
        return views;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setCategory(Category category){
        this.category = category;
    }

    public void setContent(String content){
        this.content = content;
    }

    public void setUser(User user){
        this.user = user;
    }

    public void setLastModifiedAt(LocalDateTime lastModifiedAt){
        this.lastModifiedAt = lastModifiedAt;
    }

    public void setReactions(List<Reaction> reactionList){
        this.reactionList = reactionList;
    }

    public void setCommentList(List<Comment> commentList){
        this.commentList = commentList;
    }

    public void addComment(Comment comment){
        this.commentList.add(comment);
    }

    public static final class Builder{

        private String title;
        private Category category;
        private String content;
        private User user;
        private LocalDateTime createdAt;
        private LocalDateTime lastModifiedAt;
        private List<String> imagePathList;
        private List<String> filePathList;
        private List<Reaction> reactionList;
        private List<Comment> commentList;
        private Integer views;

        private Builder(){
            createdAt = LocalDateTime.now();
            lastModifiedAt = createdAt;
            reactionList = new ArrayList<>();
            commentList = new ArrayList<>();
            views = 0;
        }

        public Builder title(String title){
            this.title = title;
            return this;
        }

        public Builder category(Category category){
            this.category = category;
            return this;
        }

        public Builder content(String content){
            this.content = content;
            return this;
        }

        public Builder user(User user){
            this.user = user;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt){
            this.createdAt = createdAt;
            return this;
        }

        public Builder lastModifiedAt(LocalDateTime lastModifiedAt){
            this.lastModifiedAt = lastModifiedAt;
            return this;
        }


        public Builder imagePathList(List<String> imagePathList){
            this.imagePathList = imagePathList;
            return this;
        }

        public Builder filePathList(List<String> filePathList){
            this.filePathList = filePathList;
            return this;
        }

        public Builder reactionList(List<Reaction> reactionList){
            this.reactionList = reactionList;
            return this;
        }

        public Builder commentList(List<Comment> commentList){
            this.commentList = commentList;
            return this;
        }

        public Builder views(Integer views){
            this.views = views;
            return this;
        }

        public Board build(){
            return new Board(this);
        }

    }

}
