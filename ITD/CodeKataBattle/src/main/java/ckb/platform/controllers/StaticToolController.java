package ckb.platform.controllers;

import ckb.platform.formParser.CreateTournamentRequest;
import ckb.platform.formParser.StaticAnalysisResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.json.Json;
import org.apache.http.client.methods.HttpGet;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;

@RestController
public class StaticToolController {
    //this endpoint is activated when the user clicks on the "Get Static Analysis Results" button and the analysis is finished
    //in fact, the webhook of sonarqube will call this endpoint
    @PostMapping("/static_analysis/results")
    public ResponseEntity<String> getStaticAnalysisResults(@RequestBody StaticAnalysisResult request) {
        String token = "sqp_cf3249fc8f00d0fc259b388f93ea4de4f8c6b224";
        try {
            // Specify the URL you want to connect to
            String url = "http://localhost:9000/api/measures/component?metrics&component=CKB-platform&metricKeys=%2Creliability_rating%2Csecurity_rating%2Cnew_maintainability_rating";

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
            ArrayList<Map<String, Object>> measures =(ArrayList<Map<String, Object>>) ((Map<String, Object>) responseMap.get("component")).get("measures");
            // Print the parsed response
            System.out.println("Parsed Response Map: " + measures);

            // Close the connection
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok("OK");
    }

}
