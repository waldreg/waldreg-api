package org.waldreg.character.dto;

public final class PermissionDto{

    private final int id;
    private final String service;
    private final String name;
    private final String status;

    private PermissionDto(){
        throw new UnsupportedOperationException("Can not invoke constructor \"PermissionDto()\"");
    }

    private PermissionDto(Builder builder){
        this.id = builder.id;
        this.service = builder.service;
        this.name = builder.name;
        this.status = builder.status;
    }

    public static Builder builder(){
        return new Builder();
    }

    public int getId(){
        return id;
    }

    public String getService(){
        return service;
    }

    public String getName(){
        return this.name;
    }

    public String getStatus(){
        return this.status;
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

        public PermissionDto build(){
            return new PermissionDto(this);
        }

    }

}