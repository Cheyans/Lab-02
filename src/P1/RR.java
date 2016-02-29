package P1;

import java.util.*;


public class RR{

	public static int run(List<Job> jobs){
        Queue<Job> readyQueue = new LinkedList<Job>();
        int time = 0;
        Boolean jobsDone = false;
        int avgWaitTime = 0;

        while(!jobsDone){

        	for(int i = 0; i < jobs.size(); i++){ //check if job has arrived
				if(jobs.get(i).arrivalTime == time){
					readyQueue.add(jobs.get(i));
					jobs.get(i).inQueue = true;
				}
			}

			Job currJob = readyQueue.remove();
			currJob.inQueue =false;

			for (int i = 0; i < jobs.size(); i++) { //increase waitTime by 1 if job is in queue but not executing
				if(jobs.get(i).inQueue){
					jobs.get(i).waitTime++;
				}
			}

			currJob.progress++;

			if(currJob.length == currJob.progress){
				avgWaitTime += currJob.waitTime;
			}
			else{
				readyQueue.add(currJob);
				currJob.inQueue = true;
			}

	        jobsDone = true;
			for(int i = 0; i < jobs.size(); i++){ //check if all jobs have finished
				if(jobs.get(i).length != 0){
					jobsDone = false;
				}
			}
			time++;
		}

		avgWaitTime = avgWaitTime/jobs.size();
		
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