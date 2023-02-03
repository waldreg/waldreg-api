package org.waldreg.controller.board.response;

public class Reaction{

    private int good;
    private int bad;
    private int check;
    private int heart;
    private int smile;
    private int sad;

    private String[] users;

    public Reaction(){}

    private Reaction(Builder builder){
        this.good = builder().good;
        this.bad = builder().bad;
        this.check = builder().check;
        this.heart = builder().heart;
        this.smile = builder().smile;
        this.sad = builder().sad;
        this.users = builder.users;
    }

    public static Builder builder(){
        return new Builder();
    }

    public int getGood(){
        return good;
    }

    public void setGood(int good){
        this.good = good;
    }

    public int getBad(){
        return bad;
    }

    public void setBad(int bad){
        this.bad = bad;
    }

    public int getCheck(){
        return check;
    }

    public void setCheck(int check){
        this.check = check;
    }

    public int getHear(){
        return heart;
    }

    public void setHear(int heart){
        this.heart = heart;
    }

    public int getSmile(){
        return smile;
    }

    public void setSmile(int smile){
        this.smile = smile;
    }

    public int getSad(){
        return sad;
    }

    public void setSad(int sad){
        this.sad = sad;
    }

    public String[] getUsers(){
        return users;
    }

    public void setUsers(String[] users){
        this.users = users;
    }

    public final static class Builder{

        private int good;
        private int bad;
        private int check;
        private int heart;
        private int smile;
        private int sad;
        private String[] users;

        {
            good = 0;
            bad = 0;
            check = 0;
            heart = 0;
            smile = 0;
            sad = 0;
        }

        private Builder(){}

        public Builder good(int good){
            this.good = good;
            return this;
        }

        public Builder bad(int bad){
            this.bad = bad;
            return this;
        }

        public Builder check(int check){
            this.check = check;
            return this;
        }

        public Builder heart(int heart){
            this.heart = heart;
            return this;
        }

        public Builder smile(int smile){
            this.smile = smile;
            return this;
        }

        public Builder sad(int sad){
            this.sad = sad;
            return this;
        }

        public Builder users(String[] users){
            this.users = users;
            return this;
        }

        public Reaction build(){
            return new Reaction(this);
        }

    }

}
