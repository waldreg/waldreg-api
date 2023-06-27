package org.waldreg.controller.board.board.response;

public class FileName{

    private String origin;

    private String uuid;

    public FileName(){}

    private FileName(Builder builder){
        this.origin = builder.origin;
        this.uuid = builder.uuid;
    }

    public static Builder builder(){
        return new Builder();
    }

    public String getOrigin(){
        return origin;
    }

    public void setOrigin(String origin){
        this.origin = origin;
    }

    public String getUuid(){
        return uuid;
    }

    public void setUuid(String uuid){
        this.uuid = uuid;
    }

    public final static class Builder{
        private String origin;

        private String uuid;

        private Builder(){}

        public Builder origin(String origin){
            this.origin = origin;
            return this;
        }

        public Builder uuid(String uuid){
            this.uuid = uuid;
            return this;
        }

        public FileName build(){
            return new FileName(this);
        }
    }
}
