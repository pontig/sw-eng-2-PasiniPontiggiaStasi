package ckb.platform.gitHubAPI;

/*
 * GITHUB ACCOUNT:
 * mail: codekatabattle.platform@gmail.com
 * password: CKB202430l!
 *
 * GITHUB API TOKEN:
 * Auth token Used: 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
 * Auth token 2nd: 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
 * Expiration: Sat, Jan 18 2025
 *
 * FILES ORGANIZATION:
 * fileStorage
 * |-- CKBProblem
 * |   |-- <BattleId>.pdf
 * |   |-- ...
 * |-- PullFiles
 * |   |-- <BattleId>
 * |   |   |-- <TeamId>.zip
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
import ckb.platform.entities.Team;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Comparator;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class GitHubAPI {

    private static final Logger log = LoggerFactory.getLogger(GitHubAPI.class);

    Properties properties = new Properties();
    // Define Access Token for GitHub authentication
    private final String accessToken = "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";
    //properties.getProperty("github.auth.token");


    // Define username
    private final String username = "CodeKataBattlePlatform";
    public GitHubAPI(){
        try {
            properties.load(new FileInputStream("src/main/resources/application.properties"));
        } catch (IOException e) {
            log.error("Error loading properties file", e);
        }
    }

    public int createRepository(Battle battle, String description) {
        // Repo name same as battle name
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
            log.info("Create Repository Response Code: " + response);
            return response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            return 500;
        }
    }

    public int createFolder(Battle battle, String folder) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPut httpPut;
            String fileName = null;
            String path = null;
            String repoName = battle.getName().replace(" ", "-");
            Path absolutePath = Paths.get("fileStorage").toAbsolutePath();
            
            switch (folder) {
                case "CKBProblem" -> {
                    fileName = "/ProblemDescription.pdf";
                    path = absolutePath + "/" + folder + "/" + battle.getId() + fileName;
                }
                case "Rules" -> {
                    fileName = "/README.md";
                    path = absolutePath + "/" + folder + fileName;
                    folder = "";
                }
                case "main", "i_m" -> {
                    if(battle.getLanguage().equals("Java")){
                        folder = "JavaProject/src/main/java/org/problem";
                        fileName = "/Main.java";
                        path = absolutePath + "/ProjectOrganization/JavaProject/src/main/java/org/problem/Main.java";
                    } else if(battle.getLanguage().equals("JavaScript")){
                        folder = "JavaScriptProject/src/main/js";
                        fileName = "/main.js";
                        path = absolutePath + "/ProjectOrganization/JavaScriptProject/src/main/js/main.js";
                    } else {
                        if(folder.equals("i_m"))
                            fileName = "/__init__.py";
                        else
                            fileName = "/main.py";
                        folder = "PythonProject/src/main/python";
                        path = absolutePath + "/ProjectOrganization/PythonProject/src/main/python" + fileName;
                    }
                }
                case "Test" -> {
                    if(battle.getLanguage().equals("Java")){
                        fileName = "/MainTest.java";
                        path = absolutePath + "/" + folder + "/" + battle.getId() + fileName;
                        folder = "JavaProject/src/test/java/org/problem";
                    } else if(battle.getLanguage().equals("JavaScript")){
                        fileName = "/main.test.js";
                        path = absolutePath + "/" + folder + "/" + battle.getId() + fileName;
                        folder = "JavaScriptProject/src/test/js";
                    } else {
                        fileName = "/main_test.py";
                        path = absolutePath + "/" + folder + "/" + battle.getId() + fileName;
                        folder = "PythonProject/tests";
                    }
                }
                case "i_t" -> {
                    if(battle.getLanguage().equals("Python")) {
                        fileName = "/__init__.py";
                        folder = "PythonProject/tests";
                        path = absolutePath + "/ProjectOrganization/PythonProject/tests" + fileName;
                    }
                }
                case "BuildScript" -> {
                    if(battle.getLanguage().equals("Java"))
                        fileName = "/pom.xml";
                    else if(battle.getLanguage().equals("JavaScript"))
                        fileName = "/package.json";
                    else
                        fileName = "/setup.py";

                    path = absolutePath + "/" + folder + "/" + battle.getId() + fileName;

                    folder = battle.getLanguage() + "Project";
                }
            }


            // Create a PUT object to make the submission
            httpPut = new HttpPut("https://api.github.com/repos/" + username + "/" + repoName + "/contents/" + folder + fileName);
            System.out.println(httpPut);

            // Add the header to be authenticated
            httpPut.addHeader("Authorization", "token " + accessToken);
            
            // Encode content for the folder in base64
            File pdfFile = new File(path);
            byte[] fileContent = Files.readAllBytes(pdfFile.toPath());
            String base64Encoded = Base64.getEncoder().encodeToString(fileContent);

            // Build the repo
            StringEntity entity = new StringEntity("{\"message\":\"Create " + folder + " folder\",\"content\":\"" + base64Encoded + "\"}");
            httpPut.setEntity(entity);
            httpPut.setHeader("Accept", "application/vnd.github+json");
            log.info("File path: " + path + "\nPost path: " + httpPut);

            // Execute the PUT request and wait for response
            HttpResponse response = httpClient.execute(httpPut);
            log.info("Create Folder Response Code for " + folder + ": " + response);
            return response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            return 500;
        }
    }

    public String pullRepository(Battle battle, Team team, String repoName, String repoOwner) throws IOException {
        String pullsPath = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Create a get object to make the submission
            HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repoOwner + "/" + repoName.replace(" ", "-") + "/zipball/main");

            // Add the header to be authenticated
            httpGet.addHeader("Authorization", "Bearer " + accessToken);

            // Execute the GET request and wait for response
            HttpResponse response = httpClient.execute(httpGet);
            log.info("Create Repository Response Code: " + response);

            // Check if the request was successful (status code 200)
            if (response.getStatusLine().getStatusCode() == 200) {
                Path absolutePath = Paths.get("fileStorage").toAbsolutePath();
                pullsPath = absolutePath + "/PullFiles" +
                        "/" + battle.getId() +
                        "/" + team.getId() + "/";

                // If folder exist
                if (Files.exists(Path.of(pullsPath))) {
                    // Directory already exists, delete it
                    Files.walk(Path.of(pullsPath))
                            .sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                }

                Files.createDirectories(Path.of(pullsPath));

                try (InputStream inputStream = response.getEntity().getContent();
                     FileOutputStream outputStream = new FileOutputStream(pullsPath + "repo.zip")) {

                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    pullsPath = unzip(pullsPath + "repo.zip", pullsPath);
                    log.info("Repository downloaded successfully.");
                } catch (IOException e) {
                    log.error("Failed to download repository", e);
                }
            } else {
                // Handle the case where the request was not successful
                log.error("Failed to download repository. Status code: " + response.getStatusLine().getStatusCode());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Path.of(pullsPath).toString();
    }

    public String unzip(String zipFilePath, String destDir) {
        ZipInputStream zis=null;
        FileOutputStream fos=null;
        try {
            byte[] buffer = new byte[1024];
            zis = new ZipInputStream(new FileInputStream(zipFilePath));
            ZipEntry zipEntry = zis.getNextEntry();
            int i = 0;
            String dirName = null;
            while (zipEntry != null) {
                File newFile = newFile(new File(destDir), zipEntry);
                if (zipEntry.isDirectory()) {
                    if (i == 0) {
                        i++;
                        dirName = newFile.getName();
                    }
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    // fix for Windows-created archives
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }

                    // write file content
                    fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                zipEntry = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();
            return destDir.concat(dirName);
        }catch (IOException e){
            log.error("Failed to unzip file", e);
        }finally {
            try {
                zis.close();
                fos.close();
            } catch (IOException e) {
                log.error("Failed to close zip input stream", e);
            }
        }
        return null;
    }

    private File newFile(File destinationDir, ZipEntry zipEntry) throws IOException{
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }
}