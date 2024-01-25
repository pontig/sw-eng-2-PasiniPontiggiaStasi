package ckb.platform.formParser;

public class RepoPullRequest {
    private String repository;
    private String pusher;

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getPusher() {
        return pusher;
    }

    public void setPusher(String pusher) {
        this.pusher = pusher;
    }
}
