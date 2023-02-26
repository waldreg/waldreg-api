package org.waldreg.domain.teambuilding;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.waldreg.domain.user.User;

@Entity
@Table(name = "TEAM_USER")
public final class TeamUser{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TEAM_USER_ID")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_TEAM_ID", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    private TeamUser(){}

    private TeamUser(Builder builder){
        this.team = builder.team;
        this.user = builder.user;
    }

    public Integer getId(){
        return id;
    }

    public Team getTeam(){
        return team;
    }

    public User getUser(){
        return user;
    }

    public static final class Builder{

        private Team team;
        private User user;

        private Builder(){}

        public Builder team(Team team){
            this.team = team;
            return this;
        }

        public Builder user(User user){
            this.user = user;
            return this;
        }

        public TeamUser build(){
            return new TeamUser(this);
        }

    }

}
