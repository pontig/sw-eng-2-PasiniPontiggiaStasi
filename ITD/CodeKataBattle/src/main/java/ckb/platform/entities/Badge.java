package ckb.platform.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity @Table(name = "Badge")
public class Badge {

    private @Id @GeneratedValue Long id;

    public Badge() {
    }

    public Badge(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    //TODO: Add badge properties
}
