package P1;

import java.util.List;
import java.util.Queue;

public class FCFS implements Scheduler{

    @Override
    public double run(List<Job> jobs){
        int jobCount = jobs.size();
        Queue<Job> q = (Queue<Job>) jobs;
        Job prev = null;
        double total = 0;

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