package ckb.platform.formParser;

public class InviteSinglePersonRequest {
    private String mail;
    private Long team_id;

    public String getMail() {
        return mail;
    }

    public Long getTeam_id() {
        return team_id;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setTeam_id(Long team_id) {
        this.team_id = team_id;
    }
}
