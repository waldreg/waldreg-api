package org.waldreg.domain.board.file;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.waldreg.domain.board.Board;

@Embeddable
@Table(name = "FILENAME")
public class FileName{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FILENAME_ID")
    private Integer id;

    @Column(name = "FILENAME_ORIGIN", nullable = false)
    private String origin;

    @Column(name = "FILENAME_UUID", nullable = false)
    private String uuid;

    private FileName(){}

    private FileName(Builder builder){
        this.origin = builder.origin;
        this.uuid = builder.uuid;
    }

    public static Builder builder(){
        return new Builder();
    }

    public Integer getId(){
        return id;
    }

    public String getOrigin(){
        return origin;
    }

    public String getUuid(){
        return uuid;
    }


    public static final class Builder{

        private String origin;

        private String uuid;


        public Builder origin(String origin){
            this.origin = origin;
            return this;
        }

        public Builder uuid(String uuid){
            this.uuid = uuid;
            return this;
        }

        public FileName build(){
            return new FileName(this);
        }

    }

}
