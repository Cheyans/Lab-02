package P1;

public class Job {
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