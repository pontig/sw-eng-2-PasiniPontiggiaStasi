package ckb.platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.Properties;
import java.util.Scanner;

@SpringBootApplication
public class CodeKataBattleApplication {

	private static Logger logger = LoggerFactory.getLogger(CodeKataBattleApplication.class);

	public static void main(String[] args) {
		insertDir();
		SpringApplication.run(CodeKataBattleApplication.class, args);
	}
	public static void insertDir(){
		logger.info("Insert the location of the StartSonar.bat file (e.g. C:\\sonarqube\\bin\\windows-x86-64, this is the default location) : ");
		logger.info("If you want to use the default location, press enter :  ");
		Scanner read = new Scanner(System.in);
		String dir = read.nextLine();
		if(!dir.equals("")){
			boolean check = new File(dir, "StartSonar.bat").exists();
			if(!check){
				logger.warn("Invalid directory, please try again");
				insertDir();
			}else {
				Properties properties = new Properties();
				properties.setProperty("SonarQubeServerDir", dir+"\\StartSonar.bat");
			}

		}
	}
}
