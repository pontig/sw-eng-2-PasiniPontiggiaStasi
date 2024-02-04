package ckb.platform.controllers;

import ckb.platform.entities.Educator;
import ckb.platform.repositories.EducatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class EducatorController {
    @Autowired
    private final EducatorRepository repository;

    EducatorController(EducatorRepository repository) {
        this.repository = repository;
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
                    //educatorMap.put("_links_", linkTo(methodOn(EducatorController.class).search(educator.getId().toString())).withSelfRel());
                    return educatorMap;
                })
                .collect(Collectors.toList());
    }

}
