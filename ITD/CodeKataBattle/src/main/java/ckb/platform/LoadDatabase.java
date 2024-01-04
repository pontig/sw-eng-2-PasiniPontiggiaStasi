package ckb.platform;

import ckb.platform.entities.*;
import ckb.platform.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.text.SimpleDateFormat;
import java.util.Date;

@Configuration // indicates that this class can be used by the Spring IoC container as a source of bean definitions
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase1(StudentRepository stuRep, EducatorRepository eduRep, TournamentRepository tourRep, BattleRepository batRep, TeamRepository teamRep) {

        /*
        * Ancora non funziona per via degli id, comincio a pushare
        * */

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return args -> {
            log.info("Preloading " + stuRep.save(new Student(519, "Woody", "Cowboy", "woody.cowboy@email.com", "nervi", "Western Toy College")));
            log.info("Preloading " + stuRep.save(new Student(518, "Buzz", "Lightyear", "buzz.lightyear@email.com", "nervi", "Star Command University")));
            log.info("Preloading " + stuRep.save(new Student(517, "Jasmine", "Princess", "jasmine.princess@email.com", "nervi", "Agrabah Academy")));
            log.info("Preloading " + stuRep.save(new Student(516, "Aladdin", "Prince", "aladdin.prince@email.com", "nervi", "Agrabah Academy")));
            log.info("Preloading " + stuRep.save(new Student(515, "Nala", "Lioness", "nala.lioness@email.com", "nervi", "Pride Rock University")));
            log.info("Preloading " + stuRep.save(new Student(514, "Simba", "Lion", "simba.lion@email.com", "nervi", "Pride Rock University")));
            log.info("Preloading " + stuRep.save(new Student(513, "Ariel", "Mermaid", "ariel.mermaid@email.com", "nervi", "Undersea Institute")));
            log.info("Preloading " + stuRep.save(new Student(512, "Cinderella", "Princess", "cinderella.princess@email.com", "nervi", "Royal Kingdom University")));
            log.info("Preloading " + stuRep.save(new Student(511, "Pluto", "Dog", "pluto.dog@email.com", "nervi", "ToonTown College")));
            log.info("Preloading " + stuRep.save(new Student(510, "Goofy", "Goof", "goofy.goof@email.com", "nervi", "ToonTown College")));
            log.info("Preloading " + stuRep.save(new Student(509, "Mickey", "Mouse", "mickey.mouse@email.com", "nervi", "ToonTown College")));
            log.info("Preloading " + stuRep.save(new Student(508, "Minnie", "Mouse", "minnie.mouse@email.com", "nervi", "ToonTown College")));
            log.info("Preloading " + stuRep.save(new Student(507, "Louie", "Duck", "louie@coldmail.com", "nervi", "IULM University")));
            log.info("Preloading " + stuRep.save(new Student(506, "Dewey", "Duck", "dewey@coldmail.com", "nervi", "IULM University")));
            log.info("Preloading " + stuRep.save(new Student(505, "Huey", "Duck", "huey@coldmail.com", "nervi", "IULM University")));

            log.info("Preloading " + eduRep.save(new Educator(504, "Dale", "Squirrel", "Mouseton Institute of Technology", "chipndalebutjustdale@mail.mit.com", "nervi")));
            log.info("Preloading " + eduRep.save(new Educator(503, "Chip", "Squirrel", "Mouseton Institute of Technology", "chipndalebutjustchip@mail.mit.com", "nervi")));
            log.info("Preloading " + eduRep.save(new Educator(502, "Donald", "Duck", "Duckburg University", "dduck@gmail.com", "nervi")));
            log.info("Preloading " + eduRep.save(new Educator(501, "Scrooge", "McDuck", "Duckburg University", "scrooge@coldmail.com", "nervi")));

            log.info("Preloading = " + tourRep.save(new Tournament(218, "IoT Innovators Clash", sdf.parse("2023-06-15"), sdf.parse("2023-07-15"), eduRep.getReferenceById(502L))));
            log.info("Preloading = " + tourRep.save(new Tournament(217, "CodeMasters Championship", sdf.parse("2022-04-05"), sdf.parse(null), eduRep.getReferenceById(503L))));
            log.info("Preloading = " + tourRep.save(new Tournament(216, "Blockchain Battles", sdf.parse("2023-01-10"), sdf.parse("2023-02-10"), eduRep.getReferenceById(503L))));
            log.info("Preloading = " + tourRep.save(new Tournament(215, "CodeInnovate Invitational", sdf.parse("2020-04-25"), sdf.parse(null), eduRep.getReferenceById(503L))));
            log.info("Preloading = " + tourRep.save(new Tournament(214, "GameDev Grand Prix", sdf.parse("2022-07-20"), sdf.parse(null), eduRep.getReferenceById(501L))));
            log.info("Preloading = " + tourRep.save(new Tournament(213, "MobileMasters Marathon", sdf.parse("2021-12-05"), sdf.parse("2022-01-05"), eduRep.getReferenceById(501L))));
            log.info("Preloading = " + tourRep.save(new Tournament(212, "CodeGurus Gala", sdf.parse("2023-02-15"), sdf.parse("2023-03-15"), eduRep.getReferenceById(501L))));
            log.info("Preloading = " + tourRep.save(new Tournament(211, "CryptoCode Clash", sdf.parse("2020-11-01"), sdf.parse(null), eduRep.getReferenceById(501L))));
            log.info("Preloading = " + tourRep.save(new Tournament(210, "Robot Rumble", sdf.parse("2021-08-10"), sdf.parse("2021-09-10"), eduRep.getReferenceById(501L))));
            log.info("Preloading = " + tourRep.save(new Tournament(209, "AI Arena", sdf.parse("2022-09-15"), sdf.parse("2022-10-15"), eduRep.getReferenceById(504L))));
            log.info("Preloading = " + tourRep.save(new Tournament(208, "CodeSprint Showdown", sdf.parse("2023-04-01"), sdf.parse("2023-04-30"), eduRep.getReferenceById(504L))));
            log.info("Preloading = " + tourRep.save(new Tournament(207, "WebWizards Challenge", sdf.parse("2022-02-20"), sdf.parse(null), eduRep.getReferenceById(504L))));
            log.info("Preloading = " + tourRep.save(new Tournament(206, "AlgoMasters Showcase", sdf.parse("2021-05-10"), sdf.parse("2021-06-10"), eduRep.getReferenceById(504L))));
            log.info("Preloading = " + tourRep.save(new Tournament(205, "CodeCraft Cup", sdf.parse("2020-06-01"), sdf.parse(null), eduRep.getReferenceById(502L))));
            log.info("Preloading = " + tourRep.save(new Tournament(204, "DataQuest Challenge", sdf.parse("2020-03-15"), sdf.parse("2020-04-15"), eduRep.getReferenceById(502L))));
            log.info("Preloading = " + tourRep.save(new Tournament(203, "Hackaton Fiesta", sdf.parse("2023-12-30"), sdf.parse(null), eduRep.getReferenceById(501L))));
            log.info("Preloading = " + tourRep.save(new Tournament(202, "BugMaster League", sdf.parse("2019-01-01"), sdf.parse("2019-02-28"), eduRep.getReferenceById(501L))));
            log.info("Preloading = " + tourRep.save(new Tournament(201, "Codebash Championship", sdf.parse("2019-01-01"), sdf.parse("2019-01-31"), eduRep.getReferenceById(501L))));

            log.info("Preloading = " + batRep.save(new Battle(5904, "The dawn of a new code",    sdf.parse("2023-12-15"), sdf.parse("2023-12-20"), sdf.parse("2023-12-25"), "Python", false, 1, 1,   eduRep.getReferenceById(501L), tourRep.getReferenceById(201L), false)));
            log.info("Preloading = " + batRep.save(new Battle(5903, "CodeCraft Clash",           sdf.parse("2023-12-15"), sdf.parse("2023-12-20"), sdf.parse("2023-12-25"), "C++", true, 3, 5,       eduRep.getReferenceById(501L), tourRep.getReferenceById(201L), false)));
            log.info("Preloading = " + batRep.save(new Battle(5902, "Whitespace Warriors",       sdf.parse("2023-12-15"), sdf.parse("2024-02-10"), sdf.parse("2024-02-15"), "C", false, 2, 6,        eduRep.getReferenceById(501L), tourRep.getReferenceById(201L), false)));
            log.info("Preloading = " + batRep.save(new Battle(5901, "Opening battle",            sdf.parse("2024-02-10"), sdf.parse("2024-02-15"), sdf.parse("2024-02-20"), "Java", false, 1, 2,     eduRep.getReferenceById(501L), tourRep.getReferenceById(201L), false)));

            log.info("Preloading = " + teamRep.save(new Team(101, "Team 01", batRep.getReferenceById(5901L))));
            teamRep.getReferenceById(101L).addStudent(stuRep.getReferenceById(519L));
            log.info("...and added a player");
            log.info("Preloading = " + teamRep.save(new Team(102, "Team 02", batRep.getReferenceById(5901L))));
            teamRep.getReferenceById(102L).addStudent(stuRep.getReferenceById(518L));
            log.info("...and added a player");
            log.info("Preloading = " + teamRep.save(new Team(103, "Team 03", batRep.getReferenceById(5901L))));
            teamRep.getReferenceById(103L).addStudent(stuRep.getReferenceById(517L));
            log.info("...and added a player");
            log.info("Preloading = " + teamRep.save(new Team(104, "Team 04", batRep.getReferenceById(5901L))));
            teamRep.getReferenceById(104L).addStudent(stuRep.getReferenceById(516L));
            log.info("...and added a player");

            log.info("Preloading = " + teamRep.save(new Team(105, "Team 05", batRep.getReferenceById(5902L))));
            teamRep.getReferenceById(105L).addStudent(stuRep.getReferenceById(519L));
            log.info("...and added a player");
            teamRep.getReferenceById(105L).addStudent(stuRep.getReferenceById(517L));
            log.info("...and added a player");
            teamRep.getReferenceById(105L).addStudent(stuRep.getReferenceById(516L));
            log.info("...and added a player");
            log.info("Preloading = " + teamRep.save(new Team(106, "Team 06", batRep.getReferenceById(5902L))));
            teamRep.getReferenceById(106L).addStudent(stuRep.getReferenceById(518L));
            log.info("...and added a player");
            teamRep.getReferenceById(106L).addStudent(stuRep.getReferenceById(513L));
            log.info("...and added a player");
            teamRep.getReferenceById(106L).addStudent(stuRep.getReferenceById(512L));
            log.info("...and added a player");
            teamRep.getReferenceById(106L).addStudent(stuRep.getReferenceById(511L));
            log.info("...and added a player");
            teamRep.getReferenceById(106L).addStudent(stuRep.getReferenceById(510L));
            log.info("...and added a player");
            log.info("Preloading = " + teamRep.save(new Team(107, "Team 07", batRep.getReferenceById(5902L))));
            teamRep.getReferenceById(107L).addStudent(stuRep.getReferenceById(509L));
            log.info("...and added a player");
            teamRep.getReferenceById(107L).addStudent(stuRep.getReferenceById(508L));
            log.info("...and added a player");
            teamRep.getReferenceById(107L).addStudent(stuRep.getReferenceById(507L));
            log.info("...and added a player");
            teamRep.getReferenceById(107L).addStudent(stuRep.getReferenceById(506L));
            log.info("...and added a player");

            log.info("Preloading = " + teamRep.save(new Team(108, "Team 08", batRep.getReferenceById(5903L))));
            teamRep.getReferenceById(107L).addStudent(stuRep.getReferenceById(506L));
            log.info("...and added a player");
            teamRep.getReferenceById(107L).addStudent(stuRep.getReferenceById(508L));
            log.info("...and added a player");
            log.info("Preloading = " + teamRep.save(new Team(109, "Team 09", batRep.getReferenceById(5903L))));
            teamRep.getReferenceById(107L).addStudent(stuRep.getReferenceById(507L));
            log.info("...and added a player");
            teamRep.getReferenceById(107L).addStudent(stuRep.getReferenceById(509L));
            log.info("...and added a player");
            teamRep.getReferenceById(107L).addStudent(stuRep.getReferenceById(511L));
            log.info("...and added a player");
            teamRep.getReferenceById(107L).addStudent(stuRep.getReferenceById(512L));
            log.info("...and added a player");
            log.info("Preloading = " + teamRep.save(new Team(110, "Team 10", batRep.getReferenceById(5903L))));
            teamRep.getReferenceById(107L).addStudent(stuRep.getReferenceById(513L));
            log.info("...and added a player");
            teamRep.getReferenceById(107L).addStudent(stuRep.getReferenceById(514L));
            log.info("...and added a player");
            log.info("Preloading = " + teamRep.save(new Team(111, "Team 11", batRep.getReferenceById(5903L))));
            teamRep.getReferenceById(107L).addStudent(stuRep.getReferenceById(515L));
            log.info("...and added a player");
            teamRep.getReferenceById(107L).addStudent(stuRep.getReferenceById(516L));
            log.info("...and added a player");
            log.info("Preloading = " + teamRep.save(new Team(112, "Team 12", batRep.getReferenceById(5903L))));
            teamRep.getReferenceById(107L).addStudent(stuRep.getReferenceById(517L));
            log.info("...and added a player");
            teamRep.getReferenceById(107L).addStudent(stuRep.getReferenceById(518L));
            log.info("...and added a player");
            log.info("Preloading = " + teamRep.save(new Team(113, "Team 13", batRep.getReferenceById(5903L))));
            teamRep.getReferenceById(107L).addStudent(stuRep.getReferenceById(519L));
            log.info("...and added a player");
            teamRep.getReferenceById(107L).addStudent(stuRep.getReferenceById(505L));
            log.info("...and added a player");

            log.info("Preloading = " + teamRep.save(new Team(114, "Team 14", batRep.getReferenceById(5904L))));
            teamRep.getReferenceById(107L).addStudent(stuRep.getReferenceById(505L));
            log.info("...and added a player");
            log.info("Preloading = " + teamRep.save(new Team(115, "Team 15", batRep.getReferenceById(5904L))));
            teamRep.getReferenceById(107L).addStudent(stuRep.getReferenceById(506L));
            log.info("...and added a player");
            log.info("Preloading = " + teamRep.save(new Team(116, "Team 16", batRep.getReferenceById(5904L))));
            teamRep.getReferenceById(107L).addStudent(stuRep.getReferenceById(507L));
            log.info("...and added a player");
            log.info("Preloading = " + teamRep.save(new Team(117, "Team 17", batRep.getReferenceById(5904L))));
            teamRep.getReferenceById(107L).addStudent(stuRep.getReferenceById(508L));
            log.info("...and added a player");
            log.info("Preloading = " + teamRep.save(new Team(118, "Team 18", batRep.getReferenceById(5904L))));
            teamRep.getReferenceById(107L).addStudent(stuRep.getReferenceById(509L));
            log.info("...and added a player");
            log.info("Preloading = " + teamRep.save(new Team(119, "Team 19", batRep.getReferenceById(5904L))));
            teamRep.getReferenceById(107L).addStudent(stuRep.getReferenceById(510L));
            log.info("...and added a player");
            log.info("Preloading = " + teamRep.save(new Team(120, "Team 20", batRep.getReferenceById(5904L))));
            teamRep.getReferenceById(107L).addStudent(stuRep.getReferenceById(511L));
            log.info("...and added a player");
        };
    }


}