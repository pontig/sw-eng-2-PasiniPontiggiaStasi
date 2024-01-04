package ckb.platform.entities;

import jakarta.persistence.*;

import java.util.List;


@Entity
//@DiscriminatorValue("0")
@Table(name = "Student")
public class Student extends User{

    @ManyToMany
    List<Badge> achieveBadges;

    @ManyToMany
    List<Tournament> tournaments;

    @ManyToMany
    List<Battle> battles;

    public Student(String firstName, String lastName, String email, String password, String home_uni) {
        super(firstName, lastName, email, password, false, home_uni);
    }

    public Student() {}

    public void addBadge(Badge badge){
        achieveBadges.add(badge);
    }

    public void addTournament(Tournament tournament){
        tournaments.add(tournament);
    }

    public void addBattle(Battle battle){
        battles.add(battle);
    }

    public List<Badge> getAchieveBadges(){
        return achieveBadges;
    }

    public List<Tournament> getTournaments(){
        return tournaments;
    }

    public List<Battle> getBattles(){
        return battles;
    }

    public void setAchieveBadges(List<Badge> achieveBadges){
        this.achieveBadges = achieveBadges;
    }

    public void setTournaments(List<Tournament> tournaments){
        this.tournaments = tournaments;
    }

    public void setBattles(List<Battle> battles){
        this.battles = battles;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + super.getId() +
                ", firstName='" + super.getFirstName() +
                ", lastName='" + super.getLastName()+
                ", email='" + super.getEmail() +
                '}';
    }
}
