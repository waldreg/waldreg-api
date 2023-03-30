package org.waldreg.home.service.color.dto;

public final class ColorDto{

    private final Integer id;
    private final String primaryColor;
    private final String backgroundColor;

    private ColorDto(){
        throw new UnsupportedOperationException("Cannot invoke constructor \"ColorDto()\"");
    }

    private ColorDto(Builder builder){
        this.id = builder.id;
        this.primaryColor = builder.primaryColor;
        this.backgroundColor = builder.backgroundColor;
    }

    public static Builder builder(){return new Builder();}

    public Integer getId(){
        return id;
    }

    public String getPrimaryColor(){
        return primaryColor;
    }

    public String getBackgroundColor(){
        return backgroundColor;
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

        public ColorDto build(){
            return new ColorDto(this);
        }

    }

}
