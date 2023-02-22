package org.waldreg.reward.users.dto;

import java.util.List;

public class UsersRewardDto{

    private final int id;
    private final String name;
    private final String userId;
    private final int reward;
    private final List<UsersRewardTagDto> usersRewardTagDtoList;

    private UsersRewardDto(){
        throw new UnsupportedOperationException("Cannot invoke constructor \"UsersRewardDto()\"");
    }

    private UsersRewardDto(Builder builder){
        this.id = builder.id;
        this.name = builder.name;
        this.userId = builder.userId;
        this.reward = builder.reward;
        this.usersRewardTagDtoList = builder.usersRewardTagDtoList;
    }

    public static Builder builder(){
        return new Builder();
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getUserId(){
        return userId;
    }

    public int getReward(){
        return reward;
    }

    public List<UsersRewardTagDto> getUsersRewardTagDtoList(){
        return usersRewardTagDtoList;
    }

    public final static class Builder{

        private int id;
        private String name;
        private String userId;
        private int reward;
        private List<UsersRewardTagDto> usersRewardTagDtoList;

        private Builder(){}

        public Builder id(int id){
            this.id = id;
            return this;
        }

        public Builder name(String name){
            this.name = name;
            return this;
        }

        public Builder userId(String userId){
            this.userId = userId;
            return this;
        }

        public Builder reward(int reward){
            this.reward = reward;
            return this;
        }

        public Builder usersRewardTagDtoList(List<UsersRewardTagDto> usersRewardTagDtoList){
            this.usersRewardTagDtoList = usersRewardTagDtoList;
            return this;
        }

        public UsersRewardDto build(){
            return new UsersRewardDto(this);
        }

    }

}
