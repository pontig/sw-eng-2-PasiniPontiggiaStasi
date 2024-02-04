package ckb.platform.formParser;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

public class CreateBattleRequest {
    private Long tournamentId;
    private String battleName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date registerDeadline;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date submissionDeadline;
    private String language;
    private int minSize;
    private int maxSize;
    private boolean manualEvaluation;
    private MultipartFile ckbProblem;
    private MultipartFile buildScript;
    private MultipartFile test;
    private String description;
    private boolean reliability, maintainability, security;

    // Getter and Setter methods

    public Long getTournamentId() {
        return tournamentId;
    }
    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getBattleName() {
        return battleName;
    }

    public void setBattleName(String battleName) {
        this.battleName = battleName;
    }

    public Date getRegisterDeadline() {
        return registerDeadline;
    }

    public void setRegisterDeadline(Date registerDeadline) {
        this.registerDeadline = registerDeadline;
    }

    public Date getSubmissionDeadline() {
        return submissionDeadline;
    }

    public void setSubmissionDeadline(Date submissionDeadline) {
        this.submissionDeadline = submissionDeadline;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getMinSize() {
        return minSize;
    }

    public void setMinSize(int minSize) {
        this.minSize = minSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public boolean isManualEvaluation() {
        return manualEvaluation;
    }
    public void setManualEvaluation(boolean manualEvaluation) {
        this.manualEvaluation = manualEvaluation;
    }

    public MultipartFile getCkbProblem() {
        return ckbProblem;
    }
    public void setCkbProblem(MultipartFile ckbProblem) {
        this.ckbProblem = ckbProblem;
    }

    public boolean isSecurity() {
        return security;
    }

    public boolean isReliability() {
        return reliability;
    }

    public boolean isMaintainability() {
        return maintainability;
    }

    public String getDescription() {
        return description;
    }

    public MultipartFile getBuildScript() {
        return buildScript;
    }
    public void setBuildScript(MultipartFile buildScript) {
        this.buildScript = buildScript;
    }

    public MultipartFile getTest() {
        return test;
    }
    public void setTest(MultipartFile test) {
        this.test = test;
    }

    public boolean getReliability() {
        return reliability;
    }

    public boolean getMaintainability() {
        return maintainability;
    }

    public void setMaintainability(boolean maintainability) {
        this.maintainability = maintainability;
    }

    public boolean getSecurity() {
        return security;
    }

    public void setSecurity(boolean security) {
        this.security = security;
    }
}
