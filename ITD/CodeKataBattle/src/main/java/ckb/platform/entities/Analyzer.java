package ckb.platform.entities;

import ckb.platform.utils.ParameterStringBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Analyzer {
    private static final Logger log = LoggerFactory.getLogger(Analyzer.class);
    public static final String AUTH_TYPE = "Basic ";
    Properties prop = new Properties();
    private final String projectName;
    private final String projectKey;
    private String login;
    private String password;

    public Analyzer(String projectName, String projectKey) {
        this.projectName = projectName;
        this.projectKey = projectKey;
        try(FileInputStream input = new FileInputStream("src/main/resources/application.properties")) {
            prop.load(input);
            this.login = prop.getProperty("sonarqube.login");
            this.password = prop.getProperty("sonarqube.password");
        } catch (IOException e) {
            log.error("Error while reading application.properties: " + e.getMessage());
        }

    }

    public int projectExists(){
        // http://localhost:9000/api/components/show?component=projectKey
        try {
            // Specify the URL you want to connect to
            String url = "http://localhost:9000/api/components/show?component=" + projectKey;

            // Create a URL object
            URL urlObj = new URL(url);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");

            // Add authentication header (Basic Auth example)
            String authString = this.login + ":" + this.password;
            String authHeader = AUTH_TYPE + Base64.getEncoder().encodeToString(authString.getBytes());
            connection.setRequestProperty("Authorization", authHeader);

            // Get the response code
            int responseCode = connection.getResponseCode();
            log.info("Response Code for project existence: " + responseCode);


            // Close the connection
            connection.disconnect();
            if (responseCode == 200)
                return 1;
            else if (responseCode == 404)
                return 0;
            else
                return -1;
        } catch (Exception e) {
            log.error("Error while checking project existence: " + e.getMessage());
            return -1;
        }
    }

    public void createProjectSonarQube() {
        try {

            // Specify the URL you want to connect to
            String url = "http://localhost:9000/api/projects/create";

            // Create a URL object
            URL urlObj = new URL(url);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            // Set the request method to POST
            connection.setRequestMethod("POST");

            // Add authentication header (Basic Auth example)
            String authString = this.login + ":" + this.password;
            String authHeader = AUTH_TYPE + Base64.getEncoder().encodeToString(authString.getBytes());
            connection.setRequestProperty("Authorization", authHeader);

            Map<String, String> parameters = new HashMap<>();
            parameters.put("name", projectName);
            parameters.put("project", projectKey);

            connection.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
            out.flush();
            out.close();


            // Get the response code
            int responseCode = connection.getResponseCode();
            log.info("Response Code for creating SonarQube Project: " + responseCode);
            log.info("name: " + projectName + " projectKey: " + projectKey);

            // Read the response from the server
            // Use Jackson ObjectMapper to parse JSON response
            ObjectMapper objectMapper = new ObjectMapper();

            // Read the response from the server
            Map<String, Object> responseMap = objectMapper.readValue(connection.getInputStream(), Map.class);
            // Print the parsed response
            log.info("Parsed Response Map: " + responseMap);

            // Close the connection
            connection.disconnect();
        } catch (Exception e) {
            log.error("Error creating SonarQube Project: " + e.getMessage());
        }
    }

    public int runAnalysisSonarQube(String language, String path) {
        String token = getToken();
        if (token == null){
            log.info("Token not found");
            return 0;
        }
        try {

            // Specify the command you want to run
            String command = "sonar-scanner.bat -D sonar.projectKey="+ projectKey+" -D sonar.sources=. -D sonar.host.url=http://localhost:9000 -D sonar.token=" + token+
                    " -D sonar.login=" + login+ " -D sonar.password="+ password +" -D sonar.scm.exclusions.disabled=true ";

            language = language.toLowerCase();
            if(language.equals("java")){
                command += " -D sonar.java.binaries=./target/classes";
            }
            // Create ProcessBuilder with the command and set the working directory
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c",command);
            processBuilder.directory(new java.io.File(path));

            // Start the process
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info(line);
                }
            } catch (IOException e) {
                log.info("Error reading process output" + e.getMessage());
            }
            // Wait for the process to finish
            int exitCode = process.waitFor();


            log.info("Command exited with code: " + exitCode);
            return 1;
        }catch (IOException e){
            log.error("IOException running sonar-scanner: " + e.getMessage());
        }catch (InterruptedException e) {
            log.error("InterruptedException running sonar-scanner: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
        return 0;
    }

    public String getToken(){
        try {
            // Specify the URL you want to connect to
            String url = "http://localhost:9000/api/project_badges/token?project=" + projectKey;

            // Create a URL object
            URL urlObj = new URL(url);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");
            // Add authentication header (Basic Auth example)

            String authString = this.login + ":" + this.password;
            String authHeader = AUTH_TYPE + Base64.getEncoder().encodeToString(authString.getBytes());
            connection.setRequestProperty("Authorization", authHeader);

            // Get the response code
            int responseCode = connection.getResponseCode();
            log.info("Response Code for getting the token: " + responseCode);

            // Read the response from the server
            // Use Jackson ObjectMapper to parse JSON response
            ObjectMapper objectMapper = new ObjectMapper();

            // Read the response from the server
            Map<String, Object> responseMap = objectMapper.readValue(connection.getInputStream(), Map.class);
            // Print the parsed response
            log.info("Parsed Response Map: " + responseMap);

            // Close the connection
            connection.disconnect();
            return responseMap.get("token").toString();
        } catch (Exception e) {
            log.error("Error getting token: " + e.getMessage());
        }
        return null;
    }

    public void createWebHook(){
        try {
            // Specify the URL you want to connect to
            String url = "http://localhost:9000/api/webhooks/create";

            // Create a URL object
            URL urlObj = new URL(url);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            // Set the request method to POST
            connection.setRequestMethod("POST");

            // Add authentication header (Basic Auth example)
            String authString = this.login + ":" + this.password;
            String authHeader = AUTH_TYPE + Base64.getEncoder().encodeToString(authString.getBytes());
            connection.setRequestProperty("Authorization", authHeader);

            Map<String, String> parameters = new HashMap<>();
            parameters.put("name", "webhook-"+  projectKey);
            parameters.put("project", projectKey);
            parameters.put("url", "http://localhost:8080/ckb_platform/static_analysis/results");

            connection.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
            out.flush();
            out.close();

            // Get the response code
            int responseCode = connection.getResponseCode();
            log.info("Response Code for generating a webhook: " + responseCode);

            // Read the response from the server
            // Use Jackson ObjectMapper to parse JSON response
            ObjectMapper objectMapper = new ObjectMapper();

            // Read the response from the server
            Map<String, Object> responseMap = objectMapper.readValue(connection.getInputStream(), Map.class);
            // Print the parsed response
            log.info("Parsed Response Map: " + responseMap);

            // Close the connection
            connection.disconnect();
        } catch (Exception e) {
            log.error("Error creating webhook: " + e.getMessage());
        }
    }
}
