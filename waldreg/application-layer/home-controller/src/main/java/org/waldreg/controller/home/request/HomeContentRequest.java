package org.waldreg.controller.home.request;

public final class HomeContentRequest{

    private String content;

    public HomeContentRequest(){}

    private HomeContentRequest(Builder builder){
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

        public HomeContentRequest build(){
            return new HomeContentRequest(this);
        }

    }

}
