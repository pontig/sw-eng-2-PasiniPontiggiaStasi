package ckb.platform.formParser;

public class ShareTournamentRequest {
    private Long id;                // Tournament ID to share
    private Long educatorId;        // ID of the educator to invite

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEducatorId() {
        return educatorId;
    }

    public void setEducatorId(Long educatorId) {
        this.educatorId = educatorId;
    }
}
