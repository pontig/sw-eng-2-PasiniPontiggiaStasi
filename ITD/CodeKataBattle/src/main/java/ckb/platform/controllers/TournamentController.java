package ckb.platform.controllers;

import ckb.platform.entities.*;
import ckb.platform.exceptions.EducatorNotFoundException;
import ckb.platform.exceptions.StudentNotFoundException;
import ckb.platform.exceptions.TournamentNotFoundException;
import ckb.platform.formParser.CloseTournamentRequest;
import ckb.platform.formParser.CreateTournamentRequest;
import ckb.platform.formParser.JoinTournamentRequest;
import ckb.platform.formParser.ShareTournamentRequest;
import ckb.platform.gmailAPI.GmailAPI;
import ckb.platform.repositories.BattleRepository;
import ckb.platform.repositories.EducatorRepository;
import ckb.platform.repositories.StudentRepository;
import ckb.platform.repositories.TournamentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class TournamentController {

    @Autowired
    private final TournamentRepository tournamentRepository;
    @Autowired
    private final EducatorRepository educatorRepository;
    @Autowired
    private final StudentRepository studentRepository;
    @Autowired
    private final BattleRepository battleRepository;

    TournamentController(TournamentRepository tournamentRepository, EducatorRepository educatorRepository, StudentRepository studentRepository, BattleRepository battleRepository) {
        this.tournamentRepository = tournamentRepository;
        this.educatorRepository = educatorRepository;
        this.studentRepository = studentRepository;
        this.battleRepository = battleRepository;
    }

    @GetMapping("/tournaments/{id}")
    Map<String, Object> one(@PathVariable Long id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new TournamentNotFoundException(id));

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("id", tournament.getId());
        response.put("name", tournament.getName());
        response.put("active", tournament.isActive());
        response.put("battles", tournament.getBattles().stream().map(battle -> {
            Map<String, Object> battleMap = new LinkedHashMap<>();
            battleMap.put("id", battle.getId());
            battleMap.put("name", battle.getName());
            battleMap.put("language", battle.getLanguage());
            battleMap.put("participants", battle.getTeams().stream().reduce(0, (sum, team) -> sum + team.getStudents().size(), Integer::sum));
            battleMap.put("phase", battle.getPhase());

            String daysLeft;
            Date nextStep = switch (battle.getPhase()) {
                case 1 -> battle.getRegistrationDeadline();
                case 2 -> battle.getFinalSubmissionDeadline();
                default -> null;
            };
            if (nextStep != null) {
                long diffInMills = (nextStep.getTime() - new Date().getTime());
                long diff = TimeUnit.DAYS.convert(diffInMills, TimeUnit.MILLISECONDS);
                daysLeft = String.valueOf(diff) + "d";
                battleMap.put("remaining", daysLeft);
            }

            return battleMap;
        }));

        ArrayList<Map<String, Object>> rankings = new ArrayList<>();
        tournament.getRanking().forEach((Student, score) -> {
            Map<String, Object> rankingMap = new LinkedHashMap<>();
            rankingMap.put("id", Student.getId());
            rankingMap.put("firstname", Student.getFirstName());
            rankingMap.put("lastname", Student.getLastName());
            rankingMap.put("points", score);
            rankings.add(rankingMap);
        });
        response.put("ranking", rankings);

        ArrayList<Link> links = new ArrayList<>();
        links.add(linkTo(methodOn(TournamentController.class).one(tournament.getId())).withSelfRel());
        links.add(linkTo(methodOn(TournamentController.class).all()).withRel("tournaments"));
        response.put("_links_", links);

        return response;
    }

    //mapped to "Get all Tournaments"
    @GetMapping("/tournaments")
    List<Map<String, Object>> all() {
        List<Tournament> tournaments = tournamentRepository.findAll();

        List<Map<String, Object>> response = new ArrayList<>();

        tournaments.forEach(t -> {
            Map<String, Object> tournament = new LinkedHashMap<>();
            tournament.put("id", t.getId());
            tournament.put("name", t.getName());
            tournament.put("first_name", t.getCreator().getFirstName());
            tournament.put("last_name", t.getCreator().getLastName());
            tournament.put("active", t.isActive());
            ArrayList<Link> links = new ArrayList<>();
            links.add(linkTo(methodOn(TournamentController.class).one(t.getId())).withSelfRel());
            links.add(linkTo(methodOn(TournamentController.class).all()).withRel("tournaments"));
            tournament.put("_links_", links);
            response.add(tournament);
        });

        return response;
    }

    //mapped to "Get owned Tournaments"
    @GetMapping("/tournaments/owned/")
    List<Map<String, Object>> getOwnedTournaments(HttpSession session) {
        User user = (User) session.getAttribute("user");
        // TODO: if user == null return to index.html
        Educator educator = educatorRepository.findById(user.getId())
                .orElseThrow(() -> new EducatorNotFoundException(user.getId()));

        List<Map<String, Object>> response = new ArrayList<>();

        educator.getOwnedTournaments().forEach(t -> {
            Map<String, Object> tournament = new LinkedHashMap<>();

            tournament.put("id", t.getId());
            tournament.put("name", t.getName());
            tournament.put("first_name", t.getCreator().getFirstName());
            tournament.put("last_name", t.getCreator().getLastName());
            tournament.put("active", t.isActive());
            ArrayList<Link> links = new ArrayList<>();
            links.add(linkTo(methodOn(TournamentController.class).tournamentDetailsEDU(t.getId(), session)).withSelfRel());
            links.add(linkTo(methodOn(TournamentController.class).all()).withRel("tournaments"));
            tournament.put("_links_", links);
            response.add(tournament);
        });

        return response;
    }

    //mapped to "Get tournament details " for a stu
    @GetMapping("/tournaments/stu/{t_id}")
    Map<String, Object> tournamentDetailsSTU(@PathVariable Long t_id, HttpSession session) {
        Tournament tournament = tournamentRepository.findById(t_id)
                .orElseThrow(() -> new TournamentNotFoundException(t_id));

        //check if the user is a student
        User user = (User) session.getAttribute("user");
        if (user == null || user.isEdu()) {
            throw new StudentNotFoundException(user.getId());
        }
        Student student = studentRepository.findById(user.getId())
                .orElseThrow(() -> new StudentNotFoundException(user.getId()));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", tournament.getId());
        response.put("name", tournament.getName());
        response.put("active", tournament.isActive());
        response.put("canSubscribe", tournament.getSubscriptionDeadline().compareTo(new Date()) > 0 && !tournament.getSubscribedStudents().contains(student));
        response.put("subscribed", tournament.getSubscribedStudents().contains(student));
        response.put("battles", tournament.getBattles().stream().map(battle -> {
            Map<String, Object> battleMap = new LinkedHashMap<>();
            battleMap.put("id", battle.getId());
            battleMap.put("name", battle.getName());
            battleMap.put("language", battle.getLanguage());
            battleMap.put("participants", battle.getTeams().stream().reduce(0, (sum, team) -> sum + team.getStudents().size(), Integer::sum));
            battleMap.put("subscribed", battle.getTeams().stream().anyMatch(team -> team.getStudents().contains(student)));
            battleMap.put("score", battle.getTeams().stream().filter(team -> team.getStudents().contains(student)).findFirst().map(Team::getFinalScore).orElse(0));
            battleMap.put("phase", battle.getPhase());

            String daysLeft;
            Date nextStep = switch (battle.getPhase()) {
                case 1 -> battle.getRegistrationDeadline();
                case 2 -> battle.getFinalSubmissionDeadline();
                default -> null;
            };
            if (nextStep != null) {
                long diffInMills = (nextStep.getTime() - new Date().getTime());
                long diff = TimeUnit.DAYS.convert(diffInMills, TimeUnit.MILLISECONDS);
                daysLeft = String.valueOf(diff) + "d";
                battleMap.put("remaining", daysLeft);
            }

            return battleMap;
        }));
        ArrayList<Map<String, Object>> rankings = new ArrayList<>();
        tournament.getRanking().forEach((Student, score) -> {
            Map<String, Object> rankingMap = new LinkedHashMap<>();
            rankingMap.put("id", Student.getId());
            rankingMap.put("firstname", Student.getFirstName());
            rankingMap.put("lastname", Student.getLastName());
            rankingMap.put("points", score);
            rankings.add(rankingMap);
        });
        response.put("ranking", rankings);
        ArrayList<Link> links = new ArrayList<>();
        links.add(linkTo(methodOn(TournamentController.class).tournamentDetailsSTU(tournament.getId(), session)).withSelfRel());
        links.add(linkTo(methodOn(TournamentController.class).all()).withRel("tournaments"));
        response.put("_links_", links);
        return response;
    }


    //mapped to "Get tournament details"
    @GetMapping("/tournaments/edu/{t_id}")
    Map<String, Object> tournamentDetailsEDU(@PathVariable Long t_id, HttpSession session) {
        Tournament tournament = tournamentRepository.findById(t_id)
                .orElseThrow(() -> new TournamentNotFoundException(t_id));
        //check if the id is an educator
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isEdu()) {
            throw new EducatorNotFoundException(user.getId());
        }
        Educator educator = educatorRepository.findById(user.getId())
                .orElseThrow(() -> new EducatorNotFoundException(user.getId()));


        Map<String, Object> tournamentMap = new LinkedHashMap<>();
        tournamentMap.put("id", tournament.getId());
        tournamentMap.put("name", tournament.getName());
        tournamentMap.put("active", tournament.isActive());
        tournamentMap.put("admin", tournament.getGrantedEducators().contains(educator));
        tournamentMap.put("battles", tournament.getBattles().stream().map(battle -> {
            Map<String, Object> battleMap = new LinkedHashMap<>();
            battleMap.put("id", battle.getId());
            battleMap.put("name", battle.getName());
            battleMap.put("language", battle.getLanguage());
            battleMap.put("participants", battle.getTeams().stream().reduce(0, (sum, team) -> sum + team.getStudents().size(), Integer::sum));
            battleMap.put("phase", battle.getPhase());
            String daysLeft;
            Date nextStep = switch (battle.getPhase()) {
                case 1 -> battle.getRegistrationDeadline();
                case 2 -> battle.getFinalSubmissionDeadline();
                default -> null;
            };
            if (nextStep != null) {
                long diffInMills = (nextStep.getTime() - new Date().getTime());
                long diff = TimeUnit.DAYS.convert(diffInMills, TimeUnit.MILLISECONDS);
                daysLeft = String.valueOf(diff) + "d";
                battleMap.put("remaining", daysLeft);
            }
            return battleMap;
        }));

        ArrayList<Map<String, Object>> rankings = new ArrayList<>();
        tournament.getRanking().forEach((Student, score) -> {
            Map<String, Object> rankingMap = new LinkedHashMap<>();
            rankingMap.put("id", Student.getId());
            rankingMap.put("firstname", Student.getFirstName());
            rankingMap.put("lastname", Student.getLastName());
            rankingMap.put("points", score);
            rankings.add(rankingMap);
        });
        tournamentMap.put("ranking", rankings);
        ArrayList<Link> links = new ArrayList<>();
        links.add(linkTo(methodOn(TournamentController.class).tournamentDetailsEDU(tournament.getId(), session)).withSelfRel());
        links.add(linkTo(methodOn(TournamentController.class).all()).withRel("tournaments"));
        tournamentMap.put("_links_", links);
        return tournamentMap;
    }

    //mapped to "Get subscribed tournaments"
    @GetMapping("/tournaments/subscribed/")
    List<Map<String, Object>> getSubscribedTournaments(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.isEdu()) {
            throw new StudentNotFoundException(user.getId());
        }
        Student student = studentRepository.findById(user.getId())
                .orElseThrow(() -> new StudentNotFoundException(user.getId()));

        List<Map<String, Object>> response = new ArrayList<>();

        student.getTournaments().forEach(t -> {
            Map<String, Object> tournamentMap = new LinkedHashMap<>();
            tournamentMap.put("id", t.getId());
            tournamentMap.put("name", t.getName());
            tournamentMap.put("first_name", t.getCreator().getFirstName());
            tournamentMap.put("last_name", t.getCreator().getLastName());
            tournamentMap.put("active", t.isActive());
            ArrayList<Link> links = new ArrayList<>();
            links.add(linkTo(methodOn(TournamentController.class).tournamentDetailsSTU(t.getId(), session)).withSelfRel());
            links.add(linkTo(methodOn(TournamentController.class).all()).withRel("tournaments"));
            tournamentMap.put("_links_", links);
            response.add(tournamentMap);
        });

        return response;
    }

    //mapped to "Get unsubscribed tournaments"
    @GetMapping("/tournaments/unsubscribed/")
    List<Map<String, Object>> getUnsubscribedTournaments(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.isEdu()) {
            throw new StudentNotFoundException(user.getId());
        }
        Student student = studentRepository.findById(user.getId())
                .orElseThrow(() -> new StudentNotFoundException(user.getId()));

        List<Map<String, Object>> response = new ArrayList<>();

        tournamentRepository.findAll().forEach(t -> {
            if (!student.getTournaments().contains(t) && t.getSubscriptionDeadline().getTime() > new Date().getTime()) {
                Map<String, Object> tournamentMap = new LinkedHashMap<>();

                String daysLeft;
                long diffInMills = (t.getSubscriptionDeadline().getTime() - new Date().getTime());
                long diff = TimeUnit.DAYS.convert(diffInMills, TimeUnit.MILLISECONDS);
                daysLeft = String.valueOf(diff) + "d";

                tournamentMap.put("id", t.getId());
                tournamentMap.put("name", t.getName());
                tournamentMap.put("first_name", t.getCreator().getFirstName());
                tournamentMap.put("last_name", t.getCreator().getLastName());
                tournamentMap.put("daysLeft", daysLeft);
                ArrayList<Link> links = new ArrayList<>();
                links.add(linkTo(methodOn(TournamentController.class).tournamentDetailsSTU(t.getId(), session)).withSelfRel());
                links.add(linkTo(methodOn(TournamentController.class).all()).withRel("tournaments"));
                tournamentMap.put("_links_", links);
                response.add(tournamentMap);
            }
        });

        return response;
    }

    @PostMapping("/tournament/create")
    public ResponseEntity<String> createTournament(@RequestBody CreateTournamentRequest createTournament, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("index");

        String name = createTournament.getTournamentName();
        Date registerDeadline = createTournament.getRegisterDeadline();

        if (name.isBlank() || name.isEmpty() || registerDeadline == null)
            // Check if any field is empty
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request - Empty or Blank parameters");

        Date currentDate = new Date();
        if (registerDeadline.before(currentDate))
            // Check if the deadline is past
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request - Registration deadline has passed");

        long tournamentId;
        String torunamentIdString;
        if (user.isEdu()) {
            Tournament newTournament = new Tournament(name, registerDeadline, null, (Educator) user);
            tournamentRepository.save(newTournament);
            tournamentId = tournamentRepository.getNewTournamentId(name, registerDeadline, (Educator) user);
            torunamentIdString = Long.valueOf(tournamentId).toString();
        } else
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden - You do not have the necessary rights");

        // Get all students and inform them of an upcoming tournament via email
        List<Student> students = studentRepository.getAllStudentInPlatform();

        new Thread(() -> {
            // Prepare Email to send
            GmailAPI gmailSender;
            try {
                gmailSender = new GmailAPI();
            } catch (GeneralSecurityException | IOException e) {
                throw new RuntimeException(e);
            }
            String subject = "UPCOMING TOURNAMENT " + name;

            // Send Email to each student in CKB
            for (Student s : students) {
                String bodyMsg = "Hi " + s.getFirstName() + ",\n\n" +
                                 "as CKB Platform member I want to inform you about an upcoming tournament\n" +
                                 "Educator " + user.getFirstName() + " has created the tournament " + name + " and is waiting for you\n" +
                                 "The registration deadline is on " + registerDeadline + ", subscribe before it expires\n" +
                                 "You can find CKB Platform at the following link: http://localhost:8080/ckb_platform\n\n" +
                                 "Best regards,\n CKB Team";
                try {
                    gmailSender.sendEmail(subject,bodyMsg, s.getEmail());
                } catch (IOException | MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        return ResponseEntity.status(HttpStatus.OK).body(torunamentIdString);
    }

    @PostMapping("/tournament/close")
    public ResponseEntity<String> closeTournament(@RequestBody CloseTournamentRequest closeTournamentRequest, HttpSession session) {
        User user = (User) session.getAttribute("user");

        Long tournamentId = closeTournamentRequest.getId();
        boolean owner = false;

        if (user == null)
            // Check if the user is not logged in
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized - You are not logged in CKB");

        if (!user.isEdu())
            // Check if user is not an Educator
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden - You do not have the necessary rights");

        if(tournamentRepository.isTournamentOwner(tournamentId, (Educator) user) == 1){
            // Check if it is the creator
            owner = true;
        } else {
            // Check if it got permission
            Educator grantedOwner = (Educator) user;
            for (Tournament t : grantedOwner.getOwnedTournaments()) {
                if (t.getId().equals(tournamentId)) {
                    owner = true;
                    break;
                }
            }
        }

        if(owner){
            // User can close the tournament
            Tournament closedTournament = tournamentRepository.getTournamentById(tournamentId);

            if(closedTournament == null)
                // If it does not exist return
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found - Tournament with id: " + closeTournamentRequest.getId() + " does not exist");

            if(closedTournament.getEndDate() != null)
                // If is closed it can not be shared
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden - Tournament " + closedTournament.getName() + " is already closed");

            for(Battle b : closedTournament.getBattles()){
                // Check if all the battles are closed
                if(b.getFinalSubmissionDeadline().after(new Date()))
                    // Check if the submission deadline is gone
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden - Some battles in tournament " + closedTournament.getName() + " are not ended yet");

                if(b.getManualEvaluation() && !b.getHasBeenEvaluated())
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden - Some battles in tournament " + closedTournament.getName() + " are not evaluated yet");
            }

            Date currentDate = new Date();
            tournamentRepository.closeTournament(tournamentId, currentDate);

            // Get all students and inform them of an upcoming tournament via email
            List<Student> students = studentRepository.getAllStudentInPlatform();

            new Thread(() -> {
                // Prepare Email to send
                GmailAPI gmailSender;
                try {
                    gmailSender = new GmailAPI();
                } catch (GeneralSecurityException | IOException e) {
                    throw new RuntimeException(e);
                }
                String subject = "CLOSE TOURNAMENT " + closedTournament.getName();

                // Send Email to each student in CKB
                for (Student s : students) {
                    String bodyMsg = "Hi " + s.getFirstName() + ", \n\n" +
                                     "Tournament " + closedTournament.getName() + " has been closed by " + user.getFirstName() + "\n" +
                                     "You can now find the final ranking with your total personal score\n" +
                                     "You can find CKB Platform at the following link: http://localhost:8080/ckb_platform\n\n" +
                                     "Best regards,\n CKB Team";
                    try {
                        gmailSender.sendEmail(subject, bodyMsg, s.getEmail());
                    } catch (IOException | MessagingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();

            return ResponseEntity.status(HttpStatus.OK).body(user.getFirstName() + "\nTournament " + closedTournament.getName() + " successfully closed");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden - You do not own this tournament");
    }

    @PostMapping("/tournament/share")
    public ResponseEntity<String> shareTournament(@RequestBody ShareTournamentRequest shareTournamentRequest, HttpSession session) {
        User user = (User) session.getAttribute("user");
        boolean owner = false;

        if (user == null)
            // Check if the user is not logged in
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized - You are not logged in CKB");

        if (!user.isEdu())
            // Check if user is an Educator
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden - You do not have the necessary rights");

        if(tournamentRepository.isTournamentOwner(shareTournamentRequest.getId(), (Educator) user) == 1){
            // Check if it is the creator
            owner = true;
        } else {
            // Check if it got permission
            Educator grantedOwner = (Educator) user;
            for (Tournament t : grantedOwner.getOwnedTournaments()) {
                if (t.getId().equals(shareTournamentRequest.getId())) {
                    owner = true;
                    break;
                }
            }
        }

        if(owner){
            // User can share the tournament

            // Get tournament data
            Tournament sharedTournament = tournamentRepository.getTournamentById(shareTournamentRequest.getId());

            if(sharedTournament == null)
                // If it does not exist return
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found - Tournament with id: " + shareTournamentRequest.getId() + " does not exist");

            if(sharedTournament.getEndDate() != null)
                // If is closed it can not be shared
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden - Tournament " + sharedTournament.getName() + " is closed");

            // Get educators data
            Educator ownerEDU = educatorRepository.getEducatorDataById(user.getId());
            Educator invitedEDU = educatorRepository.getEducatorDataById(shareTournamentRequest.getEducatorId());

            if (!invitedEDU.isEdu())
                // Check if the invited user is not an EDU
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden - You are not inviting an educator");

            if(ownerEDU == invitedEDU)
                // Owner can not invite itself
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden - " + ownerEDU.getFirstName() + ", you can not invite yourself");


            for (Tournament t : invitedEDU.getOwnedTournaments()) {
                if(t == sharedTournament)
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden - " + invitedEDU.getFirstName() + ", already has permission for " + sharedTournament.getName() + " tournament");
            }

            // Add tournament to the educator
            invitedEDU.addTournament(sharedTournament);
            educatorRepository.save(invitedEDU);

            // Add educator to the tournament
            sharedTournament.addEducator(invitedEDU);
            tournamentRepository.save(sharedTournament);

            new Thread(() -> {
                // Prepare Email to send
                GmailAPI gmailSender;
                try {
                    gmailSender = new GmailAPI();
                } catch (GeneralSecurityException | IOException e) {
                    throw new RuntimeException(e);
                }
                String subject = "SHARED TOURNAMENT " + sharedTournament.getName();
                String bodyMsg = "Hi " + invitedEDU.getFirstName() + ",\n\n" +
                                 ownerEDU.getFirstName() + " shared tournament " + sharedTournament.getName() + " with you\n" +
                                 "You can now perform the following action inside it:\n" +
                                 "  - Create battles\n" +
                                 "  - Invite other Educator, which become owner as you\n" +
                                 "  - Close the tournement\n" +
                                 "You can find CKB Platform at the following link: http://localhost:8080/ckb_platform\n\n" +
                                 "Best regards,\n CKB Team";

                // Send Email to each Educator
                try {
                    gmailSender.sendEmail(subject,bodyMsg, invitedEDU.getEmail());
                } catch (IOException | MessagingException e) {
                    throw new RuntimeException(e);
                }
            }).start();

            return ResponseEntity.status(HttpStatus.OK).body("Tournament " + sharedTournament.getName() + " successfully shared with " + invitedEDU.getFirstName());
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden - You do not own this tournament");
    }

    @PostMapping("/tournament/join")
    public ResponseEntity<String> joinTournament(@RequestBody JoinTournamentRequest joinTournamentRequest, HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null)
            // Check if the user is not logged in
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized - You are not logged in CKB");

        if (user.isEdu())
            // Check if user is an Educator
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden - You do not have the necessary rights");

        // Get tournament data
        Tournament joinTournament = tournamentRepository.getTournamentById(joinTournamentRequest.getTournamentId());

        if(joinTournament == null)
           // If it does not exist return
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found - Tournament with id: " + joinTournamentRequest.getTournamentId() + " does not exist");

        Student addStudent = (Student) user;

        if(addStudent.getTournaments().contains(joinTournament) && joinTournament.getSubscribedStudents().contains(addStudent))
            // Check if the student is already in the tournament
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden - You are already part of tournament: " + joinTournament);

        addStudent.addTournament(joinTournament);
        joinTournament.addStudent(addStudent);
        studentRepository.save(addStudent);
        tournamentRepository.save(joinTournament);

        return ResponseEntity.status(HttpStatus.OK).body("Tournament successfully joined");
    }
}