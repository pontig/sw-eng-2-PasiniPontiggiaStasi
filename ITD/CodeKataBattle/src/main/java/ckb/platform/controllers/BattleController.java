package ckb.platform.controllers;

import ckb.platform.entities.Battle;
import ckb.platform.entities.Student;
import ckb.platform.entities.Team;
import ckb.platform.entities.Tournament;
import ckb.platform.repositories.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class BattleController {
    private final BattleRepository battleRepository;

    private final TournamentRepository tournamentRepository;
    private final StudentRepository studentRepository;
    private final EducatorRepository educatorRepository;
    private final BattleModelAssembler assembler;
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

        Map<String, Object> battleMap = new HashMap<>();
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

        Map<String, Object> rankingMap = new HashMap<>();
        battle.getRanking().forEach((team, score) -> {
            rankingMap.put("id", team.getId());
            //rankingMap.put("name", team.getName());
            rankingMap.put("score", score);
        });

        battleMap.put("ranking", rankingMap);

        return battleMap;
        }

    @GetMapping("/battles/{id}&{stu_id}")
    Map<String,Object> getBattleDetailsSTU(@PathVariable Long id, @PathVariable Long stu_id){
        Battle battle = battleRepository.findById(id)
                .orElseThrow();

        Map<String, Object> battleMap = new HashMap<>();
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
        battleMap.put("subscribed", battle.isSubscribed(studentRepository.findById(stu_id).orElseThrow()));
        battleMap.put("tournament_name", battle.getTournament().getName());
        battleMap.put("tournament_id", battle.getTournament().getId());

        Map<String, Object> rankingMap = new HashMap<>();
        battle.getRanking().forEach((team, score) -> {
            rankingMap.put("id", team.getId());
            //rankingMap.put("name", team.getName());
            rankingMap.put("score", score);
        });

        battleMap.put("ranking", rankingMap);

        return battleMap;
    }


    // Single item
    @GetMapping("/battles/{id}&{edu_id}")
    Map<String, Object> getBattleDetailsEDU(@PathVariable Long id, @PathVariable Long edu_id) {
        Battle battle = battleRepository.findById(id)
                .orElseThrow();

        Map<String, Object> battleMap = new HashMap<>();
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
        battleMap.put("admin", battle.getTournament().getGrantedEducators().contains(educatorRepository.findById(edu_id).orElseThrow()));
        battleMap.put("manual", battle.getManualEvaluation());

        Map<String, Object> rankingMap = new HashMap<>();
        battle.getRanking().forEach((team, score) -> {
            rankingMap.put("id", team.getId());
            //rankingMap.put("name", team.getName());
            rankingMap.put("score", score);
        });

        battleMap.put("ranking", rankingMap);

        return battleMap;
    }

    //TODO : MANCA LA PARTE DELLE EVALUATION, COME CAPISCO SE UN TEAM HA GIà LO SCORE O NO?
    @GetMapping("/battles/{b_id}/manualevalution")
    Map<String, Object> manualEvalGroups(@PathVariable Long b_id) {
        Battle battle = battleRepository.findById(b_id)
                .orElseThrow();

        Map<String, Object> response= new HashMap<>();
        return response;
    }

    @GetMapping("/battles/{b_id}/teams/{t_id}")
    Map<String, Object> getCode(@PathVariable Long b_id, @PathVariable Long t_id) {
        Battle battle = battleRepository.findById(b_id)
                .orElseThrow();
        Team team = teamRepository.findById(t_id)
                .orElseThrow();

        Map<String, Object> response= new HashMap<>();
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
    CollectionModel<EntityModel<Student>> getStudents(@PathVariable Long id) {
        Battle battle = battleRepository.findById(id)
                .orElseThrow();

        List<EntityModel<Student>> students = battle.getTeams().stream()
                .flatMap(team -> team.getStudents().stream())
                .map( student -> EntityModel.of(student,
                        linkTo(methodOn(StudentController.class).one(student.getId())).withSelfRel(),
                        linkTo(methodOn(StudentController.class).all()).withRel("students")))
                .collect(Collectors.toList());

        return CollectionModel.of(students, linkTo(methodOn(BattleController.class).getStudents(id)).withSelfRel());
    }

}
