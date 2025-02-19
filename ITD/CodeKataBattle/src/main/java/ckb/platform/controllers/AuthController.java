package ckb.platform.controllers;

import ckb.platform.entities.Educator;
import ckb.platform.entities.Student;
import ckb.platform.entities.User;
import ckb.platform.formParser.LoginRequest;
import ckb.platform.formParser.RegisterRequest;
import ckb.platform.repositories.EducatorRepository;
import ckb.platform.repositories.StudentRepository;
import ckb.platform.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class AuthController {
    public static final String INDEX_EDU_HTML = "indexEDU.html";
    public static final String INDEX_STU_HTML = "indexSTU.html";
    public static final String INDEX_HTML = "index.html";
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private EducatorRepository educatorRepository;

    // CHECKED BY @PONTIG
    @PostMapping("/register")
    public ResponseEntity<String> processRegistration(@RequestBody RegisterRequest registerRequest, HttpSession session) {
        if (registerRequest.getName().isEmpty() || registerRequest.getSurname().isEmpty() || registerRequest.getEmail().isEmpty() || registerRequest.getUni().isEmpty() || registerRequest.getRole().isEmpty() || registerRequest.getPassword().isEmpty() || registerRequest.getPassword2().isEmpty() || !registerRequest.isTerms() ||
            registerRequest.getName().isBlank() || registerRequest.getSurname().isBlank() || registerRequest.getEmail().isBlank() || registerRequest.getUni().isBlank() || registerRequest.getRole().isBlank() || registerRequest.getPassword().isBlank() || registerRequest.getPassword2().isBlank()) {
            // Check if any field is empty
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request - Empty or Blank parameters");
        }

        if (!registerRequest.getEmail().contains("@")) {
            // Check if the mail is written with a @
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request - E-mail incorrect format");
        }

        if (!registerRequest.getPassword().equals(registerRequest.getPassword2())) {
            // Check if the password match the second
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request - Password Mismatch");
        }

        if (userRepository.alreadyRegistered(registerRequest.getEmail()) != null) {
            // Check if account already in the database
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict - Account already exist");
        }

        if (registerRequest.getRole().equals("STU")) {
            // Save user information as a Student
            Student newStu = new Student(registerRequest.getName(), registerRequest.getSurname(), registerRequest.getEmail(), registerRequest.getPassword(), registerRequest.getUni());
            studentRepository.save(newStu);
        } else if (registerRequest.getRole().equals("EDU")) {
            // Save user information as an Educator
            Educator newEdu = new Educator(registerRequest.getName(), registerRequest.getSurname(), registerRequest.getEmail(), registerRequest.getPassword(), registerRequest.getUni());
            educatorRepository.save(newEdu);
        } else {
            // Check if role type exist
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request - Role do not exist");
        }
        return ResponseEntity.status(HttpStatus.OK).body("login");
    }

    // CHECKED BY @PONTIG
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        if (email.isEmpty() || password.isEmpty() || email.isBlank() || password.isBlank()) {
            // Check if any field is empty
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request - Empty or Blank parameters");
        }

        if (!email.contains("@")) {
            // Check if the mail is written with a @
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request - E-mail incorrect format");
        }

        User user = userRepository.findUserByEmailAndPassword(email, password);

        if (user != null) {
            session.setAttribute("user", user);
            String res = "{";

            res += "\"name\": \"" + user.getFirstName() + "\",";
            res += "\"surname\": \"" + user.getLastName() + "\",";
            res += "\"role\": \"" + (user.isEdu() ? "EDU" : "STU") + "\",";
            res += "\"id\": \"" + user.getId() + "\"";

            res += "}";

            return ResponseEntity.status(HttpStatus.OK).body(res);
        } else {
            // User not in the DB
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error - user does not exist in the DB");
        }

    }

    // CHECKED BY @PONTIG
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session){
        User user = (User) session.getAttribute("user");
        if(user != null){
            session.invalidate();
            return ResponseEntity.status(HttpStatus.OK).body("Successfully logged out");
        } else {
            // User
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized - User already logged out");
        }
    }

    @GetMapping("/")
    public RedirectView getPlatform(HttpSession session){
        User user = (User) session.getAttribute("user");
        if(user != null){
            // User already in session
            if(user.isEdu())
                return new RedirectView(INDEX_EDU_HTML);
            else
                return new RedirectView(INDEX_STU_HTML);
        }
        return new RedirectView(INDEX_HTML);
    }

    // If a user try to make a get to the /login endpoint, it is redirected
    // CHECKED BY @PONTIG
    @GetMapping("/login")
    public RedirectView getLogin(HttpSession session){
        User user = (User) session.getAttribute("user");
        if(user != null){
            // User already in session
            if(user.isEdu())
                return new RedirectView(INDEX_EDU_HTML);
            else
                return new RedirectView(INDEX_STU_HTML);
        }
        return new RedirectView(INDEX_HTML);
    }

    // If a user try to make a get to the /register endpoint, it is redirected
    // CHECKED BY @PONTIG
    @GetMapping("/register")
    public RedirectView getRegister(HttpSession session){
        User user = (User) session.getAttribute("user");
        if(user != null){
            // User already in session
            if(user.isEdu())
                return new RedirectView(INDEX_EDU_HTML);
            else
                return new RedirectView(INDEX_STU_HTML);
        }
        return new RedirectView(INDEX_HTML);
    }

    // If a user try to make a get to the /logout endpoint, it is redirected
    // CHECKED BY @PONTIG
    @GetMapping("/logout")
    public RedirectView getLogout(HttpSession session){
        User user = (User) session.getAttribute("user");
        if(user != null){
            session.invalidate();
        }
        return new RedirectView(INDEX_HTML);
    }
}
