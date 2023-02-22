package org.waldreg.domain.character;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "CHARACTER")
public final class Character{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHARACTER_ID")
    private Integer id;

    @Column(name = "CHARACTER_CHARACTER_NAME", nullable = false, unique = true, length = 25)
    private String characterName;

    @OneToMany(mappedBy = "character", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Permission> permissionList;

    private Character(){}

    private Character(Builder builder){
        this.characterName = builder.characterName;
        this.permissionList = builder.permissionList;
    }

    public static Builder builder(){
        return new Builder();
    }

    public String getCharacterName(){
        return characterName;
    }

    public List<Permission> getPermissionList(){
        return permissionList;
    }

    public Integer getId(){
        return id;
    }

    public static final class Builder{

        private String characterName;
        private List<Permission> permissionList;

        private Builder(){}

        public Builder characterName(String characterName){
            this.characterName = characterName;
            return this;
        }

        public Builder permissionList(List<Permission> permissionList){
            this.permissionList = permissionList;
            return this;
        }

        public Character build(){
            return new Character(this);
        }

    }

}
