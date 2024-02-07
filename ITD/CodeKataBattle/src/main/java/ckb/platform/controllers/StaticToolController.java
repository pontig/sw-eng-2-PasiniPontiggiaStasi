package ckb.platform.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

import java.util.Map;
import java.util.Properties;

@RestController
public class StaticToolController {
    private static final Logger log = LoggerFactory.getLogger(StaticToolController.class);
    //this endpoint is activated when the user clicks on the "Get Static Analysis Results" button and the analysis is finished
    //in fact, the webhook of sonarqube will call this endpoint
    // TODO: va specificato sul DD?
    @PostMapping("/static_analysis/results")
    public ResponseEntity<String> getStaticAnalysisResults(@RequestBody Map<String, Object> payload) {
        String projectKey = (String) ((Map<String,Object>) payload.get("project")).get("key");
        try {
            // Specify the URL you want to connect to
            String url = "http://localhost:9000/api/measures/component?metrics&component=" + projectKey + "&metricKeys=%2Creliability_rating%2Csecurity_rating%2Cnew_maintainability_rating";

            // Create a URL object
            URL urlObj = new URL(url);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");

            // Add authentication header (Basic Auth example)
            Properties properties = new Properties();
            properties.load(new FileInputStream("src/main/resources/application.properties"));
            String authString = properties.getProperty("sonarqube.login") + ":" + properties.getProperty("sonarqube.password");
            String authHeader = "Basic " + Base64.getEncoder().encodeToString(authString.getBytes());
            connection.setRequestProperty("Authorization", authHeader);

            // Get the response code
            int responseCode = connection.getResponseCode();
            log.info("Response Code: " + responseCode);

            // Read the response from the server
            // Use Jackson ObjectMapper to parse JSON response
            ObjectMapper objectMapper = new ObjectMapper();

            // Read the response from the server
            Map<String, Object> responseMap = objectMapper.readValue(connection.getInputStream(), Map.class);
            ArrayList<Map<String, Object>> measures = (ArrayList<Map<String, Object>>) ((Map<String, Object>) responseMap.get("component")).get("measures");
            // Print the parsed response
            log.info("Parsed Response Map: " + measures);

            // Close the connection
            connection.disconnect();


            // Split the string based on the pattern "CKBplatform-" and get the last part
            String[] parts = projectKey.split("CKBplatform-");

            // Access the last part, assuming "ID" appears in the string
            String team;
            if (parts.length > 1) {
                team = parts[parts.length - 1];
                log.info(team);
                // Send the results to the CKB platform
                updateStaticAnalysisResults(team, measures);
            } else {
                log.error("Pattern not found");
            }


        } catch (Exception e) {
            log.error("Error in processing request", e);
        }

        return ResponseEntity.ok("OK");
    }

    private void updateStaticAnalysisResults(String team, ArrayList<Map<String, Object>> measures) {
        //compute the score
        int maintainabilityScore = 0;
        int reliabilityScore = 0;
        int securityScore = 0;
        for (Map<String, Object> measure : measures) {
            String metric = (String) measure.get("metric");
            switch (metric) {
                case "new_maintainability_rating" ->
                        maintainabilityScore = 6 - Math.round(Float.parseFloat((String) ((Map<String, Object>) measure.get("period")).get("value")));
                case "reliability_rating" -> reliabilityScore = 6 - Math.round(Float.parseFloat((String) measure.get("value")));
                case "security_rating" -> securityScore = 6 - Math.round(Float.parseFloat((String) measure.get("value")));
            }
        }
        // computing automatic score
        maintainabilityScore = maintainabilityScore * 100 / 5;
        reliabilityScore = reliabilityScore * 100 / 5;
        securityScore = securityScore * 100 / 5;

        // Specify the URL you want to connect to
        try{
            String url = "http://localhost:8080/ckb_platform/teams/score/staticAnalysis/" + team;
            // Create a URL object
            URL urlObj = new URL(url);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            connection.setDoOutput(true);

            String jsonInputString = "{\"maintainabilityScore\": " + maintainabilityScore + ", \"reliabilityScore\": " + reliabilityScore + ", \"securityScore\": " + securityScore + "}";
            OutputStream os = connection.getOutputStream();
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
            os.flush();
            os.close();

            BufferedReader br = new BufferedReader(new java.io.InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            log.info(response.toString());
            // Close the connection
            connection.disconnect();
        }catch(Exception e){
            log.error("Error in processing request", e);
        }
    }

}
