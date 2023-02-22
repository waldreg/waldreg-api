package org.waldreg.domain.waiver;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.waldreg.domain.user.User;

@Entity
@Table(name = "WAIVER")
public final class Waiver{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WAIVER_WAIVER_ID")
    private Integer waiverId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User waiverUser;

    @Column(name = "WAIVER_WAIVER_DATE", columnDefinition = "DATE", nullable = false)
    private LocalDate waiverDate;

    @Column(name = "WAIVER_WAIVER_REASON", length = 1000)
    private String waiverReason;

    private Waiver(){}

    private Waiver(Builder builder){
        this.waiverUser = builder.waiverUser;
        this.waiverDate = builder.waiverDate;
        this.waiverReason = builder.waiverReason;
    }

    public static Builder builder(){
        return new Builder();
    }

    public Integer getWaiverId(){
        return waiverId;
    }

    public User getWaiverUser(){
        return waiverUser;
    }

    public LocalDate getWaiverDate(){
        return waiverDate;
    }

    public String getWaiverReason(){
        return waiverReason;
    }

    public static final class Builder{

        private User waiverUser;
        private LocalDate waiverDate;
        private String waiverReason;

        private Builder(){}

        public Builder waiverUser(User waiverUser){
            this.waiverUser = waiverUser;
            return this;
        }

        public Builder waiverDate(LocalDate waiverDate){
            this.waiverDate = waiverDate;
            return this;
        }

        public Builder waiverReason(String waiverReason){
            this.waiverReason = waiverReason;
            return this;
        }

        public Waiver build(){
            return new Waiver(this);
        }

    }

}
