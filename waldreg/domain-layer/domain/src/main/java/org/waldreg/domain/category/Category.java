package org.waldreg.domain.category;

import java.util.List;
import org.waldreg.domain.board.Board;

public final class Category{

    private final int id;
    private final String categoryName;

    private final List<Board> boardList;

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

    public String getCategoryName(){
        return categoryName;
    }

    public List<Board> getBoardList(){
        return boardList;
    }

    public void addBoard(Board board){
        this.boardList.add(board);
    }

    public final static class Builder{

        private int id;
        private String categoryName;
        private List<Board> boardList;

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
