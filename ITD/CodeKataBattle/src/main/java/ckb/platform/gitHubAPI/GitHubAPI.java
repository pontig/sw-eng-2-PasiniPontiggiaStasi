package ckb.platform.gitHubAPI;

/* GitHub Account credential
 * mail: codekatabattle.platform@gmail.com
 * password: CKB202430l!
 *
 * GitHub API
 * Auth token: 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
 * Expiration: Sat, Jan 18 2025
 */

import ckb.platform.entities.Battle;
import ckb.platform.formParser.RepoPullRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Date;

public class GitHubAPI {
    private final HttpClient httpClient;
    // Define Access Token for GitHub authentication
    private final String accessToken = "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";

    // Define username
    private final String username = "CodeKataBattlePlatform";
    public GitHubAPI(){
        // Make the BackEnd as a client
        httpClient = HttpClients.createDefault();
    }

    public int createRepository(Battle battle, String description) {
        // Repo name same as battle name
        // TODO: Battle name non deve aver spazi
        // TODO: La repo deve avere un nome diverso quindi se esiste già una repo con il nome append dell'id
        String repoName = battle.getName();

        try {
            // Create a post object to make the submission
            HttpPost httpPost = new HttpPost("https://api.github.com/user/repos");

            // Add the header to be authenticated
            httpPost.addHeader("Authorization", "token " + accessToken);

            // Build the repo
            StringEntity entity = new StringEntity("{\"name\": \"" + repoName + "\", \"description\": \"" + description + "\", \"private\":false}");
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/vnd.github+json");

            // Execute the POST request and wait for response
            HttpResponse response = this.httpClient.execute(httpPost);
            System.out.println("Create Repository Response Code: " + response);
            return response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            return 500;
        }
    }

    public int createFolder(Battle battle, String folder){
        try {
            // Define file name
            String fileName;
            switch(folder){
                case("CKBProblem"):
                    fileName = "/1.pdf";
                    //fileName = battle.getId().toString() + ".pdf";
                    break;
                case("Delivery"):
                    fileName = "/ActionTemplate.pdf";
                    break;
                case ("Rules"):
                    fileName = "/Rules.txt";
                    break;
                default:
                    return 500;
            }

            System.out.println(fileName);

            // Create a post object to make the submission
            HttpPut httpPut = new HttpPut("https://api.github.com/repos/" + username + "/" + battle.getName() + "/contents/" + folder + fileName);

            // Add the header to be authenticated
            httpPut.addHeader("Authorization", "token " + accessToken);

            // Encode content for the folder in base64
            Path absolutePath = Paths.get("fileStorage").toAbsolutePath();
            String path = absolutePath + "/" + folder + fileName;
            System.out.println(path);
            File pdfFile = new File(path);
            byte[] fileContent = Files.readAllBytes(pdfFile.toPath());
            String base64Encoded = Base64.getEncoder().encodeToString(fileContent);
            System.out.println(base64Encoded);

            // Build the repo
            StringEntity entity = new StringEntity("{\"message\":\"Create " + folder + " folder\",\"content\":\"" + base64Encoded + "\"}");
            httpPut.setEntity(entity);
            httpPut.setHeader("Accept", "application/vnd.github+json");

            System.out.println(httpPut);
            // Execute the POST request and wait for response
            HttpResponse response = this.httpClient.execute(httpPut);
            System.out.println("Create Folder Response Code for " + folder + ": " + response);
            return response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            return 500;
        }
    }

    @PostMapping("/battle/pulls")
    public void closeTournament(@RequestBody RepoPullRequest repoPullRequest) {
        String repository = repoPullRequest.getRepository();
        String pusher = repoPullRequest.getPusher();


    }
    public static void main(String[] args) throws InterruptedException {
        GitHubAPI newRepo = new GitHubAPI();

        String repoName = "10";
        Battle newBattle = new Battle(repoName, new Date(), new Date(), new Date(), "Java", true, 1, 1, null, null, false);
        String description = "CKB Battle deadline: " + new Date();

        int repoCreated = newRepo.createRepository(newBattle, description);
        if(repoCreated == 201) {
            int response;
            response = newRepo.createFolder(newBattle, "Delivery");
            if(response == 201) {
                System.out.println("Top delivery");
                response = newRepo.createFolder(newBattle, "CKBProblem");
                if(response == 201) {
                    System.out.println("Top CKB");
                    response = newRepo.createFolder(newBattle, "Rules");
                    if(response == 201)
                        System.out.println("Top rules");
                    else
                        System.out.println("Erro re");
                } else
                    System.out.println("Errore C");
            } else
                System.out.println("Errore D");
        } else {
            System.out.println("There was an error in creating the repo");
        }
    }
}
