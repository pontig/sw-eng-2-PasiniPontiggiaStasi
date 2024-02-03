package ckb.platform;

import ckb.platform.entities.Analyzer;
import ckb.platform.gitHubAPI.GitHubAPI;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootTest
public class StaticAnalysis {

    @Test
    void fromZip(){
        String pullsPath = null;
        Path absolutePath = Paths.get("fileStorage").toAbsolutePath();
        pullsPath = absolutePath + "/PullFiles" +
                "/" + 1 +
                "/" + 1 + "/";

        Analyzer analyzer = new Analyzer("CKBplatform-" + 1, "CKBplatform-" + 1, "admin", "admin01");

        int projectExists = analyzer.projectExists();

        if (projectExists == 0) {
            //create the project on our static analysis tool
            analyzer.createProjectSonarQube();
            //create the token for this project on our static analysis tool
            //analyzer.createToken();
            //create the webhook on our static analysis tool
            analyzer.createWebHook();
        }else if (projectExists == -1) {
            System.out.println("Error in the connection with the SonarQube server");
            return;
        }
        //run the analysis from the command line using repoPath as source directory
        analyzer.runAnalysisSonarQube("", pullsPath);
    }

    @Test
    void unzip(){
        String zipFilePath = "fileStorage/PullFiles/1/1/Progetto.zip";
        String destDir = "fileStorage/PullFiles/1/1/";
        GitHubAPI gitHubAPI = new GitHubAPI();
        gitHubAPI.unzip(zipFilePath, destDir);
    }
}
