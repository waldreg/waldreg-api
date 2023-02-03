package org.waldreg.domain.category;

public final class Category{

    private final int id;
    private final String categoryName;

    private Category(){
        throw new UnsupportedOperationException("Can not invoke constructor \"Category()\"");
    }

    private Category(Builder builder){
        this.id = builder.id;
        this.categoryName = builder.categoryName;
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


    public final static class Builder{

        private int id;
        private String categoryName;

        private Builder(){}

        public Builder id(int id){
            this.id = id;
            return this;
        }

        public Builder categoryName(String categoryName){
            this.categoryName = categoryName;
            return this;
        }


        public Category build(){
            return new Category(this);
        }

    }

}
