package org.waldreg.domain.character;

public final class Permission{

    private final int id;
    private final String service;
    private final String name;
    private final String status;

    private Permission(){
        throw new UnsupportedOperationException("Can not invoke constructor \"Permission()\"");
    }

    private Permission(Builder builder){
        this.id = builder.id;
        this.service = builder.service;
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

    public int getId(){
        return id;
    }

    public String getService(){
        return service;
    }

    public static final class Builder{

        private int id;
        private String service;
        private String name;
        private String status;

        private Builder(){}

        public Builder id(int id){
            this.id = id;
            return this;
        }

        public Builder service(String service){
            this.service = service;
            return this;
        }

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