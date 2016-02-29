import java.util.*;

private class Job {
	int waitTime;
    int arrivalTime;
    int length;
    String name;
    public Job (String name, int length, int arrivalTime){
        waitTime = 0;
        this.arrivalTime = arrivalTime;
        this.length = length;
        this.name = name;
    }
}

public class FCFS {
	Queue<Job> q = new LinkedList<Job>();
	public void run(){
		
	}




}