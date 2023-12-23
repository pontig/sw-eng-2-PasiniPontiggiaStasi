package ckb.platform.controllers;

import ckb.platform.entities.Battle;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import ckb.platform.controllers.BattleController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class BattleModelAssembler implements RepresentationModelAssembler<Battle, EntityModel<Battle>> {

    @Override
    public EntityModel<Battle> toModel(Battle battle) {
        return EntityModel.of(battle,
            linkTo(methodOn(BattleController.class).one(battle.getId())).withSelfRel(),
            linkTo(methodOn(BattleController.class).all()).withRel("battles"));
    }
}
