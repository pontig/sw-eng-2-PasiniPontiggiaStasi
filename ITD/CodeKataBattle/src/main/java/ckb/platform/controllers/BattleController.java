package ckb.platform.controllers;

import ckb.platform.entities.*;
import ckb.platform.exceptions.BattleNotFoundException;
import ckb.platform.exceptions.EducatorNotFoundException;
import ckb.platform.exceptions.StudentNotFoundException;
import ckb.platform.exceptions.TeamNotFoundException;
import ckb.platform.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

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
    private final BattleModelAssembler assembler;
    @Autowired
    private final TeamRepository teamRepository;

    BattleController(BattleRepository battleRepository, BattleModelAssembler assembler, TournamentRepository tournamentRepository, StudentRepository studentRepository, EducatorRepository educatorRepository, TeamRepository teamRepository) {
        this.battleRepository = battleRepository;
        this.assembler = assembler;
        this.tournamentRepository = tournamentRepository;
        this.studentRepository = studentRepository;
        this.educatorRepository = educatorRepository;
        this.teamRepository = teamRepository;
    }

    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("/battles")
    CollectionModel<EntityModel<Battle>> all() {
        List<EntityModel<Battle>> battles = battleRepository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(battles, linkTo(methodOn(BattleController.class).all()).withSelfRel());
    }

    // end::get-aggregate-root[]


    @GetMapping("/battles/{id}")
    Map<String, Object> one(@PathVariable Long id) {
        Battle battle = battleRepository.findById(id)
                .orElseThrow();

        Map<String, Object> battleMap = new LinkedHashMap<>();
        battleMap.put("id", battle.getId());
        //battleMap.put("title", battle.getTitle());
        //battleMap.put("description", battle.getDescription());
        //battleMap.put("language", battle.getLanguage());
        // battleMap.put("Opening", battle.getOpening());
        battleMap.put("registration", battle.getRegistrationDeadline().toString());
        battleMap.put("closing", battle.getFinalSubmissionDeadline().toString());
        battleMap.put("min_group_size", battle.getMinStudents());
        battleMap.put("max_group_size", battle.getMaxStudents());
        //battleMap.put("phase", battle.phase());
        battleMap.put("tournament_name", battle.getTournament().getName());
        battleMap.put("tournament_id", battle.getTournament().getId());

        List<Map<String, Object>> rankings = new ArrayList<>();
        battle.getRanking().forEach((team, score) -> {
            Map<String, Object> rankingMap = new LinkedHashMap<>();
            rankingMap.put("id", team.getId());
            //rankingMap.put("name", team.getName());
            rankingMap.put("score", score);
            rankings.add(rankingMap);
        });

        battleMap.put("ranking", rankings);

        return battleMap;
        }

    //mapped to "Get Battle Details"
    @GetMapping("/battles/{id}")
    Map<String,Object> getBattleDetailsSTU(@PathVariable Long id, @RequestParam Long stu_id){
        Battle battle = battleRepository.findById(id)
                .orElseThrow(() -> new BattleNotFoundException(id));

        //check user passed is a stu
        Student student = studentRepository.findById(stu_id)
                .orElseThrow(() -> new StudentNotFoundException(stu_id));

        Map<String, Object> battleMap = new LinkedHashMap<>();
        battleMap.put("id", battle.getId());
        //battleMap.put("title", battle.getTitle());
        //battleMap.put("description", battle.getDescription());
        //battleMap.put("language", battle.getLanguage());
        // battleMap.put("Opening", battle.getOpening());
        battleMap.put("registration", battle.getRegistrationDeadline().toString());
        battleMap.put("closing", battle.getFinalSubmissionDeadline().toString());
        battleMap.put("min_group_size", battle.getMinStudents());
        battleMap.put("max_group_size", battle.getMaxStudents());
        //battleMap.put("phase", battle.phase());
        battleMap.put("canSubscribe", battle.getRegistrationDeadline().compareTo(new Date())> 0 && !battle.isSubscribed(studentRepository.findById(stu_id).orElseThrow()));
        //battleMap.put("canInviteOthers", battle.getRegistrationDeadline().compareTo(new Date())> 0 && battle.isSubscribed(studentRepository.findById(stu_id).orElseThrow()));
        battleMap.put("minConstraintSatisfied", battle.getMinStudents() <= battle.getTeams().stream().filter(team -> team.getStudents().contains(studentRepository.findById(stu_id).orElseThrow())).count());
        battleMap.put("subscribed", battle.isSubscribed(student));
        battleMap.put("tournament_name", battle.getTournament().getName());
        battleMap.put("tournament_id", battle.getTournament().getId());

        List<Map<String, Object>> rankings = new ArrayList<>();
        battle.getRanking().forEach((team, score) -> {
            Map<String, Object> rankingMap = new LinkedHashMap<>();
            rankingMap.put("id", team.getId());
            //rankingMap.put("name", team.getName());
            rankingMap.put("score", score);
            rankings.add(rankingMap);
        });

        battleMap.put("ranking", rankings);

        return battleMap;
    }


    // Single item
    //mapped to "Get Battle Details"
    @GetMapping("/battles/{id}")
    Map<String, Object> getBattleDetailsEDU(@PathVariable Long id, @RequestParam Long edu_id) {
        Battle battle = battleRepository.findById(id)
                .orElseThrow();

        //check if user passed is edu
        Educator educator = educatorRepository.findById(edu_id)
                .orElseThrow( () -> new EducatorNotFoundException(edu_id));

        Map<String, Object> battleMap = new LinkedHashMap<>();
        battleMap.put("id", battle.getId());
        //battleMap.put("title", battle.getTitle());
        //battleMap.put("description", battle.getDescription());
        //battleMap.put("language", battle.getLanguage());
        // battleMap.put("Opening", battle.getOpening());
        battleMap.put("registration", battle.getRegistrationDeadline().toString());
        battleMap.put("closing", battle.getFinalSubmissionDeadline().toString());
        battleMap.put("min_group_size", battle.getMinStudents());
        battleMap.put("max_group_size", battle.getMaxStudents());
        //battleMap.put("phase", battle.phase());
        battleMap.put("tournament_name", battle.getTournament().getName());
        battleMap.put("tournament_id", battle.getTournament().getId());
        battleMap.put("admin", battle.getTournament().getGrantedEducators().contains(educator));
        battleMap.put("manual", battle.getManualEvaluation());

        List<Map<String, Object>> rankings = new ArrayList<>();
        battle.getRanking().forEach((team, score) -> {
            Map<String, Object> rankingMap = new LinkedHashMap<>();
            rankingMap.put("id", team.getId());
            //rankingMap.put("name", team.getName());
            rankingMap.put("score", score);
            rankings.add(rankingMap);
        });

        battleMap.put("ranking", rankings);

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
    Map<String, Object> getCode(@PathVariable Long b_id, @PathVariable Long t_id, @RequestParam Long edu_id) {
        Battle battle = battleRepository.findById(b_id)
                .orElseThrow(() -> new BattleNotFoundException(b_id));
        Team team = teamRepository.findById(t_id)
                .orElseThrow(() -> new TeamNotFoundException(t_id));
        //check if user passed is edu
        Educator educator = educatorRepository.findById(edu_id)
                .orElseThrow( () -> new EducatorNotFoundException(edu_id));

        Map<String, Object> response= new LinkedHashMap<>();
        response.put("group_id", team.getId());
        response.put("battle_id", battle.getId());
        //response.put("language", battle.getLanguage());
        //response.put("name", team.getName());
        response.put("score", battle.getRanking().get(team));
        //response.put("code", team.getCode());
        return response;
    }

    @PostMapping("/battles/{t_id}")
    ResponseEntity<?> newBattle(@PathVariable Long t_id, @RequestBody Battle newBattle) {
        Tournament tournament = tournamentRepository.findById(t_id)
                .orElseThrow();

        tournament.addBattle(newBattle);
        tournamentRepository.save(tournament);

        EntityModel<Battle> entityModel = assembler.toModel(battleRepository.save(newBattle));

        return ResponseEntity
                .created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }

    // @PutMapping("/battles/{id}")

    @DeleteMapping("/battles/{id}")
    ResponseEntity<?> deleteBattle(@PathVariable Long id) {
        battleRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }


    /**
     * Student joining battle alone
     * @param id
     * @param s_id
     * @return
     */
    @PostMapping("/battles/{id}/students/{s_id}")
    ResponseEntity<?> addStudent(@PathVariable Long id, @PathVariable Long s_id) {
        Battle battle = battleRepository.findById(id)
                .orElseThrow();

        Student student = studentRepository.findById(s_id)
                .orElseThrow();

        Team team = new Team(battle , student);
        battle.addTeam(team);

        battleRepository.save(battle);

        EntityModel<Battle> entityModel = assembler.toModel(battleRepository.save(battle));

        return ResponseEntity
                .created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }

    @GetMapping("/battles/{id}/students")
    List<Map<String, Object>> getStudents(@PathVariable Long id) {
        Battle battle = battleRepository.findById(id)
                .orElseThrow();

        List<Map<String, Object>> response = new ArrayList<>();
        battle.getTeams()
                .forEach(t -> {
                    Map<String, Object> team = new LinkedHashMap<>();
                    team.put("id", t.getId());
                    //team.put("name", t.getName());
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

}
