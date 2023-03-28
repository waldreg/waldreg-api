package org.waldreg.home.service.homecontent.dto;

import org.waldreg.home.core.request.HomeRequestable;
import org.waldreg.home.core.response.HomeReadable;

public class HomeContentDto implements HomeRequestable, HomeReadable{

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

    @Override
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
