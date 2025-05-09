package edu.byteme.data.entities;

/*
* base class for all db entities
* we want this for consistent id handling
* tracks create / update timestamps auto
* copied the hashcode implementation based on id from the vaadin tuto
*
* we extend this in all other classes in this folder - user client admin etc
* */

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;


@MappedSuperclass
@EntityListeners(AuditingEntityListener.class) //for the timestamp fields
public abstract class AbstractEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @CreatedDate
    @Column(updatable = false) // this is locked after first data insert
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;


    //getters
    public Integer getId(){ //gets the primary key
        return id;
    }

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }

    public LocalDateTime getUpdatedAt(){
        return updatedAt;
    }

    @Override
    public int hashCode() {
        if (getId() != null) {
            return getId().hashCode();
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractEntity that)) {
            return false; // null or not an AbstractEntity class
        }
        if (getId() != null) {
            return getId().equals(that.getId());
        }
        return super.equals(that);
    }
}

