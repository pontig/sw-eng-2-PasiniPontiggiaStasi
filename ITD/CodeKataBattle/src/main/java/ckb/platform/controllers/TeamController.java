package ckb.platform.controllers;

import ckb.platform.entities.*;
import ckb.platform.exceptions.StudentNotFoundException;
import ckb.platform.exceptions.TeamNotFoundException;
import ckb.platform.repositories.StudentRepository;
import ckb.platform.repositories.TeamRepository;
import ckb.platform.repositories.TournamentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class TeamController {

    @Autowired
    private final TeamRepository repository;
    @Autowired
    private final StudentRepository studentRepository;
    @Autowired
    private final TournamentRepository tournamentRepository;

    TeamController(TeamRepository repository, StudentRepository studentRepository, TournamentRepository tournamentRepository) {
        this.repository = repository;
        this.studentRepository = studentRepository;
        this.tournamentRepository = tournamentRepository;
    }

    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("/teams/{t_id}")
    public Map<String, Object> one(Long t_id) {
        Team t = repository.findById(t_id)
                .orElseThrow(() -> new TeamNotFoundException(t_id));
        HashMap<String, Object> team = new HashMap<>();
        team.put("id", t.getId());
        team.put("name", t.getName());
        team.put("score", t.getBattle().getRanking().get(t));
        List<Map<String, Object>> students = new ArrayList<>();
        t.getStudents().forEach(s -> {
            Map<String, Object> student = new LinkedHashMap<>();
            student.put("id", s.getId());
            student.put("firstName", s.getFirstName());
            student.put("lastName", s.getLastName());
            students.add(student);
        });
        team.put("students", students);
        ArrayList<Link> links = new ArrayList<>();
        links.add(linkTo(methodOn(TeamController.class).one(t_id)).withSelfRel());
        links.add(linkTo(methodOn(TeamController.class).all()).withRel("teams"));
        return team;
    }

    @GetMapping("/teams")
    public List<Map<String, Object>> all() {
        List<Team> teams = repository.findAll();
        List<Map<String, Object>> response = new ArrayList<>();
        teams.forEach(t -> {
            Map<String, Object> team = new LinkedHashMap<>();
            team.put("id", t.getId());
            team.put("name", t.getName());
            team.put("score", t.getBattle().getRanking().get(t));
            List<Map<String, Object>> students = new ArrayList<>();
            t.getStudents().forEach(s -> {
                Map<String, Object> student = new LinkedHashMap<>();
                student.put("id", s.getId());
                student.put("firstName", s.getFirstName());
                student.put("lastName", s.getLastName());
                students.add(student);
            });
            team.put("students", students);
            ArrayList<Link> links = new ArrayList<>();
            links.add(linkTo(methodOn(TeamController.class).one(t.getId())).withSelfRel());
            links.add(linkTo(methodOn(TeamController.class).all()).withRel("teams"));
            team.put("_links_", links);
            response.add(team);
        });
        return response;
    }

    @PostMapping("/teams/score/staticAnalysis/{t_id}")
    public ResponseEntity<?> updateStaticAnalysisScore(@PathVariable Long t_id, @RequestBody Map<String, Object> payload) {
        Team t = repository.findById(t_id)
                .orElseThrow(() -> new TeamNotFoundException(t_id));
        t.setStaticAnalysisScore((int) payload.get("score"));
        repository.save(t);
        return ResponseEntity.ok().build();
    }
    //TODO SCORES

    @GetMapping("/team/{t_id}/join/{s_id}")
    public ModelAndView joinTeam(@PathVariable Long t_id, @PathVariable Long s_id, HttpSession session) {
        Team teamToJoin = repository.findById(t_id).orElseThrow(() -> new TeamNotFoundException(t_id));
        Student stuToJoin =  studentRepository.findById(s_id).orElseThrow(() -> new StudentNotFoundException(s_id));
        Tournament t = teamToJoin.getBattle().getTournament();
        Battle b = teamToJoin.getBattle();

        boolean inTournament = true;
        boolean inBattle = false;

        if(!stuToJoin.getTournaments().contains(t) && !t.getSubscribedStudents().contains(stuToJoin)){
            // Check if the student is not in the tournament
            if(t.getSubscriptionDeadline().after(new Date())) {
                // Check if the tournament registration windows is open
                inTournament = false;
            } else
                return new ModelAndView("index");
        }

        Team oldTeam = null;
        for(Team team: b.getTeams()){
            // Check if the student is already in the battle and so in a team
            if(team.getStudents().contains(stuToJoin)) {
                // Check if the student is in a team
                oldTeam = team;

                if(oldTeam == teamToJoin)
                    // Check if it is already in the team
                    return new ModelAndView("index");

                inBattle = true;
                break;
            }
        }

        if(teamToJoin.getStudents().size() == b.getMaxStudents())
            // Check if the team is complete
            return new ModelAndView("index");

        if(!inTournament) {
            // Add to tournament
            t.addStudent(stuToJoin);
            stuToJoin.addTournament(t);
            studentRepository.save(stuToJoin);
        }

        if(b.getRegistrationDeadline().before(new Date()))
            // Can not register due end deadline
            return new ModelAndView("index");

        if(inBattle){
            oldTeam.removeStudent(stuToJoin);
            repository.save(oldTeam);
        }

        // Join team
        teamToJoin.addStudent(stuToJoin);
        repository.save(teamToJoin);

        User user = (User) session.getAttribute("user");
        ModelAndView modelAndView = new ModelAndView();

        if(user == null) {
            modelAndView.setViewName("index");
        } else {
            modelAndView.setViewName("indexSTU");
        }

        return modelAndView;
    }
}
