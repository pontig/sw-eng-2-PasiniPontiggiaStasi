package ckb.platform.scheduler;

import ckb.platform.entities.Battle;
import ckb.platform.entities.Educator;
import ckb.platform.entities.Student;
import ckb.platform.entities.Team;
import ckb.platform.gmailAPI.GmailAPI;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SubmissionThread extends Thread {
    private final Date targetDate;
    private final Battle battle;

    public SubmissionThread(Battle battle) {
        this.targetDate = battle.getFinalSubmissionDeadline();
        this.battle = battle;
    }

    @Override
    public void run() {
        // Calculate the milliseconds till the end deadline
        long millisecondsDifference = targetDate.getTime() - System.currentTimeMillis();

        // Sleep for the amount of time to wait
        try {
            sleep(millisecondsDifference * TimeUnit.DAYS.toMillis(1));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Invio email");

        // Get educator that created the battle
        Educator battleOwner = battle.getCreator();

        // Get the students in the battle
        List<Team> teamsSubscribed = battle.getTeams();
        List<Student> studentsToNotify = new ArrayList<>();
        for (Team t : teamsSubscribed) {
            studentsToNotify.addAll(t.getStudents());
        }

        // Prepare Email to send
        GmailAPI gmailSender = null;
        try {
            gmailSender = new GmailAPI();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

        if(battle.getManualEvaluation()){
            String subject = battle.getName() + " is ready for manual evaluation";
            String bodyMsg = "Hi " + battleOwner.getFirstName() + " the battle " + battle.getName() +
                    " is ready for manual evaluation\n" +
                    "You can find the manual evaluation at : \n" +
                    "https://www.youtube.com";

            // Send Email to the educator
            try {
                gmailSender.sendEmail(subject,bodyMsg, battleOwner.getEmail());
            } catch (IOException | MessagingException e) {
                throw new RuntimeException(e);
            }
        } else {
            String subject = battle.getName() + " is closed and the final ranking is available";
            String bodyMsg = "Hi, we are pleased to inform you that\n" +
                    battle.getName() + " battle is now closed\n\n" +
                    "You can find the final ranking at : \n" +
                    "https://www.youtube.com";

            // Send Email to each student in battle
            for (Student s : studentsToNotify) {
                try {
                    gmailSender.sendEmail(subject,bodyMsg, s.getEmail());
                } catch (IOException | MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
