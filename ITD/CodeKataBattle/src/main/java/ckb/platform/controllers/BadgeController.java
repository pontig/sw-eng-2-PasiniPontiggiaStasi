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

    BadgeController(BadgeRepository repository) {
        this.repository = repository;
    }

    // Aggregate root
    // tag::get-aggregate-root[]
   /*@GetMapping("/badges")
    CollectionModel<EntityModel<Badge>> all() {
    }*/

    // end::get-aggregate-root[]

    // Single item
    /*@GetMapping("/badges/{id}")
    EntityModel<Badge> one(Long id) {
    }*/
    //todo : add badge, delete badge, update badge, get badge
}
