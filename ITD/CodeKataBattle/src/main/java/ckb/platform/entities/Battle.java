package ckb.platform.entities;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "Battle")
public class Battle {

    private @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Educator creator;
    private Boolean manualEvaluation;
    private String name;
    private Boolean hasBeenEvaluated;
    private String language;
    private int minStudents;
    private int maxStudents;
    private Date registrationDeadline;
    private Date finalSubmissionDeadline;
    private Date openDate;
    private String description;
    @OneToMany(mappedBy = "battle", cascade = CascadeType.ALL)
    private List<Team> teams;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "tournament_id", referencedColumnName = "id")
    private Tournament tournament;
    private boolean reliability;
    private boolean maintainability;
    private boolean security;

    public Battle() {
    }

    public Battle(
            String name,
            Date openDate,
            Date registrationDeadline,
            Date finalSubmissionDeadline,
            String language,
            Boolean manualEvaluation,
            int minStudents,
            int maxStudents,
            Educator creator,
            Tournament tournament,
            Boolean hasBeenEvaluated,
            String description,
            boolean reliability,
            boolean maintainability,
            boolean security
    ) {
        this.creator = creator;
        this.manualEvaluation = manualEvaluation;
        this.hasBeenEvaluated = hasBeenEvaluated;
        this.minStudents = minStudents;
        this.maxStudents = maxStudents;
        this.registrationDeadline = registrationDeadline;
        this.finalSubmissionDeadline = finalSubmissionDeadline;
        this.tournament = tournament;
        this.name = name;
        this.openDate = openDate;
        this.language = language;
        this.description = description;
        this.reliability = reliability;
        this.maintainability = maintainability;
        this.security = security;
        teams = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Date getOpenDate() {
        return openDate;
    }

    public int getPhase() {
        if (new Date().before(registrationDeadline)) {
            return 1;
        } else if (new Date().before(finalSubmissionDeadline)) {
            return 2;
        } else if (Boolean.TRUE.equals(manualEvaluation) && Boolean.TRUE.equals(!hasBeenEvaluated)) {
            return 3;
        } else {
            return 4;
        }
    }

    public String getDescription() {
        return description;
    }

    public String getLanguage() {
        return language;
    }

    public String getTitle() {
        return this.name;
    }

    public Boolean isClosed() {
        return (new Date().after(finalSubmissionDeadline));
    }

    public void addTeam(Team team) {
        teams.add(team);
    }

    public void setManualEvaluation(Boolean manualEvaluation) {
        this.manualEvaluation = manualEvaluation;
    }

    public void setHasBeenEvaluated() {
        this.hasBeenEvaluated = true;
    }

    public void setMinStudents(int minStudents) {
        this.minStudents = minStudents;
    }

    public void setMaxStudents(int maxStudents) {
        this.maxStudents = maxStudents;
    }

    public void setRegistrationDeadline(Date registrationDeadline) {
        this.registrationDeadline = registrationDeadline;
    }

    public void setFinalSubmissionDeadline(Date finalSubmissionDeadline) {
        this.finalSubmissionDeadline = finalSubmissionDeadline;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public Long getId() {
        return id;
    }

    public Educator getCreator() {
        return creator;
    }

    public Map<Team, Integer> getRanking() {
        return this.teams
                .stream()
                .map(team -> {
                    Map<Team, Integer> ranking = new HashMap<>();
                    ranking.put(team, team.getFinalScore());
                    return ranking;
                })
                .collect(HashMap::new, HashMap::putAll, HashMap::putAll);
    }

    public Boolean getManualEvaluation() {
        return manualEvaluation;
    }

    public Boolean getHasBeenEvaluated() {
        return hasBeenEvaluated;
    }

    public int getMinStudents() {
        return minStudents;
    }

    public int getMaxStudents() {
        return maxStudents;
    }

    public Date getRegistrationDeadline() {
        return registrationDeadline;
    }

    public Date getFinalSubmissionDeadline() {
        return finalSubmissionDeadline;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public Boolean isSubscribed(Student student) {
        for (Team team : teams) {
            if (team.getStudents().contains(student)) {
                return true;
            }
        }
        return false;
    }

    public Boolean satisfiesMinStudents() {
        return teams.size() >= minStudents;
    }

    public Boolean satisfiesMaxStudents() {
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
                //&& Objects.equals(this.ranking, battle.ranking) && Objects.equals(this.manualEvaluation, battle.manualEvaluation)
                && Objects.equals(this.hasBeenEvaluated, battle.hasBeenEvaluated)
                && Objects.equals(this.minStudents, battle.minStudents) && Objects.equals(this.maxStudents, battle.maxStudents)
                && Objects.equals(this.registrationDeadline, battle.registrationDeadline) && Objects.equals(this.finalSubmissionDeadline, battle.finalSubmissionDeadline)
                && Objects.equals(this.teams, battle.teams) && Objects.equals(this.tournament, battle.tournament);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.creator, this.manualEvaluation, this.hasBeenEvaluated, this.minStudents, this.maxStudents, this.registrationDeadline, this.finalSubmissionDeadline, this.tournament);
    }

    @Override
    public String toString() {
        return "Battle{" + "id=" + this.id + ", creator='" + this.creator.getId() + ", manualEvaluation='" + this.manualEvaluation + '\'' + ", hasBeenEvaluated='" + this.hasBeenEvaluated + '\'' + ", minStudents='" + this.minStudents + '\'' + ", maxStudents='" + this.maxStudents + '\'' + ", registrationDeadline='" + this.registrationDeadline + '\'' + ", finalSubmissionDeadline='" + this.finalSubmissionDeadline + '\'' + ", teams='" + this.teams + '\'' + ", tournament='" + this.tournament.getId() + '\'' + '}';
    }

    public boolean isMaintainability() {
        return maintainability;
    }

    public boolean isReliability() {
        return reliability;
    }

    public boolean isSecurity() {
        return security;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
