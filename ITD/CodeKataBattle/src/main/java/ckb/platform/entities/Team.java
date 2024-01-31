package ckb.platform.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Team")
public class Team {
    private @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

    @ManyToOne
    @JoinColumn(name = "battle_id")
    private Battle battle;

    private String name;
    private int staticAnalysisScore = 0;
    private int timelinessScore = 0;
    private int testScore = 0;
    private Integer manualScore = null;
    @ManyToMany
    private List<Student> students;
    private String repo;

    //TODO: Secondo me bisogna aggiungere anche Torneo --> no, perché se sai la battaglia sai anche il torneo

    public Team() {
    }

    public Team(Battle battle, Student student) {
        this.battle = battle;
        students = new ArrayList<Student>();
        students.add(student);
    }

    public Team(String name, Battle battle) {
        this.name = name;
        this.battle = battle;
        students = new ArrayList<Student>();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public void removeStudent(Student student) {
        students.remove(student);
    }

    public void setBattle(Battle battle) {
        this.battle = battle;
    }

    public Battle getBattle() {
        return battle;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", battle='" + battle.getId() +
                ", students='" + students +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Team)) return false;
        return id != null && id.equals(((Team) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public Object getCode() {
        return repo;
    }

    public Integer getManualScore() {
        return manualScore;
    }

    public int getAutomaticScore() {
        return (testScore + staticAnalysisScore + timelinessScore) / 3;
    }
    public int getFinalScore() {
        if (!battle.getHasBeenEvaluated() || !battle.getManualEvaluation())
            return getAutomaticScore();
        else
            return (manualScore + getAutomaticScore()) / 2;
    }
    public void setManualScore(int manualScore) {
        this.manualScore = manualScore;
    }

    public int getStaticAnalysisScore() {
        return staticAnalysisScore;
    }

    public int getTimelinessScore() {
        return timelinessScore;
    }

    public int getTestScore() {
        return testScore;
    }

    public void setStaticAnalysisScore(int staticAnalysisScore) {
        this.staticAnalysisScore = staticAnalysisScore;
    }

    public void setTimelinessScore(int timelinessScore) {
        this.timelinessScore = timelinessScore;
    }

    public void setTestScore(int testScore) {
        this.testScore = testScore;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }
}
