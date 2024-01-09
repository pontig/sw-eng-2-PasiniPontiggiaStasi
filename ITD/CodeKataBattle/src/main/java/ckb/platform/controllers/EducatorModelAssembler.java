package ckb.platform.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import ckb.platform.controllers.EducatorController;
import ckb.platform.entities.Educator;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class EducatorModelAssembler implements RepresentationModelAssembler<Educator, EntityModel<Educator>> {

    @Override
    public EntityModel<Educator> toModel(Educator educator) {
        return EntityModel.of(educator,
            //linkTo(methodOn(EducatorController.class).one(educator.getId())).withSelfRel(),
            linkTo(methodOn(EducatorController.class).all()).withRel("educators"));
    }
}
