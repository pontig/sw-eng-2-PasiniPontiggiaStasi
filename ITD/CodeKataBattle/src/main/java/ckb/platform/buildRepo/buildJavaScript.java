package ckb.platform.buildRepo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class buildJavaScript {
    public static void main(String[] args) throws IOException {
        Path absolutePath = Paths.get("fileStorage").toAbsolutePath();
        String directoryPath = absolutePath + "/ProjectOrganization/JavaScriptProject";

        try {
            // Cambia directory
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.directory(new File(directoryPath));

            // Esegui il comando make
            processBuilder.command("cmd.exe", "/c", "npm start");
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
}
