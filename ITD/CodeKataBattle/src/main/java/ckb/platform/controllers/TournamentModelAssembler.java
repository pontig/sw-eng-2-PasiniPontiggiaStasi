package ckb.platform.controllers;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import ckb.platform.controllers.TournamentController;
import ckb.platform.entities.Tournament;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class TournamentModelAssembler implements RepresentationModelAssembler<Tournament, EntityModel<Tournament>>{

        @Override
        public EntityModel<Tournament> toModel(Tournament tournament) {
            return EntityModel.of(tournament,
                linkTo(methodOn(TournamentController.class).one(tournament.getId())).withSelfRel(),
                linkTo(methodOn(TournamentController.class).all()).withRel("tournaments"));
        }
}
