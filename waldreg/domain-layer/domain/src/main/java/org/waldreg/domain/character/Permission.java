package org.waldreg.domain.character;

public final class Permission{

    private final String name;
    private final String status;

    private Permission(){
        throw new UnsupportedOperationException("Can not invoke constructor \"Permission()\"");
    }

    private Permission(Builder builder){
        this.name = builder.name;
        this.status = builder.status;
    }

    public static Builder builder(){
        return new Builder();
    }

    public String getName(){
        return this.name;
    }

    public String getStatus(){
        return this.status;
    }

    public final static class Builder{

        private String name;
        private String status;

        private Builder(){}

        public Builder name(String name){
            this.name = name;
            return this;
        }

        public Builder status(String status){
            this.status = status;
            return this;
        }

        public Permission build(){
            return new Permission(this);
        }

    }

}