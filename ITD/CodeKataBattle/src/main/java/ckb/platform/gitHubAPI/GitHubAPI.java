package ckb.platform.gitHubAPI;

/*
 * GITHUB ACCOUNT:
 * mail: codekatabattle.platform@gmail.com
 * password: CKB202430l!
 *
 * GITHUB API TOKEN:
 * Auth token: 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
 * Expiration: Sat, Jan 18 2025
 *
 * FILES ORGANIZATION:
 * fileStorage
 * |-- CKBProblem
 * |   |-- <BattleId>.pdf
 * |   |-- ...
 * |-- Delivery
 * |   |-- ActionTemplate.pdf
 * |-- PullFiles
 * |   |-- <BattleId>
 * |   |   |-- <TeamId>.extension
 * |   |   |-- ...
 * |   |-- ...
 * |-- Rules
 * |   |-- README.md
 *
 * USING GITHUB API:
 * As our system runs on a https://localhost:8080 and GitHub need to reach a public address, we will use "ngrok".
 * Ngrok generate a tunnel address that need to be used in the WorkFlow Action to reach the End-Point "/ckb_platform/battle/pulls"
 * Log In with CKB GitHub credentials on Ngrok https://dashboard.ngrok.com/login
 * Go in section Your Authtoken and copy the command: ngrok config add-authtoken 2bTBjPXyFBY3dy8NlZSJ5LzrC9x_7FRwMrsggt8f68KFpvJfW
 * Ngrok can be downloaded from https://ngrok.com/download, unzip it, and open it (CMD will pop up)
 * Paste the command you copied before
 *
 * From now, every time you need to use ngrok, open it and type the command: ngrok http https://localhost:8080
 * A public address will be prompted, this will be used in GitHub Action
 *
 * RESOURCES:
 * https://docs.github.com/en/rest?apiVersion=2022-11-28
 *
 */

import ckb.platform.entities.Battle;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class GitHubAPI {
    // Define Access Token for GitHub authentication
    private final String accessToken = "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";

    // Define username
    private final String username = "CodeKataBattlePlatform";
    public GitHubAPI(){}

    public int createRepository(Battle battle, String description) {
        // Repo name same as battle name
        // TODO: La repo deve avere un nome diverso quindi se esiste già una repo con il nome append dell'id
        String repoName = battle.getName();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Create a post object to make the submission
            HttpPost httpPost = new HttpPost("https://api.github.com/user/repos");

            // Add the header to be authenticated
            httpPost.addHeader("Authorization", "token " + accessToken);

            // Build the repo
            StringEntity entity = new StringEntity("{\"name\": \"" + repoName + "\", \"description\": \"" + description + "\", \"private\":false}");
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/vnd.github+json");

            // Execute the POST request and wait for response
            HttpResponse response = httpClient.execute(httpPost);
            System.out.println("Create Repository Response Code: " + response);
            return response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            return 500;
        }
    }

    public int createFolder(Battle battle, String folder, String fileName) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPut httpPut;
            String repoName = battle.getName().replace(" ", "-");

            if(folder.equals("Rules")) {
                fileName = "/" + fileName + ".md";
                // Create a PUT object to make the submission
                httpPut = new HttpPut("https://api.github.com/repos/" + username + "/" + repoName + "/contents" + fileName);
            }
            else {
                fileName = "/" + fileName + ".pdf";
                // Create a PUT object to make the submission
                httpPut = new HttpPut("https://api.github.com/repos/" + username + "/" + repoName + "/contents/" + folder + fileName);
            }

            // Add the header to be authenticated
            httpPut.addHeader("Authorization", "token " + accessToken);

            // Encode content for the folder in base64
            Path absolutePath = Paths.get("fileStorage").toAbsolutePath();
            String path = absolutePath + "/" + folder + fileName;

            File pdfFile = new File(path);
            byte[] fileContent = Files.readAllBytes(pdfFile.toPath());
            String base64Encoded = Base64.getEncoder().encodeToString(fileContent);

            // Build the repo
            StringEntity entity = new StringEntity("{\"message\":\"Create " + folder + " folder\",\"content\":\"" + base64Encoded + "\"}");
            httpPut.setEntity(entity);
            httpPut.setHeader("Accept", "application/vnd.github+json");
            System.out.println("File path: " + path + "\nPost path: " + httpPut);

            // Execute the PUT request and wait for response
            HttpResponse response = httpClient.execute(httpPut);
            System.out.println("Create Folder Response Code for " + folder + ": " + response);
            return response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            return 500;
        }
    }

    // TODO Pull specific file
    /*
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Create a get object to make the submission
            HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + pusher + "/" + repository + "/contents/Delivery" + "/2.pdf");

            // Add the header to be authenticated
            httpGet.addHeader("Authorization", "Bearer " + accessToken);
            httpGet.setHeader("Accept", "application/vnd.github+json");

            // Execute the POST request and wait for response
            HttpResponse response = httpClient.execute(httpGet);
            System.out.println("Create Repository Response Code: " + response);

            // Verificare che la risposta sia di successo (status code 200)
            if (response.getStatusLine().getStatusCode() == 200) {
                // Ottenere l'InputStream dal corpo della risposta
                try (InputStream inputStream = response.getEntity().getContent()) {
                    // Salvare l'InputStream in un file JAR locale
                    try (OutputStream outputStream = new FileOutputStream("localfile.jar")) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                }
                System.out.println("File JAR salvato con successo.");
            } else {
                System.out.println("Errore durante la richiesta. Response Code: " + response.getStatusLine().getStatusCode());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
}