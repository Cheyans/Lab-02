package P1;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Main {
    public static void main(String args[]) throws IOException, URISyntaxException {
        List<Path> paths = new ArrayList<>();
        Files.walk(Paths.get(System.getProperties().getClass().getResource("/P1/Traces").toURI())).forEach(filePath -> {
            if(Files.isRegularFile(filePath)) {
                paths.add(filePath);
            }
        });

        Collections.sort(paths);
        paths.forEach(file -> {
            System.out.println(file.getFileName());
        });
    }
}
