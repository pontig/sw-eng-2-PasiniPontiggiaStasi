package ckb.platform.controllers;

import ckb.platform.entities.Battle;
import ckb.platform.entities.Educator;
import ckb.platform.entities.Student;
import ckb.platform.entities.Tournament;
import ckb.platform.exceptions.BattleNotFoundException;
import ckb.platform.exceptions.EducatorNotFoundException;
import ckb.platform.exceptions.StudentNotFoundException;
import ckb.platform.exceptions.TournamentNotFoundException;
import ckb.platform.repositories.BattleRepository;
import ckb.platform.repositories.EducatorRepository;
import ckb.platform.repositories.StudentRepository;
import ckb.platform.repositories.TournamentRepository;
import jakarta.websocket.OnClose;
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
public class TournamentController {

    @Autowired
    private final TournamentRepository tournamentRepository;
    private final TournamentModelAssembler assembler;
    @Autowired
    private final EducatorRepository educatorRepository;
    @Autowired
    private final StudentRepository studentRepository;
    @Autowired
    private final BattleRepository battleRepository;

    TournamentController(TournamentRepository tournamentRepository, TournamentModelAssembler assembler, EducatorRepository educatorRepository, StudentRepository studentRepository, BattleRepository battleRepository) {
        this.tournamentRepository = tournamentRepository;
        this.assembler = assembler;
        this.educatorRepository = educatorRepository;
        this.studentRepository = studentRepository;
        this.battleRepository = battleRepository;
    }

    //Aggregate root
    //tag::get-aggregate-root[]
    //CollectionModel is another Spring HATEOAS container aimed at encapsulating collections of resources, instead of a single resource entity.
    //mapped to "Get all Tournaments"
    @GetMapping("/tournaments")
    List<Map<String, Object>> all() {
        List<Tournament>tournaments = tournamentRepository.findAll();

        List<Map<String, Object>> response = new ArrayList<>();

        tournaments.forEach(t -> {
            Map<String, Object> tournament = new LinkedHashMap<>();
            tournament.put("id", t.getId());
            tournament.put("name", t.getName());
            tournament.put("first_name", t.getCreator().getFirstName());
            tournament.put("last_name", t.getCreator().getLastName());
            //tournament.put("active", t.isActive());
            response.add(tournament);
        });

        return response;
    }

    //end::get-aggregate-root[]

    //Single item
    @GetMapping("/tournaments/{id}")
    EntityModel<Tournament> one(@PathVariable Long id) {
        Tournament tournament = tournamentRepository.findById(id)
            .orElseThrow(() -> new TournamentNotFoundException(id));

        return assembler.toModel(tournament);
    }

    //mapped to "Get owned Tournaments"
    @GetMapping("/tournaments/owned/{edu_id}")
    List<Map<String, Object>> getOwnedTournaments(@PathVariable Long edu_id) {
        Educator educator = educatorRepository.findById(edu_id)
                .orElseThrow(() -> new EducatorNotFoundException(edu_id));

        List<Map<String, Object>> response = new ArrayList<>();

        educator.getOwnedTournaments().forEach(t -> {
            Map<String, Object> tournament = new LinkedHashMap<>();

            tournament.put("id", t.getId());
            tournament.put("first_name", t.getCreator().getFirstName());
            tournament.put("last_name", t.getCreator().getLastName());
            //tournament.put("active", t.isActive());
            response.add(tournament);
        });

        return response;
    }

    @PostMapping("/tournaments")
    ResponseEntity<?> newTournament(@RequestBody Tournament newTournament) {
        EntityModel<Tournament> entityModel = assembler.toModel(tournamentRepository.save(newTournament));

        return ResponseEntity
            .created(entityModel.getRequiredLink("self").toUri())
            .body(entityModel);
    }

    @PostMapping("/tournaments/{t_id}/educators/{e_id}")
    ResponseEntity<?> addGranted(@PathVariable Long t_id, @PathVariable Long e_id) {
        Educator grantedEducator = educatorRepository.findById(e_id)
                .orElseThrow(() -> new EducatorNotFoundException(e_id));

        Tournament updatedTournament = tournamentRepository.findById(t_id)
                .map(tournament -> {
                    tournament.addEducator(grantedEducator);
                    return tournamentRepository.save(tournament);
                }).orElseThrow(() -> new TournamentNotFoundException(t_id));

        EntityModel<Tournament> entityModel = assembler.toModel(updatedTournament);

        return ResponseEntity
                .created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }

    @PostMapping("/tournaments/{t_id}/students/{s_id}")
    ResponseEntity<?> addStudent(@PathVariable Long t_id, @PathVariable Long s_id) {
        Student newSubscriber = studentRepository.findById(s_id)
                .orElseThrow(() -> new StudentNotFoundException(s_id));

        Tournament updatedTournament = tournamentRepository.findById(t_id)
                .map(tournament -> {
                    tournament.addStudent(newSubscriber);
                    return tournamentRepository.save(tournament);
                }).orElseThrow(() -> new TournamentNotFoundException(t_id));

        EntityModel<Tournament> entityModel = assembler.toModel(updatedTournament);

        return ResponseEntity
                .created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }

    //mapped to "Get tournament details " for a stu
    //TODO: now i use a stu id as session token, but it has to be replaced, same to all other endpoints, we need to pass the token to check the permits
    @GetMapping("/tournaments/stu/{id}")
    Map<String, Object> tournamentDetailsSTU(@PathVariable Long t_id, @RequestParam Long stu_id) {
        Tournament tournament = tournamentRepository.findById(t_id)
                .orElseThrow(() -> new TournamentNotFoundException(t_id));

        Student student = studentRepository.findById(stu_id)
                .orElseThrow(() -> new StudentNotFoundException(stu_id));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", tournament.getId());
        response.put("name", tournament.getName());
        //response.put("active", tournament.isActive());
        response.put("canSubscribe", tournament.getSubscriptionDeadline().compareTo(new Date())>0);
        response.put("subscribed", tournament.getSubscribedStudents().contains(student));
        response.put("battles", tournament.getBattles().stream().map( battle -> {
            Map<String, Object> battleMap = new LinkedHashMap<>();
            battleMap.put("id", battle.getId());
            //battleMap.put("name", battle.getName());
            //battleMap.put("language", battle.getLanguage());
            battleMap.put("participants", battle.getTeams().stream().reduce(0, (sum, team) -> sum + team.getStudents().size(), Integer::sum));
            battleMap.put("subscribed", battle.getTeams().stream().anyMatch(team -> team.getStudents().contains(student)));
            //battleMap.put("score",
            //battleMap.put("phase", battle.getPhase());
            //battleMap.put("remaining", battle.getRemainingTime().toString()); --> come si calcola?
            return battleMap;
        }));
        ArrayList<Map<String, Object>> rankings= new ArrayList<>();
        tournament.getRanking().forEach((Student, score) -> {
            Map<String, Object> rankingMap = new LinkedHashMap<>();
            rankingMap.put("id", Student.getId());
            rankingMap.put("firstname", Student.getFirstName());
            rankingMap.put("lastname", Student.getLastName());
            rankingMap.put("points", score);
            rankings.add(rankingMap);
        });
        response.put("ranking", rankings);
        return response;
    }


    //mapped to "Get tournament details"
    @GetMapping("/tournaments/edu/{id}")
    Map<String, Object> tournamentDetailsEDU(@PathVariable Long t_id, @RequestParam Long edu_id) {
        Tournament tournament = tournamentRepository.findById(t_id)
                .orElseThrow(() -> new TournamentNotFoundException(t_id));
        //check if the id is an educator
        Educator educator = educatorRepository.findById(edu_id)
                .orElseThrow(() -> new EducatorNotFoundException(edu_id));


        Map<String, Object> tournamentMap = new LinkedHashMap<>();
        tournamentMap.put("id", tournament.getId());
        tournamentMap.put("name", tournament.getName());
        //tournamentMap.put("active", tournament.isActive());
        tournamentMap.put("admin", tournament.getGrantedEducators().contains(educator));
        tournamentMap.put("battles", tournament.getBattles().stream().map( battle -> {
            Map<String, Object> battleMap = new LinkedHashMap<>();
            battleMap.put("id", battle.getId());
            //battleMap.put("name", battle.getName());
            //battleMap.put("language", battle.getLanguage());
            battleMap.put("participants", battle.getTeams().stream().reduce(0, (sum, team) -> sum + team.getStudents().size(), Integer::sum));
            //battleMap.put("phase", battle.getPhase());
            //battleMap.put("remaining", battle.getRemainingTime().toString()); --> come si calcola?
            return battleMap;
        }));

        ArrayList<Map<String, Object>> rankings= new ArrayList<>();
         tournament.getRanking().forEach((Student, score) -> {
             Map<String, Object> rankingMap = new LinkedHashMap<>();
             rankingMap.put("id", Student.getId());
             rankingMap.put("firstname", Student.getFirstName());
             rankingMap.put("lastname", Student.getLastName());
             rankingMap.put("points", score);
             rankings.add(rankingMap);
        });
        tournamentMap.put("ranking", rankings);
        return tournamentMap;
    }

    //mapped to "Get subscribed tournaments"
    @GetMapping("/tournaments/subscribed/")
    List<Map<String, Object>> getSubscribedTournaments(@RequestParam Long s_id) {
        Student student = studentRepository.findById(s_id)
                .orElseThrow(() -> new StudentNotFoundException(s_id));

        List<Map<String, Object>> response = new ArrayList<>();

        student.getTournaments().forEach(t -> {
            Map<String, Object> tournamentMap = new LinkedHashMap<>();
            tournamentMap.put("id", t.getId());
            tournamentMap.put("name", t.getName());
            tournamentMap.put("first_name", t.getCreator().getFirstName());
            tournamentMap.put("last_name", t.getCreator().getLastName());
            //tournamentMap.put("active", t.isActive());
            response.add(tournamentMap);
        });

        return response;
    }

    //mapped to "Get unsubscribed tournaments"
    @GetMapping("/tournaments/unsuscribed/")
    List<Map<String, Object>> getUnsubscribedTournaments(@RequestParam Long s_id) {
        Student student = studentRepository.findById(s_id)
                .orElseThrow(() -> new StudentNotFoundException(s_id));

       List<Map<String, Object>> response = new ArrayList<>();

        tournamentRepository.findAll().forEach(t -> {
            if(!student.getTournaments().contains(t)) {
                Map<String, Object> tournamentMap = new LinkedHashMap<>();
                tournamentMap.put("id", t.getId());
                tournamentMap.put("name", t.getName());
                tournamentMap.put("first_name", t.getCreator().getFirstName());
                tournamentMap.put("last_name", t.getCreator().getLastName());
                //tournamentMap.put("daysLeft", t.getSubscriptionDeadline());
                response.add(tournamentMap);
            }
        });

        return response;
    }

}


