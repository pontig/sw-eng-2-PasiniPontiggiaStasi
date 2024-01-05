package ckb.platform.controllers;

import ckb.platform.entities.Team;
import ckb.platform.exceptions.TeamNotFoundException;
import ckb.platform.repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class TeamController {

    @Autowired
    private final TeamRepository repository;
    private final TeamModelAssembler assembler;

    TeamController(TeamRepository repository, TeamModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("/teams")
    CollectionModel<EntityModel<Team>> all() {
        List<EntityModel<Team>> teams = repository.findAll().stream()
            .map(assembler::toModel)
            .collect(Collectors.toList());

        return CollectionModel.of(teams, linkTo(methodOn(TeamController.class).all()).withSelfRel());
    }

    // end::get-aggregate-root[]

    // Single item
    @GetMapping("/teams/{id}")
    EntityModel<Team> one(Long id) {
        Team team = repository.findById(id)
            .orElseThrow(() -> new TeamNotFoundException(id));

        return assembler.toModel(team);
    }

    @PostMapping("/teams")
    ResponseEntity<?> newTeam(@RequestBody Team newTeam) {
        EntityModel<Team> entityModel = assembler.toModel(repository.save(newTeam));

        return ResponseEntity
            .created(entityModel.getRequiredLink("self").toUri())
            .body(entityModel);
    }

    @PutMapping("/teams/{id}")
    ResponseEntity<?> replaceTeam(@RequestBody Team newTeam, @PathVariable Long id) {
        Team updatedTeam = repository.findById(id)
            .map(team -> {
                team.setBattle(newTeam.getBattle());
                team.setStudents(newTeam.getStudents());
                return repository.save(team);
            }).orElseThrow(() -> new TeamNotFoundException(id));

        EntityModel<Team> entityModel = assembler.toModel(updatedTeam);

        return ResponseEntity
            .created(entityModel.getRequiredLink("self").toUri())
            .body(entityModel);
    }

    @DeleteMapping("/teams/{id}")
    ResponseEntity<?> deleteTeam(@PathVariable Long id) {
        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }


}
