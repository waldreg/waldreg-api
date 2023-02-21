package org.waldreg.teambuilding.teambuilding.dto;

public class UserRequestDto implements Comparable<UserRequestDto>{

    private String userId;
    private int weight;

    private UserRequestDto(){}

    private UserRequestDto(Builder builder){
        this.userId = builder.userId;
        this.weight = builder.weight;
    }

    public String getUserId(){
        return userId;
    }

    public int getWeight(){
        return weight;
    }

    public static Builder builder(){return new Builder();}

    @Override
    public int compareTo(UserRequestDto userRequestDto){
        if(userRequestDto.weight > weight){
            return 1;
        } else if(userRequestDto.weight <weight){
            return -1;
        }
        return 0;
    }

    public final static class Builder{

        private String userId;
        private int weight;

        private Builder(){}

        public Builder userId(String userId){
            this.userId = userId;
            return this;
        }

        public Builder weight(int weight){
            this.weight = weight;
            return this;
        }

        public UserRequestDto build(){return new UserRequestDto(this);}

    }
}
