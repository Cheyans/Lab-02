package P1;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class RR {
    public static double run(List<Job> jobs) {
        int jobCount = jobs.size();
        Queue<Job> readyQueue = new LinkedList<>();
        int time = 0;
        double avgWaitTime = 0;

        while (!jobs.isEmpty()) {
            final int finalTime = time;
            jobs.stream().filter(job -> job.arrivalTime == finalTime).forEach(job -> {
                readyQueue.add(job);
                job.inQueue = true;
            });
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
}