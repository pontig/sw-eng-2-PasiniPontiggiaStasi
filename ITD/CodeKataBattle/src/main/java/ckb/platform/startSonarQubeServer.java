package ckb.platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
@Configuration
public class startSonarQubeServer {
/*
    private static final Logger log = LoggerFactory.getLogger(startSonarQubeServer.class);
    private Process sonarQubeServerProcess;
    private Thread sonarServerThread;

    @Bean
    AsyncTaskExecutor startingServer() {
        sonarServerThread = new Thread(() -> {
            log.info("Starting SonarQube Server...");
            // Start SonarQube Server
            ProcessBuilder processBuilder = new ProcessBuilder("C:\\sonarqube\\bin\\windows-x86-64\\StartSonar.bat");
            // Start the process
            try {
                sonarQubeServerProcess = processBuilder.start();
                logProcessOutput(sonarQubeServerProcess);
                sonarQubeServerProcess.waitFor(); // Wait for the process to complete
            } catch (IOException | InterruptedException e) {
                log.error("Error starting SonarQube Server", e);
            }
        });

        sonarServerThread.start();

        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(1);
        taskExecutor.setMaxPoolSize(1);
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(4);
        return taskExecutor;
    }

    private void logProcessOutput(Process process) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.info(line);
            }
        } catch (IOException e) {
            log.error("Error reading process output", e);
        }
    }

    // method to stop the SonarQube server if needed
    public void stopSonarQubeServer() {
        if (sonarQubeServerProcess != null) {
            sonarQubeServerProcess.destroy();
        }
        if (sonarServerThread != null) {
            sonarServerThread.interrupt();
        }
    }*/
}