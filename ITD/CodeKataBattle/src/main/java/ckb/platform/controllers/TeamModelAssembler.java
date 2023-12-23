package ckb.platform.controllers;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import ckb.platform.controllers.TeamController;
import ckb.platform.entities.Team;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class TeamModelAssembler implements RepresentationModelAssembler<Team, EntityModel<Team>>{

        @Override
        public EntityModel<Team> toModel(Team team) {
            return EntityModel.of(team,
                linkTo(methodOn(TeamController.class).one(team.getId())).withSelfRel(),
                linkTo(methodOn(TeamController.class).all()).withRel("teams"));
        }
}
