import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        new Ermit(path("test.txt"));
    }

    static String path(String name) {
        return Paths.get("src/" + name).toAbsolutePath().toString();
    }
}
