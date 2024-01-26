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
        //this.targetDate = battle.getRegistrationDeadline();
        this.battle = battle;

        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.JANUARY, 26, 00, 52, 0);
        this.targetDate = calendar.getTime();
    }

    @Override
    public void run() {
        // Calculate the milliseconds till the end deadline
        long millisecondsDifference = targetDate.getTime() - System.currentTimeMillis();

        System.out.println("Registration Tempo corrente: " + System.currentTimeMillis() + " Tempo finale: " + targetDate.getTime() + " Differenza: " + millisecondsDifference);
        System.out.println("Registration Tempo corrente: " + new Date() + " Tempo finale: " + targetDate + " Differenza: " + Duration.ofMillis(millisecondsDifference).toHours());

        // Sleep for the amount of time to wait
        try {
            sleep(millisecondsDifference);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Invio email");

        // Get first student for each team in the battle
        List<Team> teamsSubscribed = battle.getTeams();
        List<Student> studentsToNotify = new ArrayList<>();
        for (Team t : teamsSubscribed) {
            studentsToNotify.add(t.getStudents().get(0));
        }

        // Create repository
        GitHubAPI gitHubAPI = new GitHubAPI();
        gitHubAPI.createRepository(battle, "Submission deadline on: " + battle.getFinalSubmissionDeadline());

        // Prepare Email to send
        GmailAPI gmailSender = null;
        try {
            gmailSender = new GmailAPI();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        String subject = battle.getName() + " repository is created";
        String bodyMsg = "Hi, we are pleased to inform you that\n" +
                battle.getName() + " repository is now created\n\n" +
                "Now you must fork it and start working\n\n" +
                "You can find it at: \n" +
                "https://github.com/CodeKataBattlePlatform/" + battle.getName();

        // Send Email to each first student in battle
        for (Student s : studentsToNotify) {
            try {
                gmailSender.sendEmail(subject,bodyMsg, s.getEmail());
            } catch (IOException | MessagingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
