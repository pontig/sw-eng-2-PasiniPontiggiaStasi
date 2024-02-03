package ckb.platform;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest
public class random {

    @Test
    void checkParser(){
        System.out.println("This is a test");
        String[] parts ;
        String projectName = "CKBplatform-Test";
        parts = projectName.split("CKBplatform-");
        for (String part : parts) {
            System.out.println(part);
        }
        System.out.println(parts.length);
    }
}
