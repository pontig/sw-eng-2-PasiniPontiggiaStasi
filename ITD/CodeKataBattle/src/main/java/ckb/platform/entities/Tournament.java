package ckb.platform.entities;

import ckb.platform.Pair;
import jakarta.persistence.*;

import java.util.*;
import java.util.stream.Collectors;

@Entity @Table(name = "Tournament")
public class Tournament {

    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    private String name;
    private Date subscriptionDeadline;
    private Date endDate;

    @ManyToOne @JoinColumn(name = "user_id",  referencedColumnName = "id")
    private Educator creator;

    @ManyToMany (fetch=FetchType.EAGER)
    private List<Educator> grantedEducators;
    @ManyToMany (fetch=FetchType.EAGER)
    private List<Student> subscribedStudents;

    @OneToMany(fetch=FetchType.EAGER, mappedBy = "tournament", cascade = CascadeType.ALL)
    private List<Battle> battles;

    @ManyToMany(fetch=FetchType.EAGER)
    private List<Badge> badges;


    public Tournament(String name, Date subscriptionDeadline, Date endDate, Educator creator) {
        this.name = name;
        this.subscriptionDeadline = subscriptionDeadline;
        this.endDate = endDate;
        this.creator = creator;
        grantedEducators = new ArrayList<>();
        grantedEducators.add(creator);
        subscribedStudents = new ArrayList<>();
        battles = new ArrayList<>();
        badges = new ArrayList<>();

    }

    public Tournament() {}

    public void addEducator(Educator educator){
        grantedEducators.add(educator);
    }

    public void addStudent(Student student){
        subscribedStudents.add(student);
    }

    public void addBattle(Battle battle){
        battles.add(battle);
    }

    public void addBadge(Badge badge){
        badges.add(badge);
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Date getSubscriptionDeadline() {
        return this.subscriptionDeadline;
    }

    public Date getEndDate() { return this.endDate; }

    public Educator getCreator() {
        return this.creator;
    }

    public List<Educator> getGrantedEducators() {
        return this.grantedEducators;
    }

    public List<Student> getSubscribedStudents() {
        return this.subscribedStudents;
    }

    public List<Battle> getBattles() {
        return this.battles;
    }

    public List<Badge> getBadges() {
        return this.badges;
    }

    public Map<Student, Integer> getRanking() {
        return this.battles
                .stream()
                .map(Battle::getTeams)
                .flatMap(Collection::stream)
                .map(e -> e.getStudents()
                        .stream()
                        .map(s -> new Pair<>(s, e.getFinalScore())).toList())
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(e -> e.first, Collectors.summingInt(e -> e.second)));
    }


    @Override
    public String toString() {
        return "Tournament{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", subscriptionDeadline=" + subscriptionDeadline +
                ", creator=" + creator +
                ", grantedEducators=" + grantedEducators +
                ", subscribedStudents=" + subscribedStudents +
                ", battles=" + battles +
                ", badges=" + badges +
                //", ranking=" + ranking +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Tournament)) {
            return false;
        }
        Tournament tournament = (Tournament) o;
        return Objects.equals(id, tournament.id) && Objects.equals(name, tournament.name) && Objects.equals(subscriptionDeadline, tournament.subscriptionDeadline) && Objects.equals(creator, tournament.creator) && Objects.equals(grantedEducators, tournament.grantedEducators) && Objects.equals(subscribedStudents, tournament.subscribedStudents) && Objects.equals(battles, tournament.battles) && Objects.equals(badges, tournament.badges) /*&& Objects.equals(ranking, tournament.ranking)*/;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, subscriptionDeadline, creator);
    }

    public Boolean isActive() {
        return this.endDate == null;
    }
}
