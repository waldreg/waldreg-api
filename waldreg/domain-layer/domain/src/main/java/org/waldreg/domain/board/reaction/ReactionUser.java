package org.waldreg.domain.board.reaction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.waldreg.domain.user.User;

@Entity
@Table(name = "REACTION_USER")
public final class ReactionUser{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REACTION_USER_ID")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "REACTION_ID", nullable = false)
    private Reaction reaction;

    private ReactionUser(){}

    private ReactionUser(Builder builder){
        this.user = builder.user;
        this.reaction = builder.reaction;
    }

    public static Builder builder(){
        return new Builder();
    }

    public Integer getId(){
        return id;
    }

    public User getUser(){
        return user;
    }

    public Reaction getReaction(){
        return reaction;
    }

    public static final class Builder{

        private User user;
        private Reaction reaction;

        private Builder(){}

        public Builder user(User user){
            this.user = user;
            return this;
        }

        public Builder reaction(Reaction reaction){
            this.reaction = reaction;
            return this;
        }

        public ReactionUser build(){
            return new ReactionUser(this);
        }

    }

}
