package ckb.platform.controllers;

import ckb.platform.entities.*;
import ckb.platform.exceptions.BattleNotFoundException;
import ckb.platform.exceptions.EducatorNotFoundException;
import ckb.platform.exceptions.StudentNotFoundException;
import ckb.platform.exceptions.TeamNotFoundException;
import ckb.platform.formParser.CreateBattleRequest;
import ckb.platform.repositories.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class BattleController {

    @Autowired
    private final BattleRepository battleRepository;
    @Autowired
    private final TournamentRepository tournamentRepository;
    @Autowired
    private final StudentRepository studentRepository;
    @Autowired
    private final EducatorRepository educatorRepository;
    @Autowired
    private final TeamRepository teamRepository;

    BattleController(BattleRepository battleRepository, TournamentRepository tournamentRepository, StudentRepository studentRepository, EducatorRepository educatorRepository, TeamRepository teamRepository) {
        this.battleRepository = battleRepository;
        this.tournamentRepository = tournamentRepository;
        this.studentRepository = studentRepository;
        this.educatorRepository = educatorRepository;
        this.teamRepository = teamRepository;
    }

    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("/battles")
    List<Map<String, Object>> all() {
        List<Battle> battles = battleRepository.findAll();
        List<Map<String, Object>> response = new ArrayList<>();

        for (Battle battle : battles) {
            Map<String, Object> battleMap = new LinkedHashMap<>();
            battleMap.put("id", battle.getId());
            battleMap.put("title", battle.getTitle());
            battleMap.put("description", battle.getDescription());
            battleMap.put("language", battle.getLanguage());
            battleMap.put("opening", battle.getOpenDate());
            battleMap.put("registration", battle.getRegistrationDeadline().toString());
            battleMap.put("closing", battle.getFinalSubmissionDeadline().toString());
            battleMap.put("min_group_size", battle.getMinStudents());
            battleMap.put("max_group_size", battle.getMaxStudents());
            battleMap.put("phase", battle.getPhase());
            battleMap.put("tournament_name", battle.getTournament().getName());
            battleMap.put("tournament_id", battle.getTournament().getId());

            ArrayList<Link> links = new ArrayList<>();
            links.add(linkTo(methodOn(BattleController.class).one(battle.getId())).withSelfRel());
            links.add(linkTo(methodOn(BattleController.class).all()).withRel("all"));
            battleMap.put("_links_", links);
            response.add(battleMap);
        }

        return response;
    }

    // end::get-aggregate-root[]


    @GetMapping("/battles/{id}")
    Map<String, Object> one(@PathVariable Long id) {
        Battle battle = battleRepository.findById(id)
                .orElseThrow(() -> new BattleNotFoundException(id));

        Map<String, Object> battleMap = new LinkedHashMap<>();
        battleMap.put("id", battle.getId());
        battleMap.put("title", battle.getTitle());
        battleMap.put("description", battle.getDescription());
        battleMap.put("language", battle.getLanguage());
        battleMap.put("opening", battle.getOpenDate());
        battleMap.put("registration", battle.getRegistrationDeadline().toString());
        battleMap.put("closing", battle.getFinalSubmissionDeadline().toString());
        battleMap.put("min_group_size", battle.getMinStudents());
        battleMap.put("max_group_size", battle.getMaxStudents());
        battleMap.put("phase", battle.getPhase());
        battleMap.put("tournament_name", battle.getTournament().getName());
        battleMap.put("tournament_id", battle.getTournament().getId());

        List<Map<String, Object>> rankings = new ArrayList<>();
        battle.getRanking().forEach((team, score) -> {
            Map<String, Object> rankingMap = new LinkedHashMap<>();
            rankingMap.put("id", team.getId());
            rankingMap.put("name", team.getName());
            rankingMap.put("score", score);
            rankings.add(rankingMap);
        });

        battleMap.put("ranking", rankings);

        ArrayList<Link> links = new ArrayList<>();
        links.add(linkTo(methodOn(BattleController.class).one(id)).withSelfRel());
        links.add(linkTo(methodOn(BattleController.class).all()).withRel("all"));
        battleMap.put("_links_", links);

        return battleMap;
        }

    //mapped to "Get Battle Details"
    @GetMapping("/battles/stu/{id}")
    Map<String,Object> getBattleDetailsSTU(@PathVariable Long id, HttpSession session){
        Battle battle = battleRepository.findById(id)
                .orElseThrow(() -> new BattleNotFoundException(id));

        //check user passed is a stu
        User user = (User) session.getAttribute("user");
        if(user == null || user.isEdu()){
            throw new StudentNotFoundException(user.getId());
        }


        Map<String, Object> battleMap = new LinkedHashMap<>();
        battleMap.put("id", battle.getId());
        battleMap.put("title", battle.getTitle());
        battleMap.put("description", battle.getDescription());
        battleMap.put("language", battle.getLanguage());
        battleMap.put("opening", battle.getOpenDate());
        battleMap.put("registration", battle.getRegistrationDeadline().toString());
        battleMap.put("closing", battle.getFinalSubmissionDeadline().toString());
        battleMap.put("min_group_size", battle.getMinStudents());
        battleMap.put("max_group_size", battle.getMaxStudents());
        battleMap.put("phase", battle.getPhase());
        boolean after = battle.getRegistrationDeadline().after(new Date());
        boolean alreadyIn = battle.isSubscribed(studentRepository.findById(user.getId()).orElseThrow(() -> new StudentNotFoundException(user.getId())));
        battleMap.put("canSubscribe", after && !alreadyIn);
        //battleMap.put("canInviteOthers", battle.getRegistrationDeadline().compareTo(new Date())> 0 && battle.isSubscribed(studentRepository.findById(stu_id).orElseThrow(() -> new StudentNotFoundException(stu_id))));
        battleMap.put("canInviteOthers",
                battle.getRegistrationDeadline().after(new Date()) &&
                battle.isSubscribed(studentRepository.findById(user.getId()).orElseThrow(() -> new StudentNotFoundException(user.getId()))) &&
                battle.getTeams().stream().filter(team -> team.getStudents().contains(studentRepository.findById(user.getId()).orElseThrow(() -> new StudentNotFoundException(user.getId())))).count() < battle.getMaxStudents());
        battleMap.put("minConstraintSatisfied", battle.getMinStudents() <= battle.getTeams().stream().filter(team -> team.getStudents().contains(studentRepository.findById(user.getId()).orElseThrow(() -> new StudentNotFoundException(user.getId())))).count());
        battleMap.put("subscribed", battle.isSubscribed(studentRepository.findById(user.getId()).orElseThrow(() -> new StudentNotFoundException(user.getId()))));
        battleMap.put("tournament_name", battle.getTournament().getName());
        battleMap.put("tournament_id", battle.getTournament().getId());

        List<Map<String, Object>> rankings = new ArrayList<>();
        battle.getRanking().forEach((team, score) -> {
            Map<String, Object> rankingMap = new LinkedHashMap<>();
            rankingMap.put("id", team.getId());
            rankingMap.put("name", team.getName());
            rankingMap.put("score", score);
            rankings.add(rankingMap);
        });

        battleMap.put("ranking", rankings);
        ArrayList<Link> links = new ArrayList<>();
        links.add(linkTo(methodOn(BattleController.class).getBattleDetailsSTU(battle.getId(), session)).withSelfRel());
        links.add(linkTo(methodOn(BattleController.class).all()).withRel("battles"));
        battleMap.put("_links_", links);
        return battleMap;
    }


    // Single item
    //mapped to "Get Battle Details"
    @GetMapping("/battles/edu/{id}")
    Map<String, Object> getBattleDetailsEDU(@PathVariable Long id, HttpSession session) {
        Battle battle = battleRepository.findById(id)
                .orElseThrow(() -> new BattleNotFoundException(id));

        //check if user passed is edu
        User user = (User) session.getAttribute("user");
        if(user == null || !user.isEdu()){
            throw new EducatorNotFoundException(user.getId());
        }
        Educator educator = educatorRepository.findById(user.getId())
                .orElseThrow( () -> new EducatorNotFoundException(user.getId()));

        Map<String, Object> battleMap = new LinkedHashMap<>();
        battleMap.put("id", battle.getId());
        battleMap.put("title", battle.getTitle());
        battleMap.put("description", battle.getDescription());
        battleMap.put("language", battle.getLanguage());
        battleMap.put("opening", battle.getOpenDate());
        battleMap.put("registration", battle.getRegistrationDeadline().toString());
        battleMap.put("closing", battle.getFinalSubmissionDeadline().toString());
        battleMap.put("min_group_size", battle.getMinStudents());
        battleMap.put("max_group_size", battle.getMaxStudents());
        battleMap.put("phase", battle.getPhase());
        battleMap.put("tournament_name", battle.getTournament().getName());
        battleMap.put("tournament_id", battle.getTournament().getId());
        battleMap.put("admin", battle.getTournament().getGrantedEducators().contains(educator));
        battleMap.put("manual", battle.getManualEvaluation());

        List<Map<String, Object>> rankings = new ArrayList<>();
        battle.getRanking().forEach((team, score) -> {
            Map<String, Object> rankingMap = new LinkedHashMap<>();
            rankingMap.put("id", team.getId());
            rankingMap.put("name", team.getName());
            rankingMap.put("score", score);
            rankings.add(rankingMap);
        });

        battleMap.put("ranking", rankings);

        ArrayList<Link> links = new ArrayList<>();
        links.add(linkTo(methodOn(BattleController.class).getBattleDetailsEDU(battle.getId(), session)).withSelfRel());
        links.add(linkTo(methodOn(BattleController.class).all()).withRel("battles"));
        battleMap.put("_links_" , links);

        return battleMap;
    }

    //mapped to "Get the list of groups for the manual evaluation"
    //TODO : MANCA LA PARTE DELLE EVALUATION, COME CAPISCO SE UN TEAM HA GIà LO SCORE O NO?
    @GetMapping("/battles/{b_id}/manualevalution")
    List<Map<String, Object>> manualEvalGroups(@PathVariable Long b_id) {
        Battle battle = battleRepository.findById(b_id)
                .orElseThrow( () -> new BattleNotFoundException(b_id));

        List<Map<String, Object>> response= new ArrayList<>();

        return response;
    }

    //mapped to "Evaluate code"
    @GetMapping("/battles/{b_id}/teams/{t_id}")
    Map<String, Object> getCode(@PathVariable Long b_id, @PathVariable Long t_id, HttpSession session) {
        Battle battle = battleRepository.findById(b_id)
                .orElseThrow(() -> new BattleNotFoundException(b_id));
        Team team = teamRepository.findById(t_id)
                .orElseThrow(() -> new TeamNotFoundException(t_id));
        //check if user passed is edu
        User user = (User) session.getAttribute("user");
        if(user == null || !user.isEdu()) {
            throw new EducatorNotFoundException(user.getId());
        }

        Map<String, Object> response= new LinkedHashMap<>();
        response.put("group_id", team.getId());
        response.put("battle_id", battle.getId());
        response.put("language", battle.getLanguage());
        response.put("name", team.getName());
        response.put("score", battle.getRanking().get(team));
        response.put("code", team.getCode());

        ArrayList <Link> links = new ArrayList<>();
        links.add(linkTo(methodOn(TeamController.class).one(t_id)).withRel("team"));
        links.add(linkTo(methodOn(BattleController.class).getStudents(b_id)).withRel("all_teams"));

        response.put("_links_", links);
        return response;
    }

    @GetMapping("/battles/{id}/students")
    List<Map<String, Object>> getStudents(@PathVariable Long id) {
        Battle battle = battleRepository.findById(id)
                .orElseThrow(() -> new BattleNotFoundException(id));

        List<Map<String, Object>> response = new ArrayList<>();
        battle.getTeams()
                .forEach(t -> {
                    Map<String, Object> team = new LinkedHashMap<>();
                    team.put("id", t.getId());
                    team.put("name", t.getName());
                    team.put("score", battle.getRanking().get(t));
                    List<Map<String, Object>> students = new ArrayList<>();
                    t.getStudents().forEach(s -> {
                        Map<String, Object> student = new LinkedHashMap<>();
                        student.put("id", s.getId());
                        student.put("firstName", s.getFirstName());
                        student.put("lastName", s.getLastName());
                        students.add(student);
                    });
                    team.put("students", students);
                    response.add(team);
                });

        return response;
    }

    @PostMapping("/battles/invite")
    ResponseEntity<?> inviteStudent(@RequestBody Long battle_id, @RequestBody String email, HttpSession session) {
        final User user = (User) session.getAttribute("user");
        //final User user = studentRepository.findById(1L).orElseThrow(() -> new StudentNotFoundException(1L));
        if(user == null || user.isEdu()){
            throw new StudentNotFoundException(user.getId());
        }

        Battle battle = battleRepository.findById(battle_id)
                .orElseThrow(() -> new BattleNotFoundException(battle_id));

        Student studentToInvite = studentRepository.
                getAllStudentInPlatform().
                stream().
                filter(s -> s.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new StudentNotFoundException(-1L));

        Team team = battle
                .getTeams()
                .stream()
                .filter(t -> t.getStudents().contains((Student) user)).findFirst().orElseThrow(() -> new TeamNotFoundException(-1L));

        team.addStudent(studentToInvite);
        teamRepository.save(team);

        battleRepository.save(battle);

        return ResponseEntity
                .created(linkTo(methodOn(BattleController.class).getBattleDetailsEDU(battle.getId(), session)).withSelfRel().toUri())
                .body("Student added to the team " + team.getName());
    }

    @PostMapping("/battle/create")
    public ResponseEntity<String> createBattle(@ModelAttribute CreateBattleRequest createBattleRequest, HttpSession session){
        User user = (User) session.getAttribute("user");
        boolean owner = false;

        Long tournamentId = createBattleRequest.getTournamentId();
        String battleName = createBattleRequest.getBattleName();
        Date registerDeadline = createBattleRequest.getRegisterDeadline();
        Date submissionDeadline = createBattleRequest.getSubmissionDeadline();
        String language = createBattleRequest.getLanguage();
        int minSize = createBattleRequest.getMinSize();
        int maxSize = createBattleRequest.getMaxSize();
        boolean manualEvaluation = createBattleRequest.isManualEvaluation();
        MultipartFile ckbProblem = createBattleRequest.getCkbProblem();

        if (user == null) {
            // Check if user is not in session
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).body("Unauthorized - You are not logged in CKB");
        }

        if (!user.isEdu()) {
            // Check if user is an Educator
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden - You do not have the necessary rights");
        }

        // TODO: Controlli da fare:
        // Check if data are not null, empty or blank
        // Check if the tournament exist
        // Check if the deadlines are in future
        // Check if submission is after registration
        // Check if language is in the list of accepted one
        // Check if minSize > 1, maxSize > minSize
        // Check if maxSize = half size of CKB stu if tournament deadline still open || half size of stu in torunemant => At most 2 teams compete

        if (!ckbProblem.getContentType().equalsIgnoreCase("application/pdf")) {
            // Check if the file is a PDF
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request - " + ckbProblem.getOriginalFilename() + " is not a PDF");
        }

        Educator creatorBattle = (Educator) user;

        for (Tournament t : creatorBattle.getOwnedTournaments()) {
            // Find out if the educator is a owner of the tournament
            if (t.getId().equals(tournamentId)) {
                owner = true;
                break;
            }
        }

        if(owner){
            Tournament tournamentRelated = tournamentRepository.getTournamentById(tournamentId);
            Battle newBattle = new Battle(battleName, new Date(), registerDeadline, submissionDeadline, language, manualEvaluation, minSize, maxSize, creatorBattle, tournamentRelated, false);
            battleRepository.save(newBattle);

            // Change file name with the battle id
            String battleId = String.valueOf(battleRepository.getBattleId(newBattle.getName(), registerDeadline, submissionDeadline, language, manualEvaluation, minSize, maxSize, creatorBattle, tournamentRelated, false));
            String newCkbProblem = battleId + ".pdf";

            // Obtain path to store the file
            Path absolutePath = Paths.get("fileStorage").toAbsolutePath();
            Path destinationPath = absolutePath.resolve("ckbProblemPDF").resolve(newCkbProblem);

            try {
                // Save file in the directory
                Files.copy(ckbProblem.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                // TODO: rollback
                throw new RuntimeException(e);
            }
            return ResponseEntity.status(HttpStatus.OK).body(battleId);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden - You do not have the necessary rights");
    }
}
