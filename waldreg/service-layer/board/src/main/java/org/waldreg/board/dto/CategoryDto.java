package org.waldreg.board.dto;

public class CategoryDto{

    private  int id;
    private  String categoryName;
    private  MemberTier memberTier;

    private CategoryDto(){}

    private CategoryDto(Builder builder){
        this.id = builder.id;
        this.categoryName = builder.categoryName;
        this.memberTier = builder.memberTier;
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

    public MemberTier getMemberTier(){
        return memberTier;
    }

    public final static class Builder{

        private int id;
        private String categoryName;
        private MemberTier memberTier;

        private Builder(){}

        public Builder id(int id){
            this.id = id;
            return this;
        }

        public Builder categoryName(String categoryName){
            this.categoryName = categoryName;
            return this;
        }

        public Builder memberTier(MemberTier memberTier){
            this.memberTier = memberTier;
            return this;
        }

        public CategoryDto build(){
            return new CategoryDto(this);
        }

    }

}

