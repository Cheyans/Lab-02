package P1;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String args[]) throws IOException, URISyntaxException {
        List<Path> paths = new ArrayList<>();
        Files.walk(Paths.get(System.getProperties().getClass().getResource("/P1/Traces").toURI())).forEach(filePath -> {
            if(Files.isRegularFile(filePath)) {
                paths.add(filePath);
            }
        });

        Collections.sort(paths, (o1, o2) -> {
            String[] o1Ints = o1.getFileName().toString().split("-|\\.");
            String[] o2Ints = o2.getFileName().toString().split("-|\\.");
            int comparison = 0;
            for(int i = 0; i<o1Ints.length; i++) {
                comparison = Integer.compare(Integer.parseInt(o1Ints[i]), Integer.parseInt(o2Ints[i]));
                if(comparison != 0) {
                    return comparison;
                }
            }
            return comparison;
        });
        runSort(paths, "FCFS ");
        runSort(paths, "RR ");
    }

    private static void runSort(List<Path> paths, String sorter) {
        paths.forEach(path -> {
            try {
                List<Job> jobs = new ArrayList<>();
                Files.readAllLines(path).forEach(line -> {
                    String[] values = line.split(" ");
                    if(values.length != 1) {
                        jobs.add(new Job(Integer.parseInt(values[0]), Integer.parseInt(values[1])));
                    }
                    if(sorter.equals("FCFS ")) {
                        System.out.println(sorter + path.getFileName().toString().split("\\.")[0] + " " + FCFS.run(jobs));
                    } else {
                        System.out.println(sorter + path.getFileName().toString().split("\\.")[0] + " " + RR.run(jobs));
                    }

                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
