package ckb.platform.testRepo;

import ckb.platform.entities.Battle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class TestRepository {
    public void buildAndTestRepo(Battle battle, boolean commandBuild, String repoPath) {
        String command;
        String language = battle.getLanguage();

        // Define command to compile and test
        switch(language){
            case "Java":
                command = "mvn clean install";
                break;
            case "JavaScript":
                if(!commandBuild)
                    command = "npm install --save-dev jest jest-junit";
                else
                    command = "npm test";
                break;
            case "Python":
                // pip install --upgrade --force-reinstall pytest e poi il warning imposable a path
                command = "pytest -v tests";
                break;
            // TODO: non implemented
            case "C":
            case "Cpp":
            default:
                return;
        }
        // Execute command
        startProcess(repoPath, command);
    }

    private void startProcess(String repoPath, String command){
        try {
            // Change directory
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.directory(new File(repoPath));

            // Execute cmd command
            processBuilder.command("cmd.exe", "/c", command);
            Process process = processBuilder.start();

            // Read output and print it in console
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Check for any error
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println("Error during command - exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(e);
        }
    }

    public int getTestPassedJava(String repoPath) throws FileNotFoundException {
        // Get the test result as JUnit file
        File fileJUnit = new File(repoPath + "/target/surefire-reports/MainTest.txt");

        // Scan the file
        Scanner scanner = new Scanner(fileJUnit);

        int run = 0;
        int failures = 0;

        // Read each row of the file
        while (scanner.hasNextLine()) {
            String riga = scanner.nextLine();

            // Look for values "run" and "failures"
            if (riga.contains("Tests run:")) {
                // Get the value of run and failures
                String[] values = riga.split(",");
                run = Integer.parseInt(values[0].split(":")[1].trim());
                failures = Integer.parseInt(values[1].split(":")[1].trim());
            }
        }

        // Print values on console and return score
        int passedTest = run - failures;

        if(run == 0)
            return 1;

        System.out.println("Score = " + passedTest + " * 100 / " + run + " = " + (passedTest*100)/run);

        scanner.close();
        return (passedTest*100)/run;
    }

    public int getTestPassedJavaScript(String repoPath) throws IOException, ParserConfigurationException, SAXException {
        // Open the file got from jest
        File fileResultJest = new File(repoPath + "/test-results/jest-results.xml");

        // Read the XML report file
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(fileResultJest);

        // Get the tag "testsuites"
        Element testsuitesElement = document.getDocumentElement();

        // Obtain the value "tests" and "failures"
        int tests = Integer.parseInt(testsuitesElement.getAttribute("tests"));
        int failures = Integer.parseInt(testsuitesElement.getAttribute("failures"));

        // Print values on console and return score
        int passedTest = tests - failures;

        if(tests == 0)
            return 1;

        System.out.println("Score = " + passedTest + " * 100 / " + tests + " = " + (passedTest*100)/tests);

        return (passedTest*100)/tests;
    }

    public int getTestPassedPython(String repoPath) throws FileNotFoundException {
        // Read file nodeids to get the number of test done
        File fileNodeIds = new File(repoPath + "/.pytest_cache/v/cache/nodeids");
        String content;

        Scanner scanner = new Scanner(fileNodeIds);
        scanner.useDelimiter("]");
        content = scanner.next();
        int run = content.split(",").length;

        // Read the file lastfailed to get the number of test failed
        File fileLastFailed = new File(repoPath + "/.pytest_cache/v/cache/lastfailed");
        int failures = 0;

        if(fileLastFailed.exists()){
            scanner = new Scanner(fileLastFailed);
            scanner.useDelimiter("}");
            content = scanner.next();
            failures = content.split(",").length;
        }

        // Print values on console and return score
        int passedTest = run - failures;

        if(run == 0)
            return 1;

        System.out.println("Score = " + passedTest + " * 100 / " + run + " = " + (passedTest*100)/run);

        scanner.close();

        return (passedTest*100)/run;
    }

    // TODO: For testing purpose only
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        String language = "Python"; // Modify this field to build

        Path absolutePath = Paths.get("fileStorage").toAbsolutePath();
        String repoPath = absolutePath + "/ProjectOrganization/" + language + "Project";

        Battle b = new Battle("TestBuild", null,null, null, language, false,
        1, 1, null, null, false, null, false, false, false);
        TestRepository build = new TestRepository();

        if(language.equals("JavaScript")) {
            build.buildAndTestRepo(b, false, repoPath);
            build.buildAndTestRepo(b, true, repoPath);
        } else build.buildAndTestRepo(b, false, repoPath);

        if(language.equals("Java"))
            build.getTestPassedJava(repoPath);
        else if (language.equals("JavaScript")) {
            build.getTestPassedJavaScript(repoPath);
        } else {
            build.getTestPassedPython(repoPath);
        }
    }
}
