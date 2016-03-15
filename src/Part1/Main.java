import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
		//Get all files in the traces folder and add them to the path list
        List<Path> paths = new ArrayList<>();
        Files.walk(Paths.get(Main.class.getResource("Traces").toURI())).forEach(filePath -> {
            if (Files.isRegularFile(filePath)) {
                paths.add(filePath);
            }
        });
		
		//Sort paths based on their numeric file names by comparing the 3 comparable integers in the filenames
        Collections.sort(paths, (path1, path2) -> {
			//Extract integers from path name
            String[] path1Names = path1.getFileName().toString().split("-|\\.");
            String[] path2Names = path2.getFileName().toString().split("-|\\.");
            int comparison = 0;
			//Compring the 3 integers
            for (int i = 0; i < path1Names.length; i++) {
                comparison = Integer.compare(Integer.parseInt(path1Names[i]), Integer.parseInt(path2Names[i]));
                if (comparison != 0) {
                    return comparison;
                }
            }
            //Should never happen, means the file has the same name or an improper naming scheme
            return comparison;
        });
		//Run the appropriate schedulers
        runScheduler(paths, new FCFS());
        runScheduler(paths, new RR());
    }

    private static void runScheduler(List<Path> paths, Scheduler scheduler) throws IOException {
		//Runs all jobs in all paths for provided scheduler
        for (Path path : paths) {
            List<Job> jobs = new LinkedList<>();
            Files.readAllLines(path).forEach(line -> {
                String[] values = line.split(" ");
                if (values.length != 1) {
                    jobs.add(new Job(Integer.parseInt(values[0]), Integer.parseInt(values[1])));
                }
            });
            System.out.println(String.format("%s %s: %.0f", scheduler.getName(), path.getFileName().toString().split("\\.")[0], scheduler.run(jobs)));
        }
    }
}

class Job {
    int waitTime;
    int arrivalTime;
    int length;
    int progress;
    boolean inQueue;
    public Job (int arrivalTime, int length){
        this.arrivalTime = arrivalTime;
        this.length = length;
    }
}

//small interfacing to make the runScheduler method simple to write
interface Scheduler {
    double run(List<Job> jobs);
    String getName();
}


class FCFS implements Scheduler{

    @Override
    public double run(List<Job> jobs){
        int jobCount = jobs.size();
		//takes the list of jobs as a queue
        Queue<Job> q = (Queue<Job>) jobs;
        Job prev = null;
        double total = 0;
		
		//iterates of queue until everything is empty
        while(!q.isEmpty()){
            Job completed = q.remove();
            if(prev != null) {
                completed.waitTime = prev.waitTime + prev.length;
                total += completed.waitTime;
            }
            prev = completed;
        }

        return Math.ceil(total/jobCount);
    }

    @Override
    public String getName() {
        return "FCFS";
    }
}

class RR implements Scheduler{

    @Override
    public double run(List<Job> jobs) {
        int jobCount = jobs.size();
        Queue<Job> readyQueue = new LinkedList<>();
        int time = 0;
        double avgWaitTime = 0;

        while (!jobs.isEmpty()) {

            for(Job job: jobs) {
                if(job.arrivalTime == time) {
                    readyQueue.add(job);
                    job.inQueue = true;
                }
            }

            if (!readyQueue.isEmpty()) {
                Job currJob = readyQueue.remove();
                currJob.inQueue = false;

                //increase waitTime by 1 if job is in queue but not executing
                jobs.stream().filter(job -> job.inQueue).forEach(job -> job.waitTime++);
                currJob.progress++;

                if (currJob.length == currJob.progress) {
                    avgWaitTime += currJob.waitTime;
                    jobs.remove(currJob);
                } else {
                    readyQueue.add(currJob);
                    currJob.inQueue = true;
                }
            }
            time++;
        }
        return Math.ceil(avgWaitTime / jobCount);
    }

    @Override
    public String getName() {
        return "RR";
    }
}
