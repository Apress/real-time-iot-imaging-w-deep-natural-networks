import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;

public class SecondFinal {

    static int myfunction1(final int i) {
        return 3 + i + 0;
    }

    final static String filePath = "my.log";

    static void myfunction2(final int i) {
        try (Writer writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(String.format(">> %d\n", i));
        } catch (Exception e) {

        }
    }

    public static void main(final String[] args) throws Exception {
        int i = 0;
        while (true) {
            i++;
            Thread.sleep(500);
        }
    }
}