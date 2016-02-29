package P1;

import java.util.*;


public class RR{

	public static int run(List<Job> list){
        Queue<Job> readyQueue = new LinkedList<Job>();
        int time = 0;
        Boolean jobsDone = false;
        int avgWaitTime = 0;

        while(!jobsDone){

        	for(int i = 0; i < jobs.length; i++){ //check if job has arrived
				if(jobs[i].arrivalTime == time){
					readyQueue.add(jobs[i]);
					jobs[i].inQ = true;
				}
			}

			Job currJob = readyQueue.remove();
			currJob.inQ =false;

			for (int i = 0; i < jobs.length; i++) { //increase waitTime by 1 if job is in queue but not executing
				if(jobs[i].inQ){ 
					jobs[i].waitTime++;
				}
			}

			currJob.progress++;

			if(currJob.runTime == currJob.progress){
				avgWaitTime += currJob.waitTime;
			}
			else{
				readyQueue.add(currJob);
				currJob.inQ = true;
			}

	        jobsDone = true;
			for(int i = 0; i < jobs.length; i++){ //check if all jobs have finished
				if(jobs[i].runTime != 0){
					jobsDone = false;
				}
			}
			time++;
		}

		avgWaitTime = avgWaitTime/jobs.length;
		
		return avgWaitTime;

	}


	public static void main(String [] args){

		Job jobA = new Job("A", 10, 0);
		Job jobB = new Job("B", 8, 4);
		Job jobC = new Job("C", 3, 7);

		List<Job> jobs = new List<Job>(jobA, jobB, jobC);

		System.out.println(run(jobs));

	}


}