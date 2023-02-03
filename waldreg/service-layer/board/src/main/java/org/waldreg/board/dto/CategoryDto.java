package org.waldreg.board.dto;

public class CategoryDto{

    private  int id;
    private  String categoryName;
    private BoardServiceMemberTier boardServiceMemberTier;

    private CategoryDto(){}

    private CategoryDto(Builder builder){
        this.id = builder.id;
        this.categoryName = builder.categoryName;
        this.boardServiceMemberTier = builder.boardServiceMemberTier;
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

    public BoardServiceMemberTier getMemberTier(){
        return boardServiceMemberTier;
    }

    public final static class Builder{

        private int id;
        private String categoryName;
        private BoardServiceMemberTier boardServiceMemberTier;

        private Builder(){}

        public Builder id(int id){
            this.id = id;
            return this;
        }

        public Builder categoryName(String categoryName){
            this.categoryName = categoryName;
            return this;
        }

        public Builder memberTier(BoardServiceMemberTier boardServiceMemberTier){
            this.boardServiceMemberTier = boardServiceMemberTier;
            return this;
        }

        public CategoryDto build(){
            return new CategoryDto(this);
        }

    }

}

