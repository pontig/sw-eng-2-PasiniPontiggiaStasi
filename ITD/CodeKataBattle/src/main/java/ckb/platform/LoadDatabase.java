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
import java.util.ArrayList;
import java.util.Date;

@Configuration // indicates that this class can be used by the Spring IoC container as a source of bean definitions
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    /*@Bean
    CommandLineRunner initDatabase1(StudentRepository stuRep, EducatorRepository eduRep, TournamentRepository tourRep, BattleRepository batRep, TeamRepository teamRep) {


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return args -> {

            ArrayList<Student> students = new ArrayList<>();
            ArrayList<Educator> educators = new ArrayList<>();
            ArrayList<Tournament> tournaments = new ArrayList<>();
            ArrayList<Battle> battles = new ArrayList<>();
            ArrayList<Team> teams = new ArrayList<>();

            students.add(new Student("Woody", "Cowboy", "woody.cowboy@email.com", "nervi", "Western Toy College"));
            students.add(new Student("Buzz", "Lightyear", "buzz.lightyear@email.com", "nervi", "Star Command University"));
            students.add(new Student("Jasmine", "Princess", "jasmine.princess@email.com", "nervi", "Agrabah Academy"));
            students.add(new Student("Aladdin", "Prince", "aladdin.prince@email.com", "nervi", "Agrabah Academy"));
            students.add(new Student("Nala", "Lioness", "nala.lioness@email.com", "nervi", "Pride Rock University"));
            students.add(new Student("Simba", "Lion", "simba.lion@email.com", "nervi", "Pride Rock University"));
            students.add(new Student("Ariel", "Mermaid", "ariel.mermaid@email.com", "nervi", "Undersea Institute"));
            students.add(new Student("Cinderella", "Princess", "cinderella.princess@email.com", "nervi", "Royal Kingdom University"));
            students.add(new Student("Pluto", "Dog", "pluto.dog@email.com", "nervi", "ToonTown College"));
            students.add(new Student("Goofy", "Goof", "goofy.goof@email.com", "nervi", "ToonTown College"));
            students.add(new Student("Mickey", "Mouse", "mickey.mouse@email.com", "nervi", "ToonTown College"));
            students.add(new Student("Minnie", "Mouse", "minnie.mouse@email.com", "nervi", "ToonTown College"));
            students.add(new Student("Louie", "Duck", "louie@coldmail.com", "nervi", "IULM University"));
            students.add(new Student("Dewey", "Duck", "dewey@coldmail.com", "nervi", "IULM University"));
            students.add(new Student("Huey", "Duck", "huey@coldmail.com", "nervi", "IULM University"));

            students.forEach(student -> log.info("Preloading " + stuRep.save(student)));

            educators.add(new Educator("Dale", "Squirrel", "chipndalebutjustdale@mail.mit.com", "nervi", "Mouseton Institute of Technology"));
            educators.add(new Educator("Chip", "Squirrel", "chipndalebutjustchip@mail.mit.com", "nervi", "Mouseton Institute of Technology"));
            educators.add(new Educator("Donald", "Duck", "dduck@gmail.com", "nervi", "Duckburg University"));
            educators.add(new Educator("Scrooge", "McDuck", "scrooge@coldmail.com", "nervi", "Duckburg University"));

            educators.forEach(educator -> log.info("Preloading " + eduRep.save(educator)));

            tournaments.add(new Tournament("IoT Innovators Clash", sdf.parse("2024-06-15"), null, educators.get(2 - 1)));
            tournaments.add(new Tournament("CodeMasters Championship", sdf.parse("2024-04-05"), null, educators.get(3 - 1)));
            tournaments.add(new Tournament("Blockchain Battles", sdf.parse("2023-01-10"), sdf.parse("2023-02-10"), educators.get(3 - 1)));
            tournaments.add(new Tournament("CodeInnovate Invitational", sdf.parse("2024-04-25"), null, educators.get(3 - 1)));
            tournaments.add(new Tournament("GameDev Grand Prix", sdf.parse("2022-07-20"), null, educators.get(1 - 1)));
            tournaments.add(new Tournament("MobileMasters Marathon", sdf.parse("2021-12-05"), sdf.parse("2022-01-05"), educators.get(1 - 1)));
            tournaments.add(new Tournament("CodeGurus Gala", sdf.parse("2023-02-15"), sdf.parse("2023-03-15"), educators.get(1 - 1)));
            tournaments.add(new Tournament("CryptoCode Clash", sdf.parse("2020-11-01"), null, educators.get(1 - 1)));
            tournaments.add(new Tournament("Robot Rumble", sdf.parse("2021-08-10"), sdf.parse("2021-09-10"), educators.get(1 - 1)));
            tournaments.add(new Tournament("AI Arena", sdf.parse("2022-09-15"), sdf.parse("2022-10-15"), educators.get(4 - 1)));
            tournaments.add(new Tournament("CodeSprint Showdown", sdf.parse("2023-04-01"), sdf.parse("2023-04-30"), educators.get(4 - 1)));
            tournaments.add(new Tournament("WebWizards Challenge", sdf.parse("2024-02-20"), null, educators.get(4 - 1)));
            tournaments.add(new Tournament("AlgoMasters Showcase", sdf.parse("2022-05-10"), sdf.parse("2021-06-10"), educators.get(4 - 1)));
            tournaments.add(new Tournament("CodeCraft Cup", sdf.parse("2020-06-01"), null, educators.get(2 - 1)));
            tournaments.add(new Tournament("DataQuest Challenge", sdf.parse("2020-03-15"), sdf.parse("2020-04-15"), educators.get(2 - 1)));
            tournaments.add(new Tournament("Hackaton Fiesta", sdf.parse("2024-12-30"), null, educators.get(1 - 1)));
            tournaments.add(new Tournament("BugMaster League", sdf.parse("2019-01-01"), sdf.parse("2019-02-28"), educators.get(1 - 1)));
            tournaments.add(new Tournament("Codebash Championship", sdf.parse("2019-01-01"), sdf.parse("2019-01-31"), educators.get(1 - 1)));

            tournaments.forEach(tournament -> log.info("Preloading " + tourRep.save(tournament)));

            battles.add(new Battle("The dawn of a new code", sdf.parse("2023-12-15"), sdf.parse("2023-12-20"), sdf.parse("2023-12-25"), "Python", false, 1, 1, educators.get(1 - 1), tournaments.get(1 - 1), false, "ciao", true, true, true));
            battles.add(new Battle("CodeCraft Clash", sdf.parse("2023-12-15"), sdf.parse("2023-12-20"), sdf.parse("2023-12-25"), "C++", true, 3, 5, educators.get(1 - 1), tournaments.get(1 - 1), false, "ciao", true, true, true));
            battles.add(new Battle("Whitespace Warriors", sdf.parse("2023-12-15"), sdf.parse("2023-12-16"), sdf.parse("2024-02-15"), "C", false, 2, 6, educators.get(1 - 1), tournaments.get(1 - 1), false, "ciao", true, true, true));
            battles.add(new Battle("Opening battle", sdf.parse("2024-02-10"), sdf.parse("2024-02-15"), sdf.parse("2024-02-20"), "Java", false, 1, 2, educators.get(1 - 1), tournaments.get(1 - 1), false, "ciao", true, true, true));

            //battles.get(0).addStudent(students.get(0));

            battles.forEach(battle -> log.info("Preloading " + batRep.save(battle)));

            teams.add(new Team("Team 01", battles.get(1 - 1)));
            teams.get(teams.size() - 1).addStudent(students.get(19 - 5));
            log.info("added a player to a team");
            teams.add(new Team("Team 02", battles.get(1 - 1)));
            teams.get(teams.size() - 1).addStudent(students.get(18 - 5));
            log.info("added a player to a team");
            teams.add(new Team("Team 03", battles.get(1 - 1)));
            teams.get(teams.size() - 1).addStudent(students.get(17 - 5));
            log.info("added a player to a team");
            teams.add(new Team("Team 04", battles.get(1 - 1)));
            teams.get(teams.size() - 1).addStudent(students.get(16 - 5));
            log.info("added a player to a team");

            teams.add(new Team("Team 05", battles.get(2 - 1)));
            teams.get(teams.size() - 1).addStudent(students.get(19 - 5));
            log.info("added a player to a team");
            teams.get(teams.size() - 1).addStudent(students.get(17 - 5));
            log.info("added a player to a team");
            teams.get(teams.size() - 1).addStudent(students.get(16 - 5));
            log.info("added a player to a team");
            teams.add(new Team("Team 06", battles.get(2 - 1)));
            teams.get(teams.size() - 1).addStudent(students.get(18 - 5));
            log.info("added a player to a team");
            teams.get(teams.size() - 1).addStudent(students.get(13 - 5));
            log.info("added a player to a team");
            teams.get(teams.size() - 1).addStudent(students.get(12 - 5));
            log.info("added a player to a team");
            teams.get(teams.size() - 1).addStudent(students.get(11 - 5));
            log.info("added a player to a team");
            teams.get(teams.size() - 1).addStudent(students.get(10 - 5));
            log.info("added a player to a team");
            teams.add(new Team("Team 07", battles.get(2 - 1)));
            teams.get(teams.size() - 1).addStudent(students.get(9 - 5));
            log.info("added a player to a team");
            teams.get(teams.size() - 1).addStudent(students.get(8 - 5));
            log.info("added a player to a team");
            teams.get(teams.size() - 1).addStudent(students.get(7 - 5));
            log.info("added a player to a team");
            teams.get(teams.size() - 1).addStudent(students.get(6 - 5));
            log.info("added a player to a team");

            teams.add(new Team("Team 08", battles.get(3 - 1)));
            teams.get(teams.size() - 1).addStudent(students.get(6 - 5));
            log.info("added a player to a team");
            teams.get(teams.size() - 1).addStudent(students.get(8 - 5));
            log.info("added a player to a team");
            teams.add(new Team("Team 09", battles.get(3 - 1)));
            teams.get(teams.size() - 1).addStudent(students.get(7 - 5));
            log.info("added a player to a team");
            teams.get(teams.size() - 1).addStudent(students.get(9 - 5));
            log.info("added a player to a team");
            teams.get(teams.size() - 1).addStudent(students.get(11 - 5));
            log.info("added a player to a team");
            teams.get(teams.size() - 1).addStudent(students.get(12 - 5));
            log.info("added a player to a team");
            teams.add(new Team("Team 10", battles.get(3 - 1)));
            teams.get(teams.size() - 1).addStudent(students.get(13 - 5));
            log.info("added a player to a team");
            teams.get(teams.size() - 1).addStudent(students.get(14 - 5));
            log.info("added a player to a team");
            teams.add(new Team("Team 11", battles.get(3 - 1)));
            teams.get(teams.size() - 1).addStudent(students.get(15 - 5));
            log.info("added a player to a team");
            teams.get(teams.size() - 1).addStudent(students.get(16 - 5));
            log.info("added a player to a team");
            teams.add(new Team("Team 12", battles.get(3 - 1)));
            teams.get(teams.size() - 1).addStudent(students.get(17 - 5));
            log.info("added a player to a team");
            teams.get(teams.size() - 1).addStudent(students.get(18 - 5));
            log.info("added a player to a team");
            teams.add(new Team("Team 13", battles.get(3 - 1)));
            teams.get(teams.size() - 1).addStudent(students.get(19 - 5));
            log.info("added a player to a team");
            teams.get(teams.size() - 1).addStudent(students.get(5 - 5));
            log.info("added a player to a team");

            teams.add(new Team("Team 14", battles.get(4 - 1)));
            teams.add(new Team("Team 15", battles.get(4 - 1)));
            teams.get(teams.size() - 1).addStudent(students.get(6 - 5));
            log.info("added a player to a team");
            teams.add(new Team("Team 16", battles.get(4 - 1)));
            teams.get(teams.size() - 1).addStudent(students.get(7 - 5));
            log.info("added a player to a team");
            teams.add(new Team("Team 17", battles.get(4 - 1)));
            teams.get(teams.size() - 1).addStudent(students.get(8 - 5));
            log.info("added a player to a team");
            teams.add(new Team("Team 18", battles.get(4 - 1)));
            teams.get(teams.size() - 1).addStudent(students.get(9 - 5));
            log.info("added a player to a team");
            teams.add(new Team("Team 19", battles.get(4 - 1)));
            teams.get(teams.size() - 1).addStudent(students.get(10 - 5));
            log.info("added a player to a team");
            teams.add(new Team("Team 20", battles.get(4 - 1)));
            teams.get(teams.size() - 1).addStudent(students.get(11 - 5));
            log.info("added a player to a team");

            teams.forEach(team -> log.info("Preloading " + teamRep.save(team)));

            teams.stream().forEach(team -> {
                team.getStudents().stream().forEach(student -> {
                    Tournament tournament = tourRep.getTournamentById(team.getBattle().getTournament().getId());
                    if (!tournament.getSubscribedStudents().contains(student)) {
                        tournament.addStudent(student);
                        student.addTournament(tournament);
                        stuRep.save(student);
                    }
                });
            });
        };
    }*/
}