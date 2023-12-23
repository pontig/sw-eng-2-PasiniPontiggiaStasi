package ckb.platform;

import ckb.platform.entities.Student;
import ckb.platform.repositories.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // indicates that this class can be used by the Spring IoC container as a source of bean definitions
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean // indicates that a method produces a bean to be managed by the Spring container
    CommandLineRunner initDatabase(StudentRepository repository) {

        return args -> {
            log.info("Preloading " + repository.save(new Student("Bilbo","Baggins", "bilbo@gmail.com")));
            log.info("Preloading " + repository.save(new Student("Frodo" , "Baggins", "frodo@hotmail.com")));
        };
    }
}

