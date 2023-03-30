package org.waldreg.domain.home;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "APPLICATION_COLOR")
public final class ApplicationColor{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "APPLICATION_COLOR_ID")
    private Integer id;

    @Column(name = "APPLICATION_PRIMARY_COLOR")
    private String primaryColor;

    @Column(name = "APPLICATION_BACKGROUND_COLOR")
    private String backgroundColor;

    private ApplicationColor(){}

    private ApplicationColor(Builder builder){
        this.id = builder.id;
        this.primaryColor = builder.primaryColor;
        this.backgroundColor = builder.backgroundColor;
    }

    public static Builder builder(){
        return new Builder();
    }

    public Integer getId(){
        return id;
    }

    public String getPrimaryColor(){
        return primaryColor;
    }

    public String getBackgroundColor(){
        return backgroundColor;
    }

    public void setPrimaryColor(String primaryColor){
        this.primaryColor = primaryColor;
    }

    public void setBackgroundColor(String backgroundColor){
        this.backgroundColor = backgroundColor;
    }

    public static final class Builder{

        private Integer id;
        private String primaryColor;
        private String backgroundColor;

        private Builder(){}

        public Builder id(Integer id){
            this.id = id;
            return this;
        }

        public Builder primaryColor(String primaryColor){
            this.primaryColor = primaryColor;
            return this;
        }

        public Builder backgroundColor(String backgroundColor){
            this.backgroundColor = backgroundColor;
            return this;
        }

        public ApplicationColor build(){
            return new ApplicationColor(this);
        }

    }

}
