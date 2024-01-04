package ckb.platform.entities;

import jakarta.persistence.*;

import java.util.*;

@Entity @Table(name = "Tournament")
public class Tournament {

    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    private String name;
    private Date subscriptionDeadline;
    private Date endDate;

    @ManyToOne @JoinColumn(name = "educator_id")
    private Educator creator;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Educator> grantedEducators;
    @ManyToMany
    private List<Student> subscribedStudents;

    @OneToMany(mappedBy = "id")
    private List<Battle> battles;

    @ManyToMany
    private List<Badge> badges;

    @ElementCollection
    private Map<Student, Integer> ranking;

    public Tournament(int id, String name, Date subscriptionDeadline, Date endDate, Educator creator) {
        this.id = (long) id;
        this.name = name;
        this.subscriptionDeadline = subscriptionDeadline;
        this.endDate = endDate;
        this.creator = creator;
        grantedEducators = new ArrayList<Educator>();
        grantedEducators.add(creator);
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

    public void setRankingPerStudent(Student student, Integer position){
        ranking.put(student, position);
    }

    public void setRanking(Map<Student, Integer> ranking){
        this.ranking = ranking;
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
        return this.ranking;
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
                ", ranking=" + ranking +
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
        return Objects.equals(id, tournament.id) && Objects.equals(name, tournament.name) && Objects.equals(subscriptionDeadline, tournament.subscriptionDeadline) && Objects.equals(creator, tournament.creator) && Objects.equals(grantedEducators, tournament.grantedEducators) && Objects.equals(subscribedStudents, tournament.subscribedStudents) && Objects.equals(battles, tournament.battles) && Objects.equals(badges, tournament.badges) && Objects.equals(ranking, tournament.ranking);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, subscriptionDeadline, creator);
    }
}
