package ckb.platform;

import ckb.platform.entities.Student;
import ckb.platform.entities.Educator;
import ckb.platform.repositories.EducatorRepository;
import ckb.platform.repositories.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // indicates that this class can be used by the Spring IoC container as a source of bean definitions
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
        // indicates that a method produces a bean to be managed by the Spring container
    CommandLineRunner initDatabase1STU(StudentRepository repository) {

        return args -> {

            // Some students
            log.info("Preloading " + repository.save(new Student("Bilbo", "Baggins", "bilbo@gmail.com")));
            log.info("Preloading " + repository.save(new Student("Frodo", "Baggins", "frodo@hotmail.com")));
            log.info("Preloading " + repository.save(new Student("Samwise", "Gamgee", "samwiseGmg@hotmail.com")));
            log.info("Preloading " + repository.save(new Student("Meriadoc", "Brandybuck", "meridaBrandy69@hotmail.com")));

        };
    }

   @Bean
   CommandLineRunner initDatabase2EDU(EducatorRepository repository) {
       return args -> {
           // A couple of educators
           log.info("Preloading " + repository.save(new Educator("Gandalf", "The Grey", "gandalfTheBlack@hotmail.com")));
           log.info("Preloading " + repository.save(new Educator("Saruhan", "The White", "evenMoreWhite@hotmail.com")));

       };
   }
}

