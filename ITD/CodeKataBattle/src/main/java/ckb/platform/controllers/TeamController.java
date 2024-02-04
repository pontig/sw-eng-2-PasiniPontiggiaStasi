package ckb.platform.controllers;

import ckb.platform.entities.*;
import ckb.platform.exceptions.TeamNotFoundException;
import ckb.platform.repositories.StudentRepository;
import ckb.platform.repositories.TeamRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
@RestController
public class TeamController {

    @Autowired
    private final TeamRepository repository;
    @Autowired
    private final StudentRepository studentRepository;

    TeamController(TeamRepository repository, StudentRepository studentRepository) {
        this.repository = repository;
        this.studentRepository = studentRepository;
    }

    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("/teams/{t_id}")
    public Map<String, Object> one(@PathVariable Long t_id) {
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
            response.add(team);
        });
        return response;
    }

    @PostMapping("/teams/score/staticAnalysis/{t_id}")
    public ResponseEntity<?> updateStaticAnalysisScore(@PathVariable Long t_id, @RequestBody Map<String, Object> payload) {
        Team t = repository.findById(t_id)
                .orElseThrow(() -> new TeamNotFoundException(t_id));
        t.setSecurityScore((Integer) payload.get("securityScore"));
        t.setReliabilityScore((Integer) payload.get("reliabilityScore"));
        t.setMaintainabilityScore((Integer) payload.get("maintainabilityScore"));
        repository.save(t);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/team/{t_id}/join/{s_id}")
    public ModelAndView joinTeam(@PathVariable Long t_id, @PathVariable Long s_id, HttpSession session) {
        Optional<Team> teamOpt = repository.findById(t_id);
        Team teamToJoin;
        if(teamOpt.isEmpty())
            return new ModelAndView("redirect:/index.html?error=Team do not exist");
        else
            teamToJoin = teamOpt.get();

        Optional<Student> stuOpt =  studentRepository.findById(s_id);
        Student stuToJoin;
        if(stuOpt.isEmpty())
            return new ModelAndView("redirect:/index.html?error=Student do not exist");
        else
            stuToJoin = stuOpt.get();

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
                return new ModelAndView("redirect:/index.html?error=You are not registered to the tournament which is closed");
        }

        Team oldTeam = null;
        for(Team team: b.getTeams()){
            // Check if the student is already in the battle and so in a team
            if(team.getStudents().contains(stuToJoin)) {
                // Check if the student is in a team
                oldTeam = team;

                if(oldTeam == teamToJoin)
                    // Check if it is already in the team
                    return new ModelAndView("redirect:/index.html?error=You are already in the team");

                inBattle = true;
                break;
            }
        }

        if(teamToJoin.getStudents().size() == b.getMaxStudents())
            // Check if the team is complete
            return new ModelAndView("redirect:/index.html?error=The team you are trying to join is full");

        if(!inTournament) {
            // Add to tournament
            t.addStudent(stuToJoin);
            stuToJoin.addTournament(t);
            studentRepository.save(stuToJoin);
        }

        if(b.getRegistrationDeadline().before(new Date()))
            // Can not register due end deadline
            return new ModelAndView("redirect:/index.html?error=The battle registration deadline is expired you can not join the team");

        if(inBattle){
            oldTeam.removeStudent(stuToJoin);
            repository.save(oldTeam);
        }

        // Join team
        teamToJoin.addStudent(stuToJoin);
        repository.save(teamToJoin);

        User user = (User) session.getAttribute("user");

        if(user == null)
            return new ModelAndView("redirect:/index.html?joined=You joined the team now log in");
        else
            return new ModelAndView("redirect:/indexSTU.html?joined=You joined the team");

    }
}
