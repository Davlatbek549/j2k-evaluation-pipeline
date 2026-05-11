import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeoutException;

public class MultiCatchException {
    public String readOrDefault(Path path) {
        try {
            if (path == null) {
                throw new TimeoutException("missing path");
            }
            return Files.readString(path);
        } catch (IOException | IllegalArgumentException | TimeoutException ex) {
            System.out.println("Recovering from " + ex.getClass().getSimpleName());
            return "fallback";
        } finally {
            System.out.println("done");
        }
    }
}
