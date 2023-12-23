package ckb.platform.controllers;

import ckb.platform.entities.Battle;
import ckb.platform.repositories.BattleRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class BattleController {
    private final BattleRepository battleRepository;
    private final BattleModelAssembler assembler;

    BattleController(BattleRepository battleRepository, BattleModelAssembler assembler) {
        this.battleRepository = battleRepository;
        this.assembler = assembler;
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

    // Single item
    @GetMapping("/battles/{id}")
    EntityModel<Battle> one(Long id) {
        Battle battle = battleRepository.findById(id)
                .orElseThrow();

        return assembler.toModel(battle);
    }

    @PostMapping("/battles")
    ResponseEntity<?> newBattle(@RequestBody Battle newBattle) {
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

}
