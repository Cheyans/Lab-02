package P1;

public class Job {
	int waitTime;
    int arrivalTime;
    int length;
    int progress;
    boolean inQueue;
    public Job (int length, int arrivalTime){
        waitTime = 0;
        this.arrivalTime = arrivalTime;
        this.length = length;
    }
}