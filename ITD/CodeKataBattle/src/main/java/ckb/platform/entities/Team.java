package ckb.platform.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity @Table(name = "Team")
public class Team {
    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

    @ManyToOne @JoinColumn(name = "battle_id")
    private Battle battle;

    private String name;
    private int score;

    @ManyToMany
    private List<Student> students;

    //TODO: Secondo me bisogna aggiungere anche Torneo


    public Team() {
    }

    public Team(Battle battle, Student student) {
        this.battle = battle;
        students = new ArrayList<Student>();
        students.add(student);
    }

    public Team( String name, Battle battle) {
        this.name = name;
        this.battle = battle;
        students = new ArrayList<Student>();
        this.score = 0;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void addStudent(Student student){
        students.add(student);
    }

    public void removeStudent(Student student){
        students.remove(student);
    }

    public void setBattle(Battle battle){
        this.battle = battle;
    }

    public Battle getBattle(){
        return battle;
    }

    public List<Student> getStudents(){
        return students;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", battle='" + battle +
                ", students='" + students +
                ", name='" + name + '\'' +
                ", score='" + score + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Team )) return false;
        return id != null && id.equals(((Team) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }


}
