package ckb.platform.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@DiscriminatorValue("0")
public class Student extends User{

    @ManyToMany
    List<Badge> achieveBadges;

    @ManyToMany (cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch=FetchType.EAGER, mappedBy = "subscribedStudents")
    List<Tournament> tournaments;

    public Student(String firstName, String lastName, String email, String password, String home_uni) {
        super(firstName, lastName, email, password, false, home_uni);
        this.tournaments = new ArrayList<>();
        this.achieveBadges = new ArrayList<>();
    }

    public Student() {}

    public void addBadge(Badge badge){
        achieveBadges.add(badge);
    }

    public void addTournament(Tournament tournament){
        tournaments.add(tournament);
    }

    public List<Badge> getAchieveBadges(){
        return achieveBadges;
    }

    public List<Tournament> getTournaments(){
        return tournaments;
    }
    public void setAchieveBadges(List<Badge> achieveBadges){
        this.achieveBadges = achieveBadges;
    }

    public void setTournaments(List<Tournament> tournaments){
        this.tournaments = tournaments;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        if (!super.equals(o)) return false;
        Student student = (Student) o;
        return getId().equals(student.getId()) &&
                getFirstName().equals(student.getFirstName()) &&
                getLastName().equals(student.getLastName()) &&
                getEmail().equals(student.getEmail()) &&
                getPassword().equals(student.getPassword()) &&
                getAchieveBadges().equals(student.getAchieveBadges()) &&
                getTournaments().equals(student.getTournaments());
    }
}
