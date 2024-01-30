package ckb.platform.controllers;

import ckb.platform.entities.Team;
import ckb.platform.exceptions.TeamNotFoundException;
import ckb.platform.repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class TeamController {

    @Autowired
    private final TeamRepository repository;

    TeamController(TeamRepository repository) {
        this.repository = repository;
    }

    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("/teams/{t_id}")
    public Map<String, Object> one(Long t_id) {
        Team t = repository.findById(t_id)
                .orElseThrow(() -> new TeamNotFoundException(t_id));
        HashMap<String, Object> team = new HashMap<>();
        team.put("id", t.getId());
        team.put("name", t.getName());
        team.put("score", t.getBattle().getRanking().get(t));
        List<Map<String, Object>> students = new ArrayList<>();
        t.getStudents().forEach(s -> {
            Map<String, Object> student = new LinkedHashMap<>();
            student.put("id", s.getId());
            student.put("firstName", s.getFirstName());
            student.put("lastName", s.getLastName());
            students.add(student);
        });
        team.put("students", students);
        ArrayList<Link> links = new ArrayList<>();
        links.add(linkTo(methodOn(TeamController.class).one(t_id)).withSelfRel());
        links.add(linkTo(methodOn(TeamController.class).all()).withRel("teams"));
        return team;
    }

    @GetMapping("/teams")
    public List<Map<String, Object>> all() {
        List<Team> teams = repository.findAll();
        List<Map<String, Object>> response = new ArrayList<>();
        teams.forEach(t -> {
            Map<String, Object> team = new LinkedHashMap<>();
            team.put("id", t.getId());
            team.put("name", t.getName());
            team.put("score", t.getBattle().getRanking().get(t));
            List<Map<String, Object>> students = new ArrayList<>();
            t.getStudents().forEach(s -> {
                Map<String, Object> student = new LinkedHashMap<>();
                student.put("id", s.getId());
                student.put("firstName", s.getFirstName());
                student.put("lastName", s.getLastName());
                students.add(student);
            });
            team.put("students", students);
            ArrayList<Link> links = new ArrayList<>();
            links.add(linkTo(methodOn(TeamController.class).one(t.getId())).withSelfRel());
            links.add(linkTo(methodOn(TeamController.class).all()).withRel("teams"));
            team.put("_links_", links);
            response.add(team);
        });
        return response;
    }

    @PostMapping("/teams/score/staticAnalysis/{t_id}")
    public ResponseEntity<?> updateStaticAnalysisScore(@PathVariable Long t_id, @RequestBody Map<String, Object> payload) {
        Team t = repository.findById(t_id)
                .orElseThrow(() -> new TeamNotFoundException(t_id));
        t.setStaticAnalysisScore((int) payload.get("score"));
        repository.save(t);
        return ResponseEntity.ok().build();
    }
    //TODO SCORES
}
