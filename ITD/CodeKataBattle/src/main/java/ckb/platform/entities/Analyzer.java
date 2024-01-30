package ckb.platform.entities;

import ckb.platform.utils.ParameterStringBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Analyzer {

    private String projectName;
    private String projectKey;
    private String token;
    private String login;
    private String password;

    public Analyzer(String projectName, String projectKey, String login, String password) {
        this.projectName = projectName;
        this.projectKey = projectKey;
        this.login = login;
        this.password = password;
    }

    public void createProjectSonarQube() {
        try {
            // Specify the URL you want to connect to
            String url = "http://localhost:9000/api/projects/create";

            // Create a URL object
            URL urlObj = new URL(url);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

            Map<String, String> parameters = new HashMap<>();
            parameters.put("name", projectName);
            parameters.put("project", projectKey);

            connection.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
            out.flush();
            out.close();
            // Set the request method to GET
            connection.setRequestMethod("POST");

            // Add authentication header (Basic Auth example)
            String authString = this.login + ":" + this.password;
            String authHeader = "Basic " + Base64.getEncoder().encodeToString(authString.getBytes());
            connection.setRequestProperty("Authorization", authHeader);

            // Get the response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Read the response from the server
            // Use Jackson ObjectMapper to parse JSON response
            ObjectMapper objectMapper = new ObjectMapper();

            // Read the response from the server
            Map<String, Object> responseMap = objectMapper.readValue(connection.getInputStream(), Map.class);
            // Print the parsed response
            System.out.println("Parsed Response Map: " + responseMap);

            // Close the connection
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int runAnalysisSonarQube(String language){
        this.token = getToken();
        if (token == null){
            System.out.println("Token not found");
            return 0;
        }
        try {
            // Specify the working directory
            String workingDirectory = "C:\\Users\\Utente\\Desktop\\repo\\uni\\anno 4 semestre 1\\SOFTWARE ENGINEERING 2\\sw-eng-2-PasiniPontiggiaStasi\\ITD\\CodeKataBattle";

            // Specify the command you want to run
            String command = "sonar-scanner.bat -Dsonar.projectKey="+ projectKey+" -Dsonar.sources=./fileStorage -Dsonar.host.url=http://localhost:9000 -Dsonar.token=" + token;
            language = language.toLowerCase();
            if(language.equals("java")){
                command += " -Dsonar.java.binaries=./fileStorage/target/classes";
            }
            // Create ProcessBuilder with the command and set the working directory
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(new java.io.File(workingDirectory));

            // Start the process
            Process process = processBuilder.start();
            // Wait for the process to finish
            int exitCode = process.waitFor();
            System.out.println("Command exited with code: " + exitCode);
            return 1;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getToken(){
        try {
            // Specify the URL you want to connect to
            String url = "http://localhost:9000/api/project_badges/token";

            // Create a URL object
            URL urlObj = new URL(url);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

            Map<String, String> parameters = new HashMap<>();
            parameters.put("project", projectKey);

            connection.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
            out.flush();
            out.close();
            // Set the request method to GET
            connection.setRequestMethod("POST");

            // Add authentication header (Basic Auth example)
            String username = "admin";
            String password = "admin01";
            String authString = username + ":" + password;
            String authHeader = "Basic " + Base64.getEncoder().encodeToString(authString.getBytes());
            connection.setRequestProperty("Authorization", authHeader);

            // Get the response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Read the response from the server
            // Use Jackson ObjectMapper to parse JSON response
            ObjectMapper objectMapper = new ObjectMapper();

            // Read the response from the server
            Map<String, Object> responseMap = objectMapper.readValue(connection.getInputStream(), Map.class);
            // Print the parsed response
            System.out.println("Parsed Response Map: " + responseMap);

            // Close the connection
            connection.disconnect();
            return responseMap.get("token").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteProjectSonarQube() {
        try {
            // Specify the URL you want to connect to
            String url = "http://localhost:9000/api/projects/delete";

            // Create a URL object
            URL urlObj = new URL(url);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

            Map<String, String> parameters = new HashMap<>();
            parameters.put("project", projectKey);

            connection.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
            out.flush();
            out.close();
            // Set the request method to GET
            connection.setRequestMethod("POST");

            // Add authentication header (Basic Auth example)
            String authString = login + ":" + password;
            String authHeader = "Basic " + Base64.getEncoder().encodeToString(authString.getBytes());
            connection.setRequestProperty("Authorization", authHeader);

            // Get the response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Close the connection
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
