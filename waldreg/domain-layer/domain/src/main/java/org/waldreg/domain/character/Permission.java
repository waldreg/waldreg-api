package org.waldreg.domain.character;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "PERMISSION")
public final class Permission{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PERMISSION_ID")
    private Integer id;

    @Column(name = "PERMISSION_SERVICE", nullable = false)
    private String service;

    @Column(name = "PERMISSION_NAME", nullable = false, unique = true)
    private String name;

    @Column(name = "PERMISSION_STATUS", nullable = false)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHARACTER_ID")
    private Character character;

    private Permission(){}

    private Permission(Builder builder){
        this.service = builder.service;
        this.name = builder.name;
        this.status = builder.status;
    }

    public static Builder builder(){
        return new Builder();
    }

    public String getName(){
        return this.name;
    }

    public String getStatus(){
        return this.status;
    }

    public Integer getId(){
        return id;
    }

    public String getService(){
        return service;
    }

    void setCharacter(Character character){
        this.character = character;
    }

    public static final class Builder{

        private String service;
        private String name;
        private String status;

        private Builder(){}

        public Builder service(String service){
            this.service = service;
            return this;
        }

        public Builder name(String name){
            this.name = name;
            return this;
        }

        public Builder status(String status){
            this.status = status;
            return this;
        }

        public Permission build(){
            return new Permission(this);
        }

    }

}