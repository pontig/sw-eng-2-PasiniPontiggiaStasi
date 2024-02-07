package ckb.platform.controllers;

import ckb.platform.entities.Battle;
import ckb.platform.entities.Student;
import ckb.platform.entities.Tournament;
import ckb.platform.exceptions.StudentNotFoundException;
import ckb.platform.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController //It indicates that the data returned by each method will be written straight into the response body instead of rendering a template.
public class StudentController {
    @Autowired
    private final StudentRepository repository;

    StudentController(StudentRepository repository) {
        this.repository = repository;
    }

    //CollectionModel is another Spring HATEOAS container aimed at encapsulating collections of resources, instead of a single resource entity.

    @Deprecated
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
    // CHECKED BY @PONTIG
    @GetMapping("/students/{query}")
    List<Map<String, Object>> search(@PathVariable String query) {
        List<Student> students = repository.findByQuery(query);

        List<Map<String, Object>> response = new ArrayList<>();
        students.forEach(s ->{
            Map<String, Object> student = new LinkedHashMap<>();
            student.put("id", s.getId());
            student.put("name", s.getFirstName());
            student.put("surname", s.getLastName());
            response.add(student);
        });

        return response;
    }

    // Single item
    //mapped to "Inspect a STU's profile"
    // CHECKED BY @PONTIG
    @GetMapping("/students/{id}/profile")
    Map<String, Object> one(@PathVariable Long id) {
        Student student = repository.findById(id)
            .orElseThrow(() -> new StudentNotFoundException(id));

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("id", student.getId());
        response.put("name", student.getFirstName());
        response.put("surname", student.getLastName());

        response.put("subscribedTournaments", student.getTournaments().stream().map(Tournament::getName).toList());
        response.put("subscribedTeams", student.getTournaments().stream()
                .map(Tournament::getBattles)
                .flatMap(Collection::stream)
                .map(Battle::getTeams)
                .flatMap(Collection::stream)
                .filter(t -> t.getStudents().contains(student))
                .map(t -> t.getBattle().getTournament().getName() + " > " + t.getName() + " in " +  t.getBattle().getName())
                .toList());
        return response;
    }

}
