package ckb.platform.scheduler;

import ckb.platform.entities.Battle;
import ckb.platform.entities.Student;
import ckb.platform.entities.Team;
import ckb.platform.gitHubAPI.GitHubAPI;
import ckb.platform.gmailAPI.GmailAPI;
import ckb.platform.repositories.BattleRepository;
import ckb.platform.repositories.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RegistrationThread extends Thread {

    private static final Logger log = LoggerFactory.getLogger(RegistrationThread.class);
    private final TeamRepository teamRepository;
    private final Date targetDate;
    private final Battle battle;
    private final BattleRepository battleRepository;

    public RegistrationThread(BattleRepository battleRepository, TeamRepository teamRepository, Battle battle) {
        this.teamRepository = teamRepository;
        this.battleRepository = battleRepository;
        this.targetDate = battle.getRegistrationDeadline();
        this.battle = battle;

        //Remember to remove the following, which is for testing purpose only
        //Calendar calendar = Calendar.getInstance();
        //calendar.set(2024, Calendar.FEBRUARY, 4, 17, 17, 0);
        //this.targetDate = calendar.getTime();
    }

    @Override
    public void run() {
        // Calculate the milliseconds till the end deadline
        long millisecondsDifference = targetDate.getTime() - System.currentTimeMillis();
        log.info("Registration " + battle.getName() + " Tempo corrente: " + new Date() + " Tempo finale: " + targetDate + " Differenza: " + Duration.ofMillis(millisecondsDifference).toHours());

        // Sleep for the amount of time to wait
        try {
            sleep(millisecondsDifference);
        } catch (InterruptedException e) {
            log.error("Error while waiting for registration deadline", e);
        }

        log.info("Send email end registration");

        // Remove team that do not respect the boundaries
        List<Team> teamsSubscribed = teamRepository.getTeamInBattle(battle);
        for(Team t : teamsSubscribed){
            if(t.getStudents().size() < t.getBattle().getMinStudents() || t.getStudents().size() > t.getBattle().getMaxStudents()){
                new Thread(() -> {
                    // Prepare Email to send
                    GmailAPI gmailSender;
                    try {
                        gmailSender = new GmailAPI();
                    } catch (GeneralSecurityException | IOException e) {
                        throw new RuntimeException(e);
                    }
                    String subject = "DELETE TEAM " + t.getName() + " from battle " + t.getBattle().getName();

                    // Send Email to each first student in battle
                    for (Student s : t.getStudents()) {
                        String bodyMsg = "Hi " + s.getFirstName() + ",\n\n" +
                                "we are sorry but your team " + t.getName() + " has been deleted.\n" +
                                "Battle " + t.getBattle().getName() + "required a number of students between " + t.getBattle().getMinStudents() + " and  " + t.getBattle().getMaxStudents() + "\n" +
                                "Your team was of " + t.getStudents().size() + ", so it has been deleted\n\n" +
                                "See you next time,\n CKB Team";
                        try {
                            gmailSender.sendEmail(subject, bodyMsg, s.getEmail());
                        } catch (IOException | MessagingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();

                teamRepository.delete(t);
            }
        }

        // Get first student for each team in the battle
        List<Student> studentsToNotify = new ArrayList<>();
        for (Team t : teamsSubscribed) {
            if(t.getStudents() != null){
                log.info(t.getStudents().get(0).getEmail());
                studentsToNotify.add(t.getStudents().get(0));
            }
        }

        new Thread(() -> {
            // Create repository
            int response;
            GitHubAPI gitHubAPI = new GitHubAPI();
            response = gitHubAPI.createRepository(battle, "Submission deadline on: " + battle.getFinalSubmissionDeadline() + " for battle " + battle.getName() + " in tournament " + battle.getTournament().getName());

            if(response != 201)
                log.error("Error in creating repo - response: " + response);

            try {
                gitHubAPI.createFolder(battle, "CKBProblem");                   // CKB Problem PDF description
                gitHubAPI.createFolder(battle, "Rules");                        // README.md
                gitHubAPI.createFolder(battle, "main");                         // Main folder of project
                if(battle.getLanguage().equals("Python"))
                    gitHubAPI.createFolder(battle, "i_m");
                gitHubAPI.createFolder(battle, "Test");                         // Test folder of project
                if(battle.getLanguage().equals("Python"))
                    gitHubAPI.createFolder(battle, "i_t");
                response = gitHubAPI.createFolder(battle, "BuildScript");             // Build script
            } catch (IOException e) {
                log.error("An error occurred in folder creation", e);
                throw new RuntimeException(e);
            }
            if(response != 201)
                log.error("An error occurred in folder creation - response: " + response);

            battle.setDescription("https://github.com/CodeKataBattlePlatform/" + battle.getName().replace(" ", "-"));

            battleRepository.save(battle);
        }).start();

        new Thread(() -> {
            // Prepare Email to send
            GmailAPI gmailSender;
            try {
                gmailSender = new GmailAPI();
            } catch (GeneralSecurityException | IOException e) {
                throw new RuntimeException(e);
            }
            String subject = "REPOSITORY CREATED " + battle.getName().replace(" ", "-");

            // Send Email to each first student in battle
            for (Student s : studentsToNotify) {
                String bodyMsg = "Hi " + s.getFirstName() + ",\n\n" +
                                 "we are pleased to inform you that the repository for " + battle.getName() + " is now available.\n" +
                                 "From now follow the instructions on the repo, fork it and invite your team members\n" +
                                 "You can find the repository at the following link: https://github.com/CodeKataBattlePlatform/" + battle.getName().replace(" ", "-") + "\n\n" +
                                 "Best regards,\n CKB Team";

                try {
                    gmailSender.sendEmail(subject, bodyMsg, s.getEmail());
                } catch (IOException | MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}