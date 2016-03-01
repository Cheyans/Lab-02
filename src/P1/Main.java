package P1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Main {
    public static void main(String args[]) throws Exception {
        List<Path> paths = new ArrayList<>();
        Files.walk(Paths.get(System.getProperties().getClass().getResource("/P1/Traces").toURI())).forEach(filePath -> {
            if(Files.isRegularFile(filePath)) {
                paths.add(filePath);
            }
        });

        Collections.sort(paths, (path1, path2) -> {
            String[] path1Names = path1.getFileName().toString().split("-|\\.");
            String[] path2Names = path2.getFileName().toString().split("-|\\.");
            int comparison = 0;
            for(int i = 0; i<path1Names.length; i++) {
                comparison = Integer.compare(Integer.parseInt(path1Names[i]), Integer.parseInt(path2Names[i]));
                if(comparison != 0) {
                    return comparison;
                }
            }
            //Should never happen, means the file has the same name or an improper naming scheme
            return comparison;
        });
        runScheduler(paths, "FCFS");
        runScheduler(paths, "RR");
    }

    private static void runScheduler(List<Path> paths, String scheduler) {
        paths.forEach(path -> {
            try {
                List<Job> jobs = new LinkedList<>();
                Files.readAllLines(path).forEach(line -> {
                    String[] values = line.split(" ");
                    if(values.length != 1) {
                        jobs.add(new Job(Integer.parseInt(values[0]), Integer.parseInt(values[1])));
                    }
                });
                if(scheduler.equals("FCFS")) {
                    System.out.println(String.format("%s %s %.2f", scheduler ,path.getFileName().toString().split("\\.")[0], FCFS.run(jobs)));
                } else {
                    System.out.println(String.format("%s %s %.2f", scheduler ,path.getFileName().toString().split("\\.")[0], RR.run(jobs)));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
