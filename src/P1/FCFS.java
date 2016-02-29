package P1;

import java.util.*;

public class FCFS {

	public static double run(List<Job> list){
        Queue<Job> q = new LinkedList<Job>();
        for(Job jobs : list)
            q.add(jobs);
        //all jobs added to q in order
		while(!q.isEmpty()){
            Job current = q.remove();
            for(Job job : q){
                job.waitTime += current.length;
            }
        }
        int total = 0;
        for(Job job : list)
            total += job.waitTime;
        double size = (double) list.size();
		return total/size;
	}




}