package org.waldreg.controller.home.response;

public class HomeContentResponse{

    private String content;

    public HomeContentResponse(){}

    private HomeContentResponse(Builder builder){
        this.content = builder.content;
    }

    public static Builder builder(){
        return new Builder();
    }

    public String getContent(){
        return content;
    }

    public void setContent(String content){
        this.content = content;
    }

    public final static class Builder{

        private String content;

        private Builder(){}

        public Builder content(String content){
            this.content = content;
            return this;
        }

        public HomeContentResponse build(){
            return new HomeContentResponse(this);
        }

    }

}
