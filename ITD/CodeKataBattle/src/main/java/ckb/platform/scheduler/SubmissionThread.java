package ckb.platform.scheduler;

import ckb.platform.entities.Battle;
import ckb.platform.entities.Educator;
import ckb.platform.entities.Student;
import ckb.platform.entities.Team;
import ckb.platform.gmailAPI.GmailAPI;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SubmissionThread extends Thread {
    private final Date targetDate;
    private final Battle battle;

    public SubmissionThread(Battle battle) {
        //this.targetDate = battle.getFinalSubmissionDeadline(); //TODO: uncomment this line
        this.battle = battle;

        // TODO: Remember to remove the following, which is for testing purpose only
        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.FEBRUARY, 1, 13, 45, 0);
        this.targetDate = calendar.getTime();
    }

    @Override
    public void run() {
        // Calculate the milliseconds till the end deadline
        long millisecondsDifference = targetDate.getTime() - System.currentTimeMillis();
        System.out.println("Submission " + battle.getName() + " Tempo corrente: " + new Date() + " Tempo finale: " + targetDate + " Differenza: " + Duration.ofMillis(millisecondsDifference).toHours());

        // Sleep for the amount of time to wait
        try {
            sleep(millisecondsDifference);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Send email");

        // Get educator that created the battle
        Educator battleOwner = battle.getCreator();

        // Get the students in the battle
        List<Team> teamsSubscribed = battle.getTeams();
        List<Student> studentsToNotify = new ArrayList<>();
        for (Team t : teamsSubscribed)
            studentsToNotify.addAll(t.getStudents());

        new Thread(() -> {
            // Prepare Email to send
            GmailAPI gmailSender;
            try {
                gmailSender = new GmailAPI();
            } catch (GeneralSecurityException | IOException e) {
                throw new RuntimeException(e);
            }

            if(battle.getManualEvaluation()){
                String subject = "MANUAL EVALUATION available for battle " + battle.getName();
                String bodyMsg = "Hi " + battleOwner.getFirstName() + ",\n\n" +
                                 "the battle you created " + battle.getName() + " is waiting for you to perform manual evaluation.\n" +
                                 "You can find CKB Platform at the following link: http://localhost:8080/ckb_platform\n\n" +
                                 "Best regards,\n CKB Team";

                // Send Email to the educator
                try {
                    gmailSender.sendEmail(subject, bodyMsg, battleOwner.getEmail());
                } catch (IOException | MessagingException e) {
                    throw new RuntimeException(e);
                }
            } else {
                String subject = "CLOSE BATTLE " + battle.getName();

                // Send Email to each student in battle
                for (Student s : studentsToNotify) {
                    String bodyMsg = "Hi " + s.getFirstName() + ",\n\n" +
                                     "Battle " + battle.getName() + " has been closed\n" +
                                     "You can now find the final ranking\n" +
                                     "You can find CKB Platform at the following link: http://localhost:8080/ckb_platform\n\n" +
                                     "Best regards,\n CKB Team";

                    try {
                        gmailSender.sendEmail(subject,bodyMsg, s.getEmail());
                    } catch (IOException | MessagingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }
}
