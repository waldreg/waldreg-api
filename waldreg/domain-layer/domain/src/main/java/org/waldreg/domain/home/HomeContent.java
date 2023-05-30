package org.waldreg.domain.home;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "HOME_CONTENT")
public final class HomeContent{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "HOME_CONTENT_ID")
    private Integer id;

    @Lob
    @Column(name = "HOME_CONTENT_CONTENT")
    private String content;

    private HomeContent(){}

    public HomeContent(Builder builder){
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

        public HomeContent build(){
            return new HomeContent(this);
        }

    }

}
