package ckb.platform.controllers;

import ckb.platform.entities.*;
import ckb.platform.exceptions.*;
import ckb.platform.formParser.*;
import ckb.platform.gitHubAPI.GitHubAPI;
import ckb.platform.gmailAPI.GmailAPI;
import ckb.platform.repositories.*;
import ckb.platform.scheduler.RegistrationThread;
import ckb.platform.scheduler.SubmissionThread;
import ckb.platform.testRepo.TestRepository;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.mail.MessagingException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class BattleController {

    private static final Logger log = LoggerFactory.getLogger(BattleController.class);
    @Autowired
    private final BattleRepository battleRepository;
    @Autowired
    private final TournamentRepository tournamentRepository;
    @Autowired
    private final StudentRepository studentRepository;
    @Autowired
    private final EducatorRepository educatorRepository;
    @Autowired
    private final TeamRepository teamRepository;

    BattleController(BattleRepository battleRepository, TournamentRepository tournamentRepository, StudentRepository studentRepository, EducatorRepository educatorRepository, TeamRepository teamRepository) {
        this.battleRepository = battleRepository;
        this.tournamentRepository = tournamentRepository;
        this.studentRepository = studentRepository;
        this.educatorRepository = educatorRepository;
        this.teamRepository = teamRepository;
    }

    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("/battles")
    @Deprecated
    List<Map<String, Object>> all() {
        List<Battle> battles = battleRepository.findAll();
        List<Map<String, Object>> response = new ArrayList<>();

        for (Battle battle : battles) {
            Map<String, Object> battleMap = new LinkedHashMap<>();
            battleMap.put("id", battle.getId());
            battleMap.put("title", battle.getTitle());
            battleMap.put("description", battle.getDescription());
            battleMap.put("language", battle.getLanguage());
            battleMap.put("opening", battle.getOpenDate());
            battleMap.put("registration", battle.getRegistrationDeadline().toString());
            battleMap.put("closing", battle.getFinalSubmissionDeadline().toString());
            battleMap.put("min_group_size", battle.getMinStudents());
            battleMap.put("max_group_size", battle.getMaxStudents());
            battleMap.put("phase", battle.getPhase());
            battleMap.put("tournament_name", battle.getTournament().getName());
            battleMap.put("tournament_id", battle.getTournament().getId());

            response.add(battleMap);
        }

        return response;
    }

    @Deprecated
    @GetMapping("/battles/{id}")
    Map<String, Object> one(@PathVariable Long id) {
        Battle battle = battleRepository.findById(id)
                .orElseThrow(() -> new BattleNotFoundException(id));

        Map<String, Object> battleMap = new LinkedHashMap<>();
        battleMap.put("id", battle.getId());
        battleMap.put("title", battle.getTitle());
        battleMap.put("description", battle.getDescription());
        battleMap.put("language", battle.getLanguage());
        battleMap.put("opening", battle.getOpenDate());
        battleMap.put("registration", battle.getRegistrationDeadline().toString());
        battleMap.put("closing", battle.getFinalSubmissionDeadline().toString());
        battleMap.put("min_group_size", battle.getMinStudents());
        battleMap.put("max_group_size", battle.getMaxStudents());
        battleMap.put("phase", battle.getPhase());
        battleMap.put("tournament_name", battle.getTournament().getName());
        battleMap.put("tournament_id", battle.getTournament().getId());

        List<Map<String, Object>> rankings = battle.getRanking().entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(ArrayList::new, (list, entry) -> {
            Map<String, Object> rankingMap = new LinkedHashMap<>();
            rankingMap.put("id", entry.getKey().getId());
            rankingMap.put("name", entry.getKey().getName());
            rankingMap.put("score", entry.getValue());
            list.add(rankingMap);
        }, ArrayList::addAll);


        battleMap.put("ranking", rankings);

        return battleMap;
    }

    //mapped to "Get Battle Details"
    // CHECKED BY @PONTIG
    @GetMapping("/battles/stu/{id}")
    Map<String, Object> getBattleDetailsSTU(@PathVariable Long id, HttpSession session) {
        Battle battle = battleRepository.findById(id)
                .orElseThrow(() -> new BattleNotFoundException(id));

        //check user passed is a stu
        User user = (User) session.getAttribute("user");
        if (user == null || user.isEdu()) {
            throw new StudentNotFoundException(id);
        }


        Map<String, Object> battleMap = new LinkedHashMap<>();
        battleMap.put("id", battle.getId());
        battleMap.put("title", battle.getTitle());
        battleMap.put("link", battle.getDescription());
        battleMap.put("language", battle.getLanguage());
        battleMap.put("opening", battle.getOpenDate());
        battleMap.put("registration", battle.getRegistrationDeadline().toString());
        battleMap.put("closing", battle.getFinalSubmissionDeadline().toString());
        battleMap.put("min_group_size", battle.getMinStudents());
        battleMap.put("max_group_size", battle.getMaxStudents());
        battleMap.put("phase", battle.getPhase());
        boolean after = battle.getRegistrationDeadline().after(new Date());
        boolean alreadyIn = battle.isSubscribed(studentRepository.findById(user.getId()).orElseThrow(() -> new StudentNotFoundException(user.getId())));
        if (alreadyIn)
            battleMap.put("team_id", battle.getTeams().stream().filter(team -> team.getStudents().contains(studentRepository.findById(user.getId()).orElseThrow(() -> new StudentNotFoundException(user.getId())))).findFirst().orElseThrow(() -> new TeamNotFoundException(-1L)).getId());
        battleMap.put("canSubscribe", after && !alreadyIn);
        battleMap.put("canInviteOthers",
                battle.getRegistrationDeadline().after(new Date()) &&
                        battle.isSubscribed(studentRepository.findById(user.getId()).orElseThrow(() -> new StudentNotFoundException(user.getId()))) &&
                        battle.getTeams().stream().filter(team -> team.getStudents().contains(studentRepository.findById(user.getId()).orElseThrow(() -> new StudentNotFoundException(user.getId())))).count() < battle.getMaxStudents());
        battleMap.put("minConstraintSatisfied", battle.getMinStudents() <= battle.getTeams().stream().filter(team -> team.getStudents().contains(studentRepository.findById(user.getId()).orElseThrow(() -> new StudentNotFoundException(user.getId())))).count());
        battleMap.put("subscribed", battle.isSubscribed(studentRepository.findById(user.getId()).orElseThrow(() -> new StudentNotFoundException(user.getId()))));
        battleMap.put("tournament_name", battle.getTournament().getName());
        battleMap.put("tournament_id", battle.getTournament().getId());

        List<Map<String, Object>> rankings = battle.getRanking().entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(ArrayList::new, (list, entry) -> {
            Map<String, Object> rankingMap = new LinkedHashMap<>();
            rankingMap.put("id", entry.getKey().getId());
            rankingMap.put("name", entry.getKey().getName());
            rankingMap.put("score", entry.getValue());
            list.add(rankingMap);
        }, ArrayList::addAll);

        battleMap.put("ranking", rankings);
        return battleMap;
    }

    //mapped to "Get Battle Details"
    // CHECKED BY @PONTIG
    @GetMapping("/battles/edu/{id}")
    Map<String, Object> getBattleDetailsEDU(@PathVariable Long id, HttpSession session) {
        Battle battle = battleRepository.findById(id)
                .orElseThrow(() -> new BattleNotFoundException(id));

        //check if user passed is edu
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isEdu()) {
            throw new EducatorNotFoundException(id);
        }
        Educator educator = educatorRepository.findById(user.getId())
                .orElseThrow(() -> new EducatorNotFoundException(user.getId()));

        Map<String, Object> battleMap = new LinkedHashMap<>();
        battleMap.put("id", battle.getId());
        battleMap.put("title", battle.getTitle());
        battleMap.put("link", battle.getDescription());
        battleMap.put("language", battle.getLanguage());
        battleMap.put("opening", battle.getOpenDate());
        battleMap.put("registration", battle.getRegistrationDeadline().toString());
        battleMap.put("closing", battle.getFinalSubmissionDeadline().toString());
        battleMap.put("min_group_size", battle.getMinStudents());
        battleMap.put("max_group_size", battle.getMaxStudents());
        battleMap.put("phase", battle.getPhase());
        battleMap.put("tournament_name", battle.getTournament().getName());
        battleMap.put("tournament_id", battle.getTournament().getId());
        battleMap.put("admin", battle.getTournament().getGrantedEducators().contains(educator));
        battleMap.put("manual", battle.getManualEvaluation());

        List<Map<String, Object>> rankings = battle.getRanking().entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(ArrayList::new, (list, entry) -> {
            Map<String, Object> rankingMap = new LinkedHashMap<>();
            rankingMap.put("id", entry.getKey().getId());
            rankingMap.put("name", entry.getKey().getName());
            rankingMap.put("score", entry.getValue());
            list.add(rankingMap);
        }, ArrayList::addAll);

        battleMap.put("ranking", rankings);

        return battleMap;
    }

    //mapped to "Get the list of groups for the manual evaluation"
    // CHECKED BY @PONTIG
    @GetMapping("/battles/{b_id}/manualEvaluation")
    List<Map<String, Object>> manualEvalGroups(@PathVariable Long b_id) {
        Battle battle = battleRepository.findById(b_id)
                .orElseThrow(() -> new BattleNotFoundException(b_id));

        List<Map<String, Object>> response = new ArrayList<>();

        battle.getTeams().forEach(t -> {
            Map<String, Object> team = new LinkedHashMap<>();
            team.put("id", t.getId());
            team.put("name", t.getName());
            team.put("score", t.getManualScore());
            team.put("link", t.getCode());
            response.add(team);
        });

        return response;
    }

    //mapped to "Evaluate code"
    @Deprecated // In the end, we decided to use a different approach
    @GetMapping("/battles/{b_id}/teams/{t_id}")
    Map<String, Object> getCode(@PathVariable Long b_id, @PathVariable Long t_id, HttpSession session) {
        Battle battle = battleRepository.findById(b_id)
                .orElseThrow(() -> new BattleNotFoundException(b_id));
        Team team = teamRepository.findById(t_id)
                .orElseThrow(() -> new TeamNotFoundException(t_id));
        //check if user passed is edu
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isEdu()) {
            throw new EducatorNotFoundException(-1L);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("group_id", team.getId());
        response.put("battle_id", battle.getId());
        response.put("language", battle.getLanguage());
        response.put("name", team.getName());
        response.put("score", battle.getRanking().get(team));
        response.put("code", team.getCode());
        return response;
    }

    @GetMapping("/battles/{id}/students")
    @Deprecated
    List<Map<String, Object>> getStudents(@PathVariable Long id) {
        Battle battle = battleRepository.findById(id)
                .orElseThrow(() -> new BattleNotFoundException(id));

        List<Map<String, Object>> response = new ArrayList<>();
        battle.getTeams()
                .forEach(t -> {
                    Map<String, Object> team = new LinkedHashMap<>();
                    team.put("id", t.getId());
                    team.put("name", t.getName());
                    team.put("score", battle.getRanking().get(t));
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

    // mapped to "Invite single student to team"
    // CHECKED BY @PONTIG
    @PostMapping("/battles/invite")
    ResponseEntity<?> inviteStudent(@RequestBody InviteSinglePersonRequest request, HttpSession session) {
        final User user = (User) session.getAttribute("user");
        if (user.isEdu()) {
            throw new StudentNotFoundException(user.getId());
        }

        String email = request.getMail();
        Long team_id = request.getTeam_id();

        Team team = teamRepository.findById(team_id)
                .orElseThrow(() -> new TeamNotFoundException(team_id));
        Long battle_id = team.getBattle().getId();

        Battle battle = battleRepository.findById(battle_id)
                .orElseThrow(() -> new BattleNotFoundException(battle_id));

        Student studentToInvite = studentRepository.
                getAllStudentInPlatform().
                stream().
                filter(s -> s.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new StudentNotFoundException(-1L));

        team.addStudent(studentToInvite);
        teamRepository.save(team);

        battleRepository.save(battle);

        new Thread(() -> {
            // Prepare Email to send
            GmailAPI gmailSender;

            try {
                gmailSender = new GmailAPI();
            } catch (IOException | GeneralSecurityException e) {
                throw new RuntimeException(e);
            }

            String subject = "JOIN TEAM " + team.getName() + " for battle " + battle.getName();

            String body = "Hi " + studentToInvite.getFirstName() + ",\n\n" +
                    user.getFirstName() + " invited you to join its team " + team.getName() + "\n" +
                    "You can compete in battle " + battle.getName() + " together with the team members!\n\n" +
                    "To join your mates in this adventure click the link below before it is too late: " +
                    "https://localhost:8080/ckb_platform/team/" + team.getId() + "/join/" + studentToInvite.getId() + "\n" +
                    "The battle registration window will close on: " + battle.getRegistrationDeadline() + "\n\n" +
                    "Best regards,\n CKB Team";

            try {
                gmailSender.sendEmail(subject, body, studentToInvite.getEmail());
            } catch (IOException | MessagingException e) {
                throw new RuntimeException(e);
            }
        }).start();

        return ResponseEntity.ok().body("Student added to the team " + team.getName());
    }

    // mapped to "Create a new battle"
    // CHECKED BY @PONTIG
    @PostMapping("/battle/create")
    public ResponseEntity<String> createBattle(@ModelAttribute CreateBattleRequest createBattleRequest, HttpSession session) {
        User user = (User) session.getAttribute("user");
        boolean owner = false;

        Long tournamentId = createBattleRequest.getTournamentId();
        String battleName = createBattleRequest.getBattleName();
        Date registerDeadline = createBattleRequest.getRegisterDeadline();
        Date submissionDeadline = createBattleRequest.getSubmissionDeadline();
        String language = createBattleRequest.getLanguage();
        int minSize = createBattleRequest.getMinSize();
        int maxSize = createBattleRequest.getMaxSize();
        boolean manualEvaluation = createBattleRequest.isManualEvaluation();

        MultipartFile ckbProblem = createBattleRequest.getCkbProblem();
        MultipartFile buildScript = createBattleRequest.getBuildScript();
        MultipartFile test = createBattleRequest.getTest();

        String description = createBattleRequest.getDescription();
        boolean reliability = createBattleRequest.getReliability();
        boolean maintainability = createBattleRequest.getMaintainability();
        boolean security = createBattleRequest.getSecurity();

        if (user == null)
            // Check if user is not in session
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).body("Unauthorized - You are not logged in CKB");

        if (!user.isEdu())
            // Check if user is an Educator
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden - You do not have the necessary rights EDUERROZR");

        if (battleName.isEmpty() || battleName.isBlank() || language.isEmpty() || language.isBlank() /*|| description.isBlank() || description.isEmpty()*/)
            // Check if any string field is empty or blank
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request - Empty or Blank parameters");

        if (battleName.contains("-"))
            // Check if '-' char is used
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request - Character '-' is not allowed");

        for (Battle b : battleRepository.findAll()) {
            // Check if there is another battle with same name
            if (b.getName().equals(battleName))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request - A battle with this name already exists");
        }

        Tournament tournamentForBattle = tournamentRepository.getTournamentById(tournamentId);

        if (tournamentForBattle == null)
            // Check if tournament exists
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found - A tournament with id: " + tournamentId + " can not found");

        if (registerDeadline.before(new Date()) && submissionDeadline.before(new Date()))
            // Check if the deadlines are not in future
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request - Deadlines are not in future");

        if (registerDeadline.after(submissionDeadline))
            // Check if submission is not after registration
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request - Submission deadline is before registration deadline");

        if (!language.equals("Java") && !language.equals("Cpp") && !language.equals("Python") && !language.equals("C") && !language.equals("JavaScript"))
            // Check if language is not in the list of accepted one
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request - Language defined not supported");

        if (language.equals("C") || language.equals("Cpp"))
            // TODO: remove if we can build it in future
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request - We are sorry but it is not possible to build it now");

        if (minSize <= 0 || maxSize <= 0)
            // Check if boundaries defined are allowed
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request - Boundaries not defined");

        if (maxSize < minSize)
            // Check if max is lower than min
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request - MaxSize can not be lower than MinSize");

        if (!ckbProblem.getContentType().equalsIgnoreCase("application/pdf"))
            // Check if the file is a PDF
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request - " + ckbProblem.getOriginalFilename() + " is not a PDF");

        if (language.equals("Java")) {
            if (!buildScript.getContentType().equalsIgnoreCase("text/xml"))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request - " + buildScript.getOriginalFilename() + " is not a XML file");
            if (!test.getOriginalFilename().toLowerCase().endsWith(".java"))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request - " + buildScript.getOriginalFilename() + " is not a .java file");
        } else if (language.equals("Python")) {
            if (!buildScript.getContentType().equalsIgnoreCase("text/x-python"))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request - " + buildScript.getOriginalFilename() + " is not a PY file");
            if (!test.getOriginalFilename().toLowerCase().endsWith(".py"))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request - " + buildScript.getOriginalFilename() + " is not a .py file");
        } else {
            if (!buildScript.getContentType().equalsIgnoreCase("application/json"))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request - " + buildScript.getOriginalFilename() + " is not a JSON file");
            if (!test.getOriginalFilename().toLowerCase().endsWith(".js")) // .js
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request - " + buildScript.getOriginalFilename() + " is not a .js file");
        }

        Educator creatorBattle = educatorRepository.getEducatorDataById(user.getId());

        for (Tournament t : creatorBattle.getOwnedTournaments()) {
            // Check if the educator is an owner of the tournament
            if (t.getId().equals(tournamentId)) {
                owner = true;
                break;
            }
        }

        if (owner) {

            Battle newBattle = new Battle(
                    battleName,
                    new Date(),
                    registerDeadline,
                    submissionDeadline,
                    language,
                    manualEvaluation,
                    minSize, maxSize,
                    creatorBattle,
                    tournamentForBattle,
                    false,
                    description,
                    reliability,
                    maintainability,
                    security);
            battleRepository.save(newBattle);

            // Change file name with the battle id
            String battleId = String.valueOf(battleRepository.getBattleIdByName(newBattle.getName()));

            String newCkbProblem = "ProblemDescription.pdf";
            String newBuildScript;
            String newTest;

            switch (language) {
                case "Java" -> {
                    newBuildScript = "pom.xml";
                    newTest = "MainTest.java";
                }
                case "JavaScript" -> {
                    newBuildScript = "package.json";
                    newTest = "main.test.js";
                }
                case "Python" -> {
                    newBuildScript = "setup.py";
                    newTest = "main_test.py";
                }
                default -> {
                    newBuildScript = null;
                    newTest = null;
                }
            }

            // Obtain path to store the file
            Path absolutePath = Paths.get("fileStorage").toAbsolutePath();
            Path destinationCKBProblemPath = absolutePath.resolve("CKBProblem").resolve(battleId).resolve(newCkbProblem);
            Path destinationBuildScriptPath = absolutePath.resolve("BuildScript").resolve(battleId).resolve(newBuildScript);
            Path destinationTestPath = absolutePath.resolve("Test").resolve(battleId).resolve(newTest);
            try {
                // Ensure that destination directories exist, creating them if necessary
                Files.createDirectories(destinationCKBProblemPath.getParent());
                Files.createDirectories(destinationBuildScriptPath.getParent());
                Files.createDirectories(destinationTestPath.getParent());

                // Save file in the directory
                Files.copy(ckbProblem.getInputStream(), destinationCKBProblemPath, StandardCopyOption.REPLACE_EXISTING);
                Files.copy(buildScript.getInputStream(), destinationBuildScriptPath, StandardCopyOption.REPLACE_EXISTING);
                Files.copy(test.getInputStream(), destinationTestPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            new Thread(() -> {
                // Prepare Email to send
                GmailAPI gmailSender;
                try {
                    gmailSender = new GmailAPI();
                } catch (GeneralSecurityException | IOException e) {
                    throw new RuntimeException(e);
                }
                String subject = "NEW BATTLE CREATED " + battleName;

                // Send Email to each first student in battle
                for (Student s : newBattle.getTournament().getSubscribedStudents()) {
                    String bodyMsg = "Hi " + s.getFirstName() + ",\n\n" +
                            "as " + newBattle.getTournament().getName() + " member I want to inform you about an upcoming battle\n" +
                            "Educator " + user.getFirstName() + " has created the battle " + battleName + " and is waiting for you\n" +
                            "The registration deadline is on " + registerDeadline + ", subscribe before it expires\n" +
                            "You can find CKB Platform at the following link: http://localhost:8080/ckb_platform\n\n" +
                            "Best regards,\n CKB Team";

                    try {
                        gmailSender.sendEmail(subject, bodyMsg, s.getEmail());
                    } catch (IOException | MessagingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();

            new RegistrationThread(battleRepository, teamRepository, newBattle).start();
            new SubmissionThread(newBattle).start();

            return ResponseEntity.status(HttpStatus.OK).body(battleId);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden - You do not have the necessary rights NOOWNER");
    }

    // TODO: va specificata nel DD?
    @PostMapping("/battle/pulls")
    public void pullRequest(@RequestBody RepoPullRequest repoPullRequest) throws IOException, ParserConfigurationException, SAXException {
        String repository = repoPullRequest.getRepository();
        String pusher = repoPullRequest.getPusher();
        String teamName = repoPullRequest.getTeam();
        String repoName = repository.replace(pusher + "/", "").replace("-", " ");

        log.info("A push has been made by: " + pusher + " in team id: " + teamName + " for battle " + repoName);

        // Get battle from name
        Battle battle = battleRepository.getBattleByName(repoName);
        Team team = teamRepository.getTeamByName(teamName);

        if (team.getBattle().getId() != battle.getId()) {
            return;
        }

        if (battle != null && team != null) {
            // If the battle is closed no more pull are performed for it
            if (battle.getFinalSubmissionDeadline().before(new Date()))
                return;

            // Add score for last commit
            log.info("score = " + (battle.getFinalSubmissionDeadline().getTime() + " - " + new Date().getTime()) + " * 100 / " + (battle.getFinalSubmissionDeadline().getTime() + " - " + battle.getRegistrationDeadline().getTime()));
            long score = ((battle.getFinalSubmissionDeadline().getTime() - new Date().getTime()) * 100) / (battle.getFinalSubmissionDeadline().getTime() - battle.getRegistrationDeadline().getTime());
            log.info(" = " + score);
            team.setTimelinessScore((int) score);
            if (team.getRepo() == null)
                team.setRepo(repository);
            teamRepository.save(team);
            String repoPath = new GitHubAPI().pullRepository(battle, team, repoName, pusher);
            log.info(repoPath);

            // Add score for script tested
            String language = battle.getLanguage();
            String projectPath = repoPath + "/" + language + "Project";
            TestRepository build = new TestRepository();

            new Thread(() -> {
                if (language.equals("JavaScript")) {
                    build.buildAndTestRepo(battle, false, projectPath);
                    build.buildAndTestRepo(battle, true, projectPath);
                } else build.buildAndTestRepo(battle, false, projectPath);

                try {
                    if (language.equals("Java"))
                        team.setTestScore(build.getTestPassedJava(projectPath));
                    else if (language.equals("JavaScript")) {

                        team.setTestScore(build.getTestPassedJavaScript(projectPath));

                    } else {
                        team.setTestScore(build.getTestPassedPython(projectPath));
                    }
                } catch (IOException | ParserConfigurationException | SAXException e) {
                    throw new RuntimeException(e);
                }
                teamRepository.save(team);
            }).start();

            if (repoPath != null) {
                Analyzer analyzer = new Analyzer("CKBplatform-" + team.getId(), "CKBplatform-" + team.getId());

                int projectExists = analyzer.projectExists();

                if (projectExists == 0) {
                    //create the project on our static analysis tool
                    analyzer.createProjectSonarQube();
                    //create the webhook on our static analysis tool
                    analyzer.createWebHook();
                } else if (projectExists == -1) {
                    log.error("Error in the connection with the SonarQube server");
                    return;
                }
                //run the analysis from the command line using repoPath as source directory
                analyzer.runAnalysisSonarQube(battle.getLanguage(), repoPath);
            }
        } else
            log.error("Battle or team not found");
    }

    // mapped to "Manual evaluation partial"
    // CHECKED BY @PONTIG
    @PostMapping("/battle/manualEvaluation/partial")
    public void evaluateSingleCode(@RequestBody ListGroupsForManualRequest data, HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (!user.isEdu()) {
            throw new EducatorNotFoundException(user.getId());
        }
        Team team = teamRepository.findById(data.getTeam_id())
                .orElseThrow(() -> new TeamNotFoundException(data.getTeam_id()));
        team.setManualScore(data.getScore());
        teamRepository.save(team);

    }

    // mapped to "Manual evaluation completed"
    // CHECKED BY @PONTIG
    @PostMapping("/battle/manualEvaluation/final")
    public void endManualEvaluation(@RequestBody Long battle_id, HttpSession session) throws CannotCloseBattleException {
        User user = (User) session.getAttribute("user");
        if (!user.isEdu()) {
            throw new EducatorNotFoundException(user.getId());
        }
        Battle battle = battleRepository.findById(battle_id)
                .orElseThrow(() -> new BattleNotFoundException(battle_id));

        battle.getTeams().stream().filter(t -> t.getManualScore() == null).forEach(t -> {
            throw new CannotCloseBattleException(battle_id);
        });

        battle.setHasBeenEvaluated();

        battleRepository.save(battle);

        // Get the students in the battle
        List<Team> teamsSubscribed = battle.getTeams();
        List<Student> studentsToNotify = new ArrayList<>();
        for (Team t : teamsSubscribed)
            studentsToNotify.addAll(t.getStudents());

        new Thread(() -> {
            // Prepare Email to send
            GmailAPI gmailSender;
            try {
                gmailSender = new GmailAPI();
            } catch (GeneralSecurityException | IOException e) {
                throw new RuntimeException(e);
            }

            String subject = "CLOSE BATTLE " + battle.getName();

            // Send Email to each student in battle
            for (Student s : studentsToNotify) {
                String bodyMsg = "Hi " + s.getFirstName() + ",\n\n" +
                        "Battle " + battle.getName() + " has been closed\n" +
                        "You can now find the final ranking\n" +
                        "You can find CKB Platform at the following link: http://localhost:8080/ckb_platform\n\n" +
                        "Best regards,\n CKB Team";

                try {
                    gmailSender.sendEmail(subject, bodyMsg, s.getEmail());
                } catch (IOException | MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    // mapped to "Join a battle"
    // CHECKED BY @PONTIG
    @PostMapping("/battle/join")
    public ResponseEntity<String> joinBattle(@RequestBody JoinBattleRequest joinBattleRequest, HttpSession session) {
        User user = (User) session.getAttribute("user");

        String teamName = joinBattleRequest.getName();
        Long battleId = joinBattleRequest.getBattleId();
        List<String> studentEmails = joinBattleRequest.getStudentsEmail();

        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized - You are not logged in CKB");

        if (user.isEdu())
            // Check if user is an Educator
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden - You do not have the necessary rights");

        if (teamName.trim().isBlank() || teamName.isEmpty())
            // Check if team name is defined
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request - Team name is empty or blank");

        for (Team team : teamRepository.findAll()) {
            if (team.getName().equals(teamName)) {
                // Check if team name already in use
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request - Team name is already used");
            }
        }

        Student stu = studentRepository.getReferenceById(user.getId());
        Battle battleToJoin = battleRepository.getReferenceById(battleId);

        if (!stu.getTournaments().contains(battleToJoin.getTournament())) {
            // Check if the student is not subscribed to the tournament
            if (battleToJoin.getTournament().getEndDate() != null) {
                // Check if tournament is closed
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden - You can not join the battle because you are not in tournament");
            } else {
                // Subscribe student to tournament
                Tournament tour = battleToJoin.getTournament();
                tour.addStudent(stu);

                stu.addTournament(tour);
                studentRepository.save(stu);
            }
        }

        if (battleToJoin.getRegistrationDeadline().before(new Date()))
            // Check if the battle is not in registration period
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden - Battle registration deadline is closed");

        List<Student> students = new ArrayList<>();
        for (String email : studentEmails) {
            Student stuInvited = studentRepository.getStudentByEmail(email);
            if (stuInvited == null)
                // Check if student invited is in the db
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found - Student " + email + " not in the database");

            if (stuInvited.isEdu())
                // Check if student invited is in the db
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden - " + email + " is an educator");

            if (stuInvited == stu)
                // Check if student is not inviting itself
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden - You can not invite yourself");

            if (!students.contains(stuInvited))
                students.add(stuInvited);
        }

        if (students.size() + 1 > battleToJoin.getMaxStudents())
            // Check if the student boundaries are respected
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden - Too many students in the team");

        if (students.size() + 1 < battleToJoin.getMinStudents())
            // Check if the student boundaries are respected
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden - Too less students in the team");

        Battle battle = battleRepository.getReferenceById(battleId);

        // Save the team and the owner
        Student teamOwner = studentRepository.getReferenceById(user.getId());
        Team newTeam = new Team(teamName, battle);
        newTeam.addStudent(teamOwner);
        teamRepository.save(newTeam);

        new Thread(() -> {
            // Prepare Email to send
            GmailAPI gmailSender;

            try {
                gmailSender = new GmailAPI();
            } catch (IOException | GeneralSecurityException e) {
                throw new RuntimeException(e);
            }

            String subject = "JOIN TEAM " + teamName + " for battle " + battleToJoin.getName();

            if (!students.isEmpty()) {
                for (Student s : students) {
                    String body = "Hi " + s.getFirstName() + ",\n\n" +
                            teamOwner.getFirstName() + " invited you to join its team " + newTeam.getName() + "\n" +
                            "You can compete in battle " + battle.getName() + " together with the team members!\n\n" +
                            "To join your mates in this adventure click the link below before it is too late: " +
                            "https://localhost:8080/ckb_platform/team/" + newTeam.getId() + "/join/" + s.getId() + "\n" +
                            "The battle registration window will close on: " + battle.getRegistrationDeadline() + "\n\n" +
                            "Best regards,\n CKB Team";

                    try {
                        gmailSender.sendEmail(subject, body, s.getEmail());
                    } catch (IOException | MessagingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();

        return ResponseEntity.status(HttpStatus.OK).body("Team " + newTeam.getName() + " has been created");
    }

    // nice to have
    // CHECKED BY @PONTIG
    @GetMapping("/edu/noticed")
    List<Map<String, Object>> getNoticedBattlesEducator(HttpSession session) {
        //check if user passed is edu
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isEdu()) {
            throw new EducatorNotFoundException(user.getId());
        }
        Educator educator = educatorRepository.findById(user.getId())
                .orElseThrow(() -> new EducatorNotFoundException(user.getId()));

        List<Map<String, Object>> response = battleRepository
                .findAll()
                .stream()
                .filter(b -> b.getCreator().getId() == educator.getId() && b.getPhase() == 3)
                .map(b -> {
                    log.info("Battle: " + b.getPhase());
                    Map<String, Object> battleMap = new LinkedHashMap<>();
                    battleMap.put("battle_id", b.getId());
                    battleMap.put("battle_name", b.getName());
                    return battleMap;
                })
                .collect(Collectors.toList());

        return response;
    }
}