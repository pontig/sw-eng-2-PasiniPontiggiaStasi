package ckb.platform.formParser;

import java.util.List;

public class JoinBattleRequest {
    private String name;
    private Long battleId;
    private List<String> studentsEmail;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getBattleId() {
        return battleId;
    }

    public void setBattleId(Long battleId) {
        this.battleId = battleId;
    }


    public List<String> getStudentsEmail() {
        return studentsEmail;
    }

    public void setStudentsEmail(List<String> studentsEmail) {
        this.studentsEmail = studentsEmail;
    }
}
