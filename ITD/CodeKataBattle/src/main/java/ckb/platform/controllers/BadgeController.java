package ckb.platform.controllers;

import ckb.platform.entities.Badge;
import ckb.platform.exceptions.BadgeNotFoundException;
import ckb.platform.repositories.BadgeRepository;
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
public class BadgeController {
    @Autowired
    private final BadgeRepository repository;
    private final BadgeModelAssembler assembler;

    BadgeController(BadgeRepository repository, BadgeModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("/badges")
    CollectionModel<EntityModel<Badge>> all() {
        List<EntityModel<Badge>> badges = repository.findAll().stream()
            .map(assembler::toModel)
            .collect(Collectors.toList());

        return CollectionModel.of(badges, linkTo(methodOn(BadgeController.class).all()).withSelfRel());
    }

    // end::get-aggregate-root[]

    // Single item
    @GetMapping("/badges/{id}")
    EntityModel<Badge> one(Long id) {
        Badge badge = repository.findById(id)
            .orElseThrow(() -> new BadgeNotFoundException(id));

        return assembler.toModel(badge);
    }

    @PostMapping("/badges")
    ResponseEntity<?> newBadge(@RequestBody Badge newBadge) {
        EntityModel<Badge> entityModel = assembler.toModel(repository.save(newBadge));

        return ResponseEntity
            .created(entityModel.getRequiredLink("self").toUri())
            .body(entityModel);
    }
}
