package ckb.platform.controllers;

import ckb.platform.entities.Analyzer;
import ckb.platform.formParser.CreateTournamentRequest;
import ckb.platform.utils.ParameterStringBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.json.Json;
import org.apache.http.client.methods.HttpGet;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.management.ObjectName;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
public class StaticToolController {
    //this endpoint is activated when the user clicks on the "Get Static Analysis Results" button and the analysis is finished
    //in fact, the webhook of sonarqube will call this endpoint
    @PostMapping("/static_analysis/results")
    public ResponseEntity<String> getStaticAnalysisResults(@RequestBody Map<String, Object> payload) {
        String projectName = (String) ((Map<String,Object>) payload.get("project")).get("name");
        try {
            // Specify the URL you want to connect to
            String url = "http://localhost:9000/api/measures/component?metrics&component=" + projectName + "&metricKeys=%2Creliability_rating%2Csecurity_rating%2Cnew_maintainability_rating";

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
            System.out.println("Response Code: " + responseCode);

            // Read the response from the server
            // Use Jackson ObjectMapper to parse JSON response
            ObjectMapper objectMapper = new ObjectMapper();

            // Read the response from the server
            Map<String, Object> responseMap = objectMapper.readValue(connection.getInputStream(), Map.class);
            ArrayList<Map<String, Object>> measures = (ArrayList<Map<String, Object>>) ((Map<String, Object>) responseMap.get("component")).get("measures");
            // Print the parsed response
            System.out.println("Parsed Response Map: " + measures);

            // Close the connection
            connection.disconnect();


            // Split the string based on the pattern "CKBplatform-" and get the last part
            String[] parts = projectName.split("CKBplatform-");

            // Access the last part, assuming "ID" appears in the string
            String team = null;
            if (parts.length > 1) {
                team = parts[parts.length - 1];
                System.out.println(team);
                // Send the results to the CKB platform
                updateStaticAnalysisResults(team, measures);
            } else {
                System.out.println("Pattern not found");
            }


        } catch (Exception e) {
            e.printStackTrace();
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
            if (metric.equals("new_maintainability_rating")) {
                maintainabilityScore =6 - Integer.parseInt((String)((Map<String,Object>) measure.get("period")).get("value"));
            } else if (metric.equals("reliability_rating")) {
                reliabilityScore = 6 - Integer.parseInt((String) measure.get("value"));
            } else if (metric.equals("security_rating")) {
                securityScore = 6 - Integer.parseInt((String) measure.get("value"));
            }
        }
        // computing automatic score
        int x = (maintainabilityScore + reliabilityScore + securityScore) / 3;
        int score = x * 100 / 15;

        // Specify the URL you want to connect to
        try{
            String url = "http://localhost:8080/ckb_platform/teams/score/staticAnalysis" + team;
            // Create a URL object
            URL urlObj = new URL(url);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("POST");

            Map<String, String> parameters = new HashMap<>();
            parameters.put("score", Integer.toString(score));

            connection.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
            out.flush();
            out.close();

            // Add authentication header (Basic Auth example)
            //TODO authentication
            String username = "chipndalebutjustdale@mail.mit.com";
            String password = "nervi";
            String authString = username + ":" + password;
            String authHeader = "Basic " + Base64.getEncoder().encodeToString(authString.getBytes());
            connection.setRequestProperty("Authorization", authHeader);

            // Get the response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Read the response from the server
            // Use Jackson ObjectMapper to parse JSON response
            ObjectMapper objectMapper = new ObjectMapper();

            // Close the connection
            connection.disconnect();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
