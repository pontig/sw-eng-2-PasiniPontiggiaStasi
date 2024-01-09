package ckb.platform.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@DiscriminatorValue("1")
public class Educator extends User{

    @ManyToMany (cascade = CascadeType.MERGE, mappedBy = "grantedEducators")
    private List<Tournament> ownedTournaments;
    @OneToMany (mappedBy = "creator", cascade = CascadeType.ALL)
    private List<Battle> ownedBattles;

    public Educator( String firstName, String lastName, String home_uni, String email, String password) {
        super( firstName, lastName, email, password, true, home_uni);
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
