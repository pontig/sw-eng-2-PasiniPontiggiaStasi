package ckb.platform.testRepo;

import ckb.platform.entities.Battle;
import ckb.platform.entities.Team;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestRepository {
    public void buildAndTestRepo(Battle battle, Team team, boolean commandBuild){
        String command;
        String language = battle.getLanguage();

        switch(language){
            case "Java":
                command = "mvn clean install";
                break;
            case "JavaScript":
                if(!commandBuild)
                    command = "npm install";
                else
                    command = "npm test";
                break;
            case "Python":
                command = "python -m unittest tests.main_test"; // più lista nome altri file
                break;
            // TODO: non implemented
            case "C":
            case "Cpp":
            default:
                return;
        }

        // TODO: cambiare i percorsi per le repo
        Path absolutePath = Paths.get("fileStorage").toAbsolutePath();
        String directoryPath = absolutePath + "/ProjectOrganization/" + language + "Project";

        startProcess(directoryPath, command);
    }

    private void startProcess(String repoPath, String command){
        try {
            // Cambia directory
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.directory(new File(repoPath));

            // Esegui il comando make
            processBuilder.command("cmd.exe", "/c", command);
            Process process = processBuilder.start();

            // Leggi l'output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Controlla se ci sono errori
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println("Errore durante l'esecuzione del comando. Codice di uscita: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // TODO: For testing purpose only
    public static void main(String[] args) throws IOException {
        String language = "JavaScript"; // Modify this field to build

        Battle b = new Battle("TestBuild", null,null, null, language, false,
        1, 1, null, null, false, null, false, false, false);
        TestRepository build = new TestRepository();

        if(language.equals("JavaScript")) {
            build.buildAndTestRepo(b, null, false);
            build.buildAndTestRepo(b, null, true);
        } else build.buildAndTestRepo(b, null, false);
    }
}
