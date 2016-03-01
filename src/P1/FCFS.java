package P1;

import java.util.List;
import java.util.Queue;

public class FCFS {
    public static double run(List<Job> list){
        int jobCount = list.size();
        Queue<Job> q = (Queue<Job>) list;
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
}