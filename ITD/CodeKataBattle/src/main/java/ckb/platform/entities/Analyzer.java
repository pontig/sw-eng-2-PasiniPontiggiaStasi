package ckb.platform.entities;

import ckb.platform.utils.ParameterStringBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
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
            String authHeader = "Basic " + Base64.getEncoder().encodeToString(authString.getBytes());
            connection.setRequestProperty("Authorization", authHeader);

            // Get the response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code for project existence: " + responseCode);


            // Close the connection
            connection.disconnect();
            if (responseCode == 200)
                return 1;
            else if (responseCode == 404)
                return 0;
            else
                return -1;
        } catch (Exception e) {
            e.printStackTrace();
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
            String authHeader = "Basic " + Base64.getEncoder().encodeToString(authString.getBytes());
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
            System.out.println("Response Code for creating SonarQube Project: " + responseCode);
            System.out.println("name: " + projectName + " projectKey: " + projectKey);

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

    public int runAnalysisSonarQube(String language, String path){
        this.token = getToken();
        if (token == null){
            System.out.println("Token not found");
            return 0;
        }
        try {

            // Specify the command you want to run
            String command = "sonar-scanner.bat -D sonar.projectKey="+ projectKey+" -D sonar.sources=. -D sonar.host.url=http://localhost:9000 -D sonar.token=" + token+
                    " -D sonar.login=" + login+ " -D sonar.password="+ password +" -D sonar.scm.exclusions.disabled=true ";

            language = language.toLowerCase();
            if(language.equals("java")){
                //todo change the path to the path of the project
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
                    System.out.println(line);
                }
            } catch (IOException e) {
                System.out.println("Error reading process output" + e.getMessage());
            }
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
            String url = "http://localhost:9000/api/project_badges/token?project=" + projectKey;

            // Create a URL object
            URL urlObj = new URL(url);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");
            // Add authentication header (Basic Auth example)
            String username = "admin";
            String password = "admin01";
            String authString = username + ":" + password;
            String authHeader = "Basic " + Base64.getEncoder().encodeToString(authString.getBytes());
            connection.setRequestProperty("Authorization", authHeader);

            // Get the response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code for getting the token: " + responseCode);

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
            String authHeader = "Basic " + Base64.getEncoder().encodeToString(authString.getBytes());
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
            System.out.println("Response Code for generating a webhook: " + responseCode);

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
}
