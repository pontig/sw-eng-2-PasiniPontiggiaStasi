import org.junit.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.Assert.assertEquals;

public class MainTest {
    @Test
    public void testMainMethodOutput() {
        // Redirect System.out to capture the output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Run the main method
        Main.main(new String[]{});

        // Restore System.out
        System.setOut(System.out);

        // Check the output
        assertEquals("Hello, World in Java!".trim(), outContent.toString().trim());
    }
}