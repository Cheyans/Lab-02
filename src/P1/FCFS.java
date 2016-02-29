package P1;

import java.util.*;


public class FCFS {  
	// Queue<Job> q = new LinkedList<Job>();
 //    Final String fileName; 
 //    List<Job> list;
    // public FCFS (String fileName, List<Job> list){
    //     this.fileName = finalName;
    //     this.list = list;
    // }

	public static int run(List<Job> list){
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
        double average = total/size;
        return average;
	}




}