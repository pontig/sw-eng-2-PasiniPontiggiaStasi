package ckb.platform.controllers;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import ckb.platform.controllers.BadgeController;
import ckb.platform.entities.Badge;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class BadgeModelAssembler implements RepresentationModelAssembler<Badge, EntityModel<Badge>> {

        @Override
        public EntityModel<Badge> toModel(Badge badge) {
            return EntityModel.of(badge,
                linkTo(methodOn(BadgeController.class).one(badge.getId())).withSelfRel(),
                linkTo(methodOn(BadgeController.class).all()).withRel("badges"));
        }
}
