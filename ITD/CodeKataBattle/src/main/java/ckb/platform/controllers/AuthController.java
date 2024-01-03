package ckb.platform.controllers;

import ckb.platform.entities.Educator;
import ckb.platform.entities.Student;
import ckb.platform.entities.User;
import ckb.platform.repositories.EducatorRepository;
import ckb.platform.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class AuthController {
    @Autowired
    private StudentRepository stuRepository;
    @Autowired
    private EducatorRepository eduRepository;

    @PostMapping("/login")
    public ModelAndView login(@RequestParam String email, @RequestParam String password) {
        System.out.println("I'm a user " + email + " " + password);
        User user;

        user = stuRepository.findByEmailAndPassword(email, password);

        if (user != null) {
            System.out.println("I'm a STU");
            return new ModelAndView(new RedirectView("/indexSTU.html", true));
        } else {
            user = eduRepository.findByEmailAndPassword(email, password);
            if (user != null) {
                System.out.println("I'm an EDU");
                return new ModelAndView(new RedirectView("/indexEDU.html", true));
            }
            return new ModelAndView(new RedirectView("/index.html", true));
        }
    }

    @PostMapping("/register")
    public ModelAndView processRegistration(@RequestParam String name, @RequestParam String surname,
                                      @RequestParam String email, @RequestParam String uni,
                                      @RequestParam String role, @RequestParam String password,
                                      @RequestParam String password2, @RequestParam String terms) {

        if(!password.equals(password2))
            return new ModelAndView(new RedirectView("/index.html", true));


        if(role.equals("STU")){
            if(stuRepository.alreadyRegistered(email) != null) {
                //Errore account già esistente
                return new ModelAndView(new RedirectView("/index.html", true));
            }

            Student newStu = new Student();
            newStu.setEmail(email);
            newStu.setFirstName(name);
            newStu.setLastName(surname);
            newStu.setPassword(password);

            System.out.println("Sono uno STU - nome: " + name + " cognome: " + surname + " email: " + email + " password: " + password);

            stuRepository.save(newStu);

        } else if(role.equals("EDU")){
            if(eduRepository.alreadyRegistered(email) != null) {
                //Errore account già esistente
                return new ModelAndView(new RedirectView("/index.html", true));
            }

            Educator newEdu = new Educator();
            newEdu.setEmail(email);
            newEdu.setFirstName(name);
            newEdu.setLastName(surname);
            newEdu.setPassword(password);

            System.out.println("Sono un EDU - nome: " + name + " cognome: " + surname + " email: " + email + " password: " + password);

            eduRepository.save(newEdu);
        }

        return new ModelAndView(new RedirectView("/index.html", true));

    }
}
