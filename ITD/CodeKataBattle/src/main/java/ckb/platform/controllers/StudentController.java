package ckb.platform.controllers;

import ckb.platform.entities.Student;
import ckb.platform.exceptions.StudentNotFoundException;
import ckb.platform.repositories.StudentRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    CollectionModel<EntityModel<Student>> all() {
        List<EntityModel<Student>> students = repository.findAll().stream()
            .map(assembler::toModel)
            .collect(Collectors.toList());

        return CollectionModel.of(students, linkTo(methodOn(StudentController.class).all()).withSelfRel());
    }

    // end::get-aggregate-root[]

    // Single item
    @GetMapping("/students/{id}")
    EntityModel<Student> one(@PathVariable Long id) {
        Student student = repository.findById(id)
            .orElseThrow(() -> new StudentNotFoundException(id));

        return assembler.toModel(student);
    }

    @PostMapping("/students")
    ResponseEntity<?> newStudent(@RequestBody Student newStudent) {
        EntityModel<Student> entityModel = assembler.toModel(repository.save(newStudent));

        return ResponseEntity
            .created(entityModel.getRequiredLink("self").toUri())
            .body(entityModel);
    }

    @PutMapping("/students/{id}")
    ResponseEntity<?> replaceStudent(@RequestBody Student newStudent, @PathVariable Long id) {
        Student updatedStudent = repository.findById(id)
            .map(student -> {
                student.setEmail(newStudent.getEmail());
                student.setFirstName(newStudent.getFirstName());
                student.setLastName(newStudent.getLastName());
                student.setBattles(newStudent.getBattles());
                student.setAchieveBadges(newStudent.getAchieveBadges());
                student.setTournaments(newStudent.getTournaments());
                return repository.save(student);
            })
            .orElseGet(() -> {
                newStudent.setId(id);
                return repository.save(newStudent);
            });

        EntityModel<Student> entityModel = assembler.toModel(updatedStudent);

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
