package ckb.platform.controllers;

import ckb.platform.entities.Educator;
import ckb.platform.exceptions.EducatorNotFoundException;
import ckb.platform.repositories.EducatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class EducatorController {
    @Autowired
    private final EducatorRepository repository;
    private final EducatorModelAssembler assembler;

    EducatorController(EducatorRepository repository, EducatorModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    //Aggregate root
    //tag::get-aggregate-root[]

    //CollectionModel is another Spring HATEOAS container aimed at encapsulating collections of resources, instead of a single resource entity.

    @GetMapping("/educators")
    CollectionModel<EntityModel<Educator>> all() {
        List<EntityModel<Educator>> educators = repository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(educators, linkTo(methodOn(EducatorController.class).all()).withSelfRel());
    }
    //end::get-aggregate-root[]

    //Single item
    @GetMapping("/educators/{id}")
    EntityModel<Educator> one(@PathVariable Long id) {
        Educator educator = repository.findById(id)
                .orElseThrow(() -> new EducatorNotFoundException(id));

        return assembler.toModel(educator);
    }


    //mapped to "Search for an EDU"
    @GetMapping("/educators/{query}")
    List<Map<String, Object>> search(@PathVariable String query) {
        List<Educator> educators = repository.findByQuery(query);

        return educators.stream()
                .map(educator -> {
                    Map<String, Object> educatorMap = new HashMap<>();
                    educatorMap.put("id", educator.getId());
                    educatorMap.put("name", educator.getFirstName());
                    educatorMap.put("surname", educator.getLastName());

                    return educatorMap;
                })
                .collect(Collectors.toList());
    }

    @PostMapping("/educators")
    ResponseEntity<?> newEducator(@RequestBody Educator newEducator) {
        EntityModel<Educator> entityModel = assembler.toModel(repository.save(newEducator));

        return ResponseEntity
                .created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }

    @PutMapping("/educators/{id}")
    ResponseEntity<?> replaceEducator(@RequestBody Educator newEducator, @PathVariable Long id) {
        Educator updatedEducator = repository.findById(id)
                .map(educator -> {
                    educator.setEmail(newEducator.getEmail());
                    educator.setFirstName(newEducator.getFirstName());
                    educator.setLastName(newEducator.getLastName());
                    educator.setOwnedBattles(newEducator.getOwnedBattles());
                    educator.setOwnedTournaments(newEducator.getOwnedTournaments());
                    return repository.save(educator);
                })
                .orElseGet(() -> {
                    newEducator.setId(id);
                    return repository.save(newEducator);
                });

        EntityModel<Educator> entityModel = assembler.toModel(updatedEducator);

        return ResponseEntity
                .created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }

    @DeleteMapping("/educators/{id}")
    ResponseEntity<?> deleteEducator(@PathVariable Long id) {
        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
