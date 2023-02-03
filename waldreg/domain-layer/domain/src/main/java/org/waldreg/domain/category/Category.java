package org.waldreg.domain.category;

import org.waldreg.domain.tier.MemberTier;

public final class Category{

    private final int id;
    private final String categoryName;
    private final MemberTier memberTier;

    private Category(){
        throw new UnsupportedOperationException("Can not invoke constructor \"Category()\"");
    }

    private Category(Builder builder){
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

        public Category build(){
            return new Category(this);
        }

    }

}
