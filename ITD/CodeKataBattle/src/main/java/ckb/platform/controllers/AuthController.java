package ckb.platform.controllers;

import ckb.platform.entities.Educator;
import ckb.platform.entities.Student;
import ckb.platform.entities.User;
import ckb.platform.repositories.EducatorRepository;
import ckb.platform.repositories.StudentRepository;
import ckb.platform.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private EducatorRepository educatorRepository;

    @PostMapping("/login")
    public ModelAndView login(@RequestParam String email, @RequestParam String password, HttpSession session) {
        System.out.println("I'm a user " + email + " " + password);
        User user;

        user = userRepository.findByEmailAndPassword(email, password);

        if (user!=null){
            session.setAttribute("user", user);
            if (!user.isEdu())
                return new ModelAndView(new RedirectView("/login/indexSTU.html", true));
            else
                return new ModelAndView(new RedirectView("/login/indexEDU.html", true));
        }else {
            return new ModelAndView(new RedirectView("/index.html", true));
        }
    }

    //Only for testing
    @GetMapping("/profile")
    public ModelAndView profile(HttpSession session) {
        // recupera l'utente dalla sessione
        User user = (User) session.getAttribute("user");
        System.out.println("Dati - Id: " + user.getId() + " Mail: " + user.getEmail());
        return new ModelAndView(new RedirectView("/index.html", true));
    }

    @GetMapping("/login/indexSTU.html")
    public ModelAndView indexSTU(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView(new RedirectView("/index.html", true));
        }
        return new ModelAndView(new RedirectView("/indexSTU.html", true));
    }

    @PostMapping("/register")
    public ModelAndView processRegistration(@RequestParam String name, @RequestParam String surname,
                                            @RequestParam String email, @RequestParam String uni,
                                            @RequestParam String role, @RequestParam String password,
                                            @RequestParam String password2, @RequestParam String terms) {

        if(!password.equals(password2))
            return new ModelAndView(new RedirectView("/index.html", true));

        if(userRepository.alreadyRegistered(email) != null) {
            //Errore account già esistente
            return new ModelAndView(new RedirectView("/index.html", true));
        } else {
            if (role.equals("STU")) {
                Student newStu = new Student(name, surname, email, password, uni);
                studentRepository.save(newStu);
            } else if (role.equals("EDU")) {
                Educator newEdu = new Educator(name, surname, email, password, uni);
                educatorRepository.save(newEdu);
            }
        }

        return new ModelAndView(new RedirectView("/index.html", true));

    }
}
