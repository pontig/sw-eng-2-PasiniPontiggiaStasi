package ckb.platform.scheduler;

import ckb.platform.entities.Battle;
import ckb.platform.entities.Student;
import ckb.platform.entities.Team;
import ckb.platform.gitHubAPI.GitHubAPI;
import ckb.platform.gmailAPI.GmailAPI;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RegistrationThread extends Thread {
    private final Date targetDate;
    private final Battle battle;

    public RegistrationThread(Battle battle) {
        //this.targetDate = battle.getRegistrationDeadline(); //TODO: uncomment this line
        this.battle = battle;

        // TODO: Remember to remove the following, which is for testing purpose only
        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.FEBRUARY, 1, 13, 30, 0);
        this.targetDate = calendar.getTime();
    }

    @Override
    public void run() {
        // Calculate the milliseconds till the end deadline
        long millisecondsDifference = targetDate.getTime() - System.currentTimeMillis();
        System.out.println("Registration " + battle.getName() + " Tempo corrente: " + new Date() + " Tempo finale: " + targetDate + " Differenza: " + Duration.ofMillis(millisecondsDifference).toHours());

        // Sleep for the amount of time to wait
        try {
            sleep(millisecondsDifference);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Send email end registration");

        // TODO: remove students and team that do not respect the boundaries

        // Get first student for each team in the battle
        List<Team> teamsSubscribed = battle.getTeams();
        List<Student> studentsToNotify = new ArrayList<>();
        for (Team t : teamsSubscribed) {
            System.out.println(t + " \n" + t.getStudents().getFirst());
            studentsToNotify.add(t.getStudents().getFirst());
        }

        // TODO: talk about the repo organization
        // Create repository
        int response;
        GitHubAPI gitHubAPI = new GitHubAPI();
        response = gitHubAPI.createRepository(battle, "Submission deadline on: " + battle.getFinalSubmissionDeadline() + " for battle " + battle.getName() + " in tournament " + battle.getTournament().getName());

        if(response != 201)
            System.out.println("Error in creating repo");

        try {
            response = gitHubAPI.createFolder(battle, "CKBProblem", battle.getId().toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(response != 201)
            System.out.println("Error in creating folder CKB Problem");

        try {
            response = gitHubAPI.createFolder(battle, "Rules", "README");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(response != 201)
            System.out.println("Error in creating README");

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