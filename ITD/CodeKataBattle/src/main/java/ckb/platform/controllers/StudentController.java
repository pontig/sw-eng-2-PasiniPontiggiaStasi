package ckb.platform.controllers;

import ckb.platform.entities.Student;
import ckb.platform.exceptions.StudentNotFoundException;
import ckb.platform.repositories.StudentRepository;
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

@RestController //It indicates that the data returned by each method will be written straight into the response body instead of rendering a template.
public class StudentController {
    private final StudentRepository repository;
    private final StudentModelAssembler assembler;

    StudentController(StudentRepository repository, StudentModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    // Aggregate root
    // tag::get-aggregate-root[]

    //CollectionModel is another Spring HATEOAS container aimed at encapsulating collections of resources, instea dof a single resource entity.

    @GetMapping("/students")
    Map<String, Object> all() {
        List<Student> students = repository.findAll();

        Map<String, Object> response = new HashMap<>();
        students.forEach(s ->{
            response.put("id", s.getId());
            response.put("firstName", s.getFirstName());
            response.put("surname", s.getLastName());
        });

        return response;
    }

    // end::get-aggregate-root[]

    // Single item
    @GetMapping("/students/{id}/profile")
    Map<String, Object> one(@PathVariable Long id) {
        Student student = repository.findById(id)
            .orElseThrow(() -> new StudentNotFoundException(id));

        Map<String, Object> response = new HashMap<>();

        response.put("id", student.getId());
        response.put("firstName", student.getFirstName());
        response.put("surname", student.getLastName());

        Map<String, Object> tournament = new HashMap<>();
        student.getTournaments().stream().map(t -> {
            tournament.put("id", t.getId());
            tournament.put("name", t.getName());
            return tournament;
        });

        response.put("tournaments", tournament);

        Map<String, Object> badges = new HashMap<>();
        student.getAchieveBadges().stream().map(b -> {
            badges.put("id", b.getId());
            //badges.put("name", b.getName());
            //TODO;
            return badges;
        });

        response.put("badges", badges);

        return response;
    }

    @PostMapping("/students")
    ResponseEntity<?> newStudent(@RequestBody Student newStudent) {
        EntityModel<Student> entityModel = assembler.toModel(repository.save(newStudent));

        return ResponseEntity
            .created(entityModel.getRequiredLink("self").toUri())
            .body(entityModel);
    }

    @DeleteMapping("/students/{id}")
    ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
