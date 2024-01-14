package ckb.platform.formParser;

import java.util.Date;

public class CreateTournamentRequest {
    private String tournamentName;
    private Date registerDeadline;

    // If it will be implemented need to be changed
    private String tournamentBadgesList;

    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    public Date getRegisterDeadline() {
        return registerDeadline;
    }

    public void setRegisterDeadline(Date registerDeadline) {
        this.registerDeadline = registerDeadline;
    }

    public String getTournamentBadgesList() {
        return tournamentBadgesList;
    }

    public void setTournamentBadgesList(String tournamentBadgesList) {
        this.tournamentBadgesList = tournamentBadgesList;
    }
}
