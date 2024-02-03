package ckb.platform.entities;

import jakarta.persistence.*;

@Entity @Table(name = "Badge")
public class Badge {

    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

    public Badge() {
    }

    public Badge(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
