package org.waldreg.domain.board.category;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.waldreg.domain.board.Board;

@Entity
@Table(name = "CATEGORY")
public final class Category{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CATEGORY_ID")
    private Integer id;

    @Column(name = "CATEGORY_NAME", nullable = false, length = 50)
    private String categoryName;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Board> boardList;

    private Category(){}

    private Category(Builder builder){
        this.id = builder.id;
        this.categoryName = builder.categoryName;
        this.boardList = builder.boardList;
    }

    public static Builder builder(){
        return new Builder();
    }

    public Integer getId(){
        return id;
    }

    public String getCategoryName(){
        return categoryName;
    }

    public void setCategoryName(String categoryName){this.categoryName = categoryName;}

    public List<Board> getBoardList(){
        return boardList;
    }

    public void setBoardList(List<Board> boardList){
        this.boardList = boardList;
    }

    public void addBoard(Board board){
        this.boardList.add(board);
    }

    public static final class Builder{

        private Integer id;
        private String categoryName;
        private List<Board> boardList;

        private Builder(){
            boardList = new ArrayList<>();
        }

        public Builder id(Integer id){
            this.id = id;
            return this;
        }

        public Builder categoryName(String categoryName){
            this.categoryName = categoryName;
            return this;
        }

        public Builder boardList(List<Board> boardList){
            this.boardList = boardList;
            return this;
        }

        public Category build(){
            return new Category(this);
        }

    }

}