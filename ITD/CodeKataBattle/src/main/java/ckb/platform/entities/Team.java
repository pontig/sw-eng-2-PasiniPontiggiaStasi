package ckb.platform.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity @Table(name = "teams")
public class Team {
    private @Id @GeneratedValue Long id;

    @ManyToOne @JoinColumn(name = "battle_id")
    private Battle battle;

    @ManyToMany
    private List<Student> students;

    public Team() {
    }

    public Team(Battle battle, Student student) {
        this.battle = battle;
        students = new ArrayList<Student>();
        students.add(student);
    }

    public Long getId() {
        return id;
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

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", battle='" + battle +
                ", students='" + students +
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
