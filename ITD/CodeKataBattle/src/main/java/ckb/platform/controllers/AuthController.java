package ckb.platform.controllers;

import ckb.platform.entities.Educator;
import ckb.platform.entities.Student;
import ckb.platform.entities.User;
import ckb.platform.repositories.EducatorRepository;
import ckb.platform.repositories.StudentRepository;
import ckb.platform.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private EducatorRepository educatorRepository;

    @PostMapping("/register")
    public ResponseEntity<String> processRegistration(@RequestParam String name, @RequestParam String surname,
                                                      @RequestParam String email, @RequestParam String uni,
                                                      @RequestParam String role, @RequestParam String password,
                                                      @RequestParam String password2, @RequestParam String terms, HttpSession session) {

        User user = (User) session.getAttribute("user");
        if(user != null){
            System.out.println("Register: " + user);

            // User already in session
            if(user.isEdu())
                return ResponseEntity.status(HttpStatus.FOUND).body("indexEDU");
            else
                return ResponseEntity.status(HttpStatus.FOUND).body("indexSTU");
        } else {
            if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || uni.isEmpty() || role.isEmpty() || password.isEmpty() || password2.isEmpty() || terms.isEmpty() ||
                name.isBlank() || surname.isBlank() || email.isBlank() || uni.isBlank() || role.isBlank() || password.isBlank() || password2.isBlank() || terms.isBlank()) {
                // Check if any field is empty
                // TODO: terms danno problemi se si toglie required
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request - Empty or Blank parameters");
            }

            if (!email.contains("@")) {
                // Check if the mail is written with a @
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request - E-mail incorrect format");
            }

            if (!password.equals(password2)) {
                // Check if the password match the second
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request - Password Mismatch");
            }

            if(terms.equals("off")){
                // Check if terms are accepted
                // TODO: da controllare una volta sistemato il problema di required
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request - Accepts terms and conditions");
            }

            if (userRepository.alreadyRegistered(email) != null) {
                // Check if account already in the database
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict - Account already exist");
            }

            if (role.equals("STU")) {
                // Save user information as a Student
                Student newStu = new Student(name, surname, email, password, uni);
                studentRepository.save(newStu);
            } else if (role.equals("EDU")) {
                // Save user information as an Educator
                Educator newEdu = new Educator(name, surname, email, password, uni);
                educatorRepository.save(newEdu);
            } else {
                // Check if role type exist
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request - Role do not exist");
            }
            return ResponseEntity.status(HttpStatus.OK).body("login");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        User user = (User) session.getAttribute("user");

        if(user != null){
            System.out.println("Login: " + user);

            // User already in session
            if(user.isEdu())
                return ResponseEntity.status(HttpStatus.FOUND).body("indexEDU");
            else
                return ResponseEntity.status(HttpStatus.FOUND).body("indexSTU");
        } else {
            if (email.isEmpty() || password.isEmpty() || email.isBlank() || password.isBlank()) {
                // Check if any field is empty
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request - Empty or Blank parameters");
            }

            if (!email.contains("@")) {
                // Check if the mail is written with a @
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request - E-mail incorrect format");
            }

            user = userRepository.findUserByEmailAndPassword(email, password);

            if (user != null) {
                session.setAttribute("user", user);
                if (!user.isEdu())
                    return ResponseEntity.status(HttpStatus.OK).body("indexSTU");
                else
                    return ResponseEntity.status(HttpStatus.OK).body("indexEDU");

            } else {
                // User not in the DB
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error - user does not exist in the DB");
            }
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session){
        User user = (User) session.getAttribute("user");
        if(user != null){
            session.invalidate();
            return ResponseEntity.status(HttpStatus.OK).body("Successfully logged out");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User already logged out");
        }

    }
}
