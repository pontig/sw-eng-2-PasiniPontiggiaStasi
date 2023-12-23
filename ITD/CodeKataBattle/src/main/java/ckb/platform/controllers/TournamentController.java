package ckb.platform.controllers;

import ckb.platform.entities.Educator;
import ckb.platform.entities.Tournament;
import ckb.platform.exceptions.TournamentNotFoundException;
import ckb.platform.repositories.TournamentRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class TournamentController {

    private final TournamentRepository repository;
    private final TournamentModelAssembler assembler;

    TournamentController(TournamentRepository repository, TournamentModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    //Aggregate root
    //tag::get-aggregate-root[]
    //CollectionModel is another Spring HATEOAS container aimed at encapsulating collections of resources, instead of a single resource entity.
    @GetMapping("/tournaments")
    CollectionModel<EntityModel<Tournament>> all() {
        List<EntityModel<Tournament>> tournaments = repository.findAll().stream()
            .map(assembler::toModel)
            .collect(Collectors.toList());

        return CollectionModel.of(tournaments, linkTo(methodOn(TournamentController.class).all()).withSelfRel());
    }

    //end::get-aggregate-root[]

    //Single item
    @GetMapping("/tournaments/{id}")
    EntityModel<Tournament> one(@PathVariable Long id) {
        Tournament tournament = repository.findById(id)
            .orElseThrow(() -> new TournamentNotFoundException(id));

        return assembler.toModel(tournament);
    }

    @PostMapping("/tournaments")
    ResponseEntity<?> newTournament(@RequestBody Tournament newTournament) {
        EntityModel<Tournament> entityModel = assembler.toModel(repository.save(newTournament));

        return ResponseEntity
            .created(entityModel.getRequiredLink("self").toUri())
            .body(entityModel);
    }

    @PutMapping("/tournaments/{id}")
    ResponseEntity<?> addGranted(@PathVariable Long id, @RequestBody Educator educator) {
        Tournament updatedTournament = repository.findById(id)
                .map(tournament -> {
                    tournament.addEducator(educator);
                    return repository.save(tournament);
                }).orElseThrow(() -> new TournamentNotFoundException(id));

        EntityModel<Tournament> entityModel = assembler.toModel(updatedTournament);

        return ResponseEntity
                .created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }
}
