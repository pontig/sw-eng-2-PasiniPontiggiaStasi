package ckb.platform.formParser;

public class ListGroupsForManualRequest {
    private Long team_id;
    private int score;

    public int getScore() {
        return score;
    }

    public Long getTeam_id() {
        return team_id;
    }

    public void setTeam_id(Long team_id) {
        this.team_id = team_id;
    }
    public void setScore(int score) {
        this.score = score;
    }
}
