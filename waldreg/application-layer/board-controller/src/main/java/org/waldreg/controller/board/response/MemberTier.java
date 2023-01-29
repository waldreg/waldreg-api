package org.waldreg.controller.board.response;

public class MemberTier{

    private String tier;

    private String info;

    public MemberTier(){}

    private MemberTier(Builder builder){
        this.tier = builder.tier;
        this.info = builder.info;
    }

    public String getTier(){
        return tier;
    }

    public String getInfo(){
        return info;
    }

    public final static class Builder{

        private String tier;

        private String info;

        public Builder tier(String tier){
            this.tier = tier;
            return this;
        }

        public Builder info(String info){
            this.info = info;
            return this;
        }

        public MemberTier build(){
            return new MemberTier(this);
        }

    }


}
