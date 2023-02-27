package org.waldreg.domain.board.reaction;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.waldreg.domain.board.Board;

@Entity
@Table(name = "REACTION")
public final class Reaction{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REACTION_ID")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BOARD_ID", nullable = false)
    private Board board;

    @Column(name = "REACTION_TYPE")
    private String type;

    @OneToMany(mappedBy = "reaction", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<ReactionUser> reactionUserList;

    private Reaction(){}

    private Reaction(Builder builder){
        this.board = builder.board;
        this.reactionUserList = builder.reactionUserList;
    }

    public Integer getId(){
        return id;
    }

    public Board getBoard(){
        return board;
    }

    public String getType(){
        return type;
    }

    public List<ReactionUser> getReactionUserList(){
        return reactionUserList;
    }

    public void setReactionUserList(List<ReactionUser> reactionUserList){
        this.reactionUserList = reactionUserList;
    }

    public static Builder builder(){
        return new Builder();
    }

    public static final class Builder{

        private Board board;
        private String type;

        private List<ReactionUser> reactionUserList;

        private Builder(){
            reactionUserList = new ArrayList<>();
        }

        public Builder board(Board board){
            this.board = board;
            return this;
        }

        public Builder type(String type){
            this.type = type;
            return this;
        }

        public Builder reactionUserList(List<ReactionUser> reactionUserList){
            this.reactionUserList = reactionUserList;
            return this;
        }

        public Reaction build(){
            return new Reaction(this);
        }

    }

}
