package ckb.platform.entities;

import jakarta.persistence.*;

import java.util.*;

@Entity @Table(name = "battles")
public class Battle {

    private @Id @GeneratedValue Long id;
    @ManyToOne @JoinColumn(name = "educator_id")
    private Educator creator;
    @ElementCollection
    private Map<Team, Integer> ranking;
    private Boolean manualEvaluation;
    /*
    private Enum language;
    private String Description;
    private TestCase[] testCases;
    * */
    private int minStudents;
    private int maxStudents;
    private Date registrationDeadline;
    private Date finalSubmissionDeadline;
    @OneToMany(mappedBy = "id")
    private List<Team> teams;
    @ManyToOne @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    public Battle() {}

    public Battle(Educator creator, Boolean manualEvaluation, int minStudents, int maxStudents, Date registrationDeadline, Date finalSubmissionDeadline, Tournament tournament) {
        this.creator = creator;
        this.manualEvaluation = manualEvaluation;
        this.minStudents = minStudents;
        this.maxStudents = maxStudents;
        this.registrationDeadline = registrationDeadline;
        this.finalSubmissionDeadline = finalSubmissionDeadline;
        this.tournament = tournament;
        teams = new ArrayList<Team>();
    }

    public Boolean isClosed(){
        return (new Date().after(finalSubmissionDeadline));
    }

    public void addTeam(Team team){
        teams.add(team);
    }

    public void setRanking(Team team, Integer position){
        ranking.put(team, position);
    }

    public void setManualEvaluation(Boolean manualEvaluation){
        this.manualEvaluation = manualEvaluation;
    }

    public void setMinStudents(int minStudents){
        this.minStudents = minStudents;
    }

    public void setMaxStudents(int maxStudents){
        this.maxStudents = maxStudents;
    }

    public void setRegistrationDeadline(Date registrationDeadline){
        this.registrationDeadline = registrationDeadline;
    }

    public void setFinalSubmissionDeadline(Date finalSubmissionDeadline){
        this.finalSubmissionDeadline = finalSubmissionDeadline;
    }

    public void setTournament(Tournament tournament){
        this.tournament = tournament;
    }

    public Long getId(){
        return id;
    }

    public Educator getCreator(){
        return creator;
    }

    public Map<Team, Integer> getRanking(){
        return ranking;
    }

    public Boolean getManualEvaluation(){
        return manualEvaluation;
    }

    public int getMinStudents(){
        return minStudents;
    }

    public int getMaxStudents(){
        return maxStudents;
    }

    public Date getRegistrationDeadline(){
        return registrationDeadline;
    }

    public Date getFinalSubmissionDeadline(){
        return finalSubmissionDeadline;
    }

    public List<Team> getTeams(){
        return teams;
    }

    public Tournament getTournament(){
        return tournament;
    }

    public Boolean isSubscribed(Student student){
        for(Team team : teams){
            if(team.getStudents().contains(student)){
                return true;
            }
        }
        return false;
    }

    public Boolean satisfiesMinStudents(){
        return teams.size() >= minStudents;
    }

    public Boolean satisfiesMaxStudents(){
        return teams.size() <= maxStudents;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (!(o instanceof Battle))
            return false;
        Battle battle = (Battle) o;
        return Objects.equals(this.id, battle.id) && Objects.equals(this.creator, battle.creator)
                && Objects.equals(this.ranking, battle.ranking) && Objects.equals(this.manualEvaluation, battle.manualEvaluation)
                && Objects.equals(this.minStudents, battle.minStudents) && Objects.equals(this.maxStudents, battle.maxStudents)
                && Objects.equals(this.registrationDeadline, battle.registrationDeadline) && Objects.equals(this.finalSubmissionDeadline, battle.finalSubmissionDeadline)
                && Objects.equals(this.teams, battle.teams) && Objects.equals(this.tournament, battle.tournament);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.creator, this.manualEvaluation, this.minStudents, this.maxStudents, this.registrationDeadline, this.finalSubmissionDeadline, this.tournament);
    }

    @Override
    public String toString() {
        return "Battle{" + "id=" + this.id + ", creator='" + this.creator + '\'' + ", ranking='" + this.ranking + '\'' + ", manualEvaluation='" + this.manualEvaluation + '\'' + ", minStudents='" + this.minStudents + '\'' + ", maxStudents='" + this.maxStudents + '\'' + ", registrationDeadline='" + this.registrationDeadline + '\'' + ", finalSubmissionDeadline='" + this.finalSubmissionDeadline + '\'' + ", teams='" + this.teams + '\'' + ", tournament='" + this.tournament + '\'' + '}';
    }

}
