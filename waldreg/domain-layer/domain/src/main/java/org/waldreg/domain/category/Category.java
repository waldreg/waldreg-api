package org.waldreg.domain.category;

import java.util.ArrayList;
import java.util.List;
import org.waldreg.domain.board.Board;

public final class Category{

    private int id;
    private String categoryName;

    private List<Board> boardList;

    private Category(){
        throw new UnsupportedOperationException("Can not invoke constructor \"Category()\"");
    }

    private Category(Builder builder){
        this.id = builder.id;
        this.categoryName = builder.categoryName;
        this.boardList = builder.boardList;
    }

    public static Builder builder(){
        return new Builder();
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
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

    public final static class Builder{

        private int id;
        private String categoryName;
        private List<Board> boardList;

        {
            boardList = new ArrayList<>();
        }

        private Builder(){}

        public Builder id(int id){
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
