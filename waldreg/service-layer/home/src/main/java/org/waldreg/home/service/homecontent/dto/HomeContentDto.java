package org.waldreg.home.service.homecontent.dto;

public final class HomeContentDto{

    private Integer id;
    private String content;

    private HomeContentDto(){}

    public HomeContentDto(Builder builder){
        this.id = builder.id;
        this.content = builder.content;
    }

    public static Builder builder(){
        return new Builder();
    }

    public Integer getId(){
        return id;
    }

    public String getContent(){
        return content;
    }

    public void setContent(String content){
        this.content = content;
    }

    public static final class Builder{

        private Integer id;
        private String content;

        private Builder(){}

        public Builder id(Integer id){
            this.id = id;
            return this;
        }

        public Builder content(String content){
            this.content = content;
            return this;
        }

        public HomeContentDto build(){
            return new HomeContentDto(this);
        }

    }

}
