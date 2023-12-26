package ckb.platform.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;


@Entity @Table(name = "Educator")
public class Educator extends User{

    @ManyToMany
    private List<Tournament> ownedTournaments;
    @OneToMany (mappedBy = "id")
    private List<Battle> ownedBattles;

    public Educator(String firstName, String lastName, String email) {
        super(firstName, lastName, email);
        ownedTournaments = new ArrayList<Tournament>();
        ownedBattles = new ArrayList<Battle>();
    }

    public Educator() {}

    public void addTournament(Tournament tournament){
        ownedTournaments.add(tournament);
    }

    public void addBattle(Battle battle){
        ownedBattles.add(battle);
    }

    public List<Tournament> getOwnedTournaments(){
        return ownedTournaments;
    }

    public List<Battle> getOwnedBattles(){
        return ownedBattles;
    }

    public void setOwnedTournaments(List<Tournament> ownedTournaments){
        this.ownedTournaments = ownedTournaments;
    }

    public void setOwnedBattles(List<Battle> ownedBattles){
        this.ownedBattles = ownedBattles;
    }

    @Override
    public String toString() {
        return "Educator{" +
                "id=" + super.getId() +
                ", firstName='" + super.getFirstName() +
                ", lastName='" + super.getLastName()+
                ", email='" + super.getEmail() +
                '}';
    }
}
