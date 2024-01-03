package ckb.platform.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.List;


@Entity @Table(name = "Student")
public class Student extends User{

    @ManyToMany
    List<Badge> achieveBadges;

    @ManyToMany
    List<Tournament> tournaments;

    @ManyToMany
    List<Battle> battles;

    public Student(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
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
