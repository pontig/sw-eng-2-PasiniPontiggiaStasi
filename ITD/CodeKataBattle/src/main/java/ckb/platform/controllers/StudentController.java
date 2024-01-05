package ckb.platform.controllers;

import ckb.platform.entities.Student;
import ckb.platform.exceptions.StudentNotFoundException;
import ckb.platform.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController //It indicates that the data returned by each method will be written straight into the response body instead of rendering a template.
public class StudentController {
    @Autowired
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
    List<Map<String, Object>> all() {
        List<Student> students = repository.findAll();

        List<Map<String, Object>> response = new ArrayList<>();
        students.forEach(s ->{
            Map<String, Object> student = new LinkedHashMap<>();
            student.put("id", s.getId());
            student.put("firstName", s.getFirstName());
            student.put("surname", s.getLastName());
            response.add(student);
        });

        return response;
    }


    //mapped to "Search for a student"
    @GetMapping("/students/{query}")
    List<Map<String, Object>> search(@PathVariable String query) {
        List<Student> students = repository.findByQuery(query);

        List<Map<String, Object>> response = new ArrayList<>();
        students.forEach(s ->{
            Map<String, Object> student = new LinkedHashMap<>();
            student.put("id", s.getId());
            student.put("firstName", s.getFirstName());
            student.put("surname", s.getLastName());
            response.add(student);
        });

        return response;
    }

    // end::get-aggregate-root[]

    // Single item
    //mapped to "Inspect a STU's profile"
    @GetMapping("/students/{id}/profile")
    Map<String, Object> one(@PathVariable Long id) {
        Student student = repository.findById(id)
            .orElseThrow(() -> new StudentNotFoundException(id));

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("id", student.getId());
        response.put("firstName", student.getFirstName());
        response.put("surname", student.getLastName());

        List<Map<String, Object>> tournaments = new ArrayList<>();
        student.getTournaments().forEach(t -> {
            Map<String, Object> tournament = new LinkedHashMap<>();
            tournament.put("id", t.getId());
            tournament.put("name", t.getName());
            tournaments.add(tournament);
        });

        response.put("tournaments", tournaments);

        List<Map<String, Object>> badges = new ArrayList<>();
        student.getAchieveBadges().forEach(b -> {
            Map<String, Object> badge = new LinkedHashMap<>();
            badge.put("id", b.getId());
            //badge.put("name", b.getName());
            //TODO;
            badges.add(badge);
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
