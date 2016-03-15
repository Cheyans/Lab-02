import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

//class of requests, only need an ID and a length of the request 
class Request {
    int length;
    int id;

    Request(int id, int length) {
        this.id = id;
        this.length = length;
    }
}

public class Main {
    public static void main(String[] args) throws Exception {
    	//Impute must be with 3 parameters!
        if (args.length != 3) {
            System.out.println("3 arguments required: N M P; consumer_ct, max_req_length, producer_max_sleep_duration");
            System.exit(0);
        }
        //save the parameters to local variables
        int consumerCount = Integer.parseInt(args[0]);
        int maxRequestLength = Integer.parseInt(args[1]);
        int prodSleepDur = Integer.parseInt(args[2]);

        //A queue of requests that is used by producer and consumers
        Queue<Request> requests = new LinkedList<>();
        //creation and running of producer thread and consumer thread
        Thread producer = new Thread(new Producer(maxRequestLength, prodSleepDur, requests, consumerCount));
        producer.start();

        Thread[] consumers = new Thread[consumerCount];
        //made an array of consumer threads to ID the consumer threads
        for (int i = 0; i < consumerCount; i++) {
            consumers[i] = new Thread(new Consumer(requests, i));
            consumers[i].start();
        }
    }
}
//producer implementation
class Producer implements Runnable {
    final Queue<Request> requests;
    int consumerCount;
    int maxRequestLength;
    int prodSleepDur;
    //must take in the maximum request length, how long to sleep for, the request queue and how many consumers 
    //queue is "full" when it gets to the size of the number of consumers
    public Producer(int maxRequestLength, int prodSleepDur, Queue<Request> requests, int consumerCount) {
        this.maxRequestLength = maxRequestLength;
        this.prodSleepDur = prodSleepDur;
        this.requests = requests;
        this.consumerCount = consumerCount;
    }

    @Override
    public void run() {
        int id = 0;
        while (true) {
            Request request = new Request(id++, (int) (Math.random() * maxRequestLength) + 1);
            try {
            	//synchronization on the request queue
                synchronized (requests) {
                	//thread will stall here until there is space to enqueue a new request
                    while (requests.size() == consumerCount) {
                        requests.wait();
                    }
                    //enqueue new request and notify the threads 
                    requests.add(request);
                    requests.notifyAll();
                }
                System.out.printf("Producer: produced request ID %d, length %d seconds at time %d\n",
                        request.id, request.length, TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                System.out.printf("Producer: sleeping for %d seconds\n", TimeUnit.MILLISECONDS.toSeconds(request.length));
                Thread.sleep(request.length);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Consumer implements Runnable {
    final Queue<Request> requests;
    int id;
    //consumer is simpler, it only needs to take in the requests queue and an id for itself. 
    public Consumer(Queue<Request> requests, int id) {
        this.requests = requests;
        this.id = id;
    }

    @Override
    public void run() {
        while (true) {
            try {
            	//synchronize upon the requests
                Request request;
                synchronized (requests) {
                	//if producer has yet to make a request, then the thread will wait until a request has been made 
                    while (requests.isEmpty()) {
                        requests.wait();
                    }
                    //as soon as a request has been made, this thread will have exclusive control of queue if it reaches this point
                    request = requests.remove();
                    //remove the next request and notify, also report on the request that was removed
                    requests.notifyAll();
                }
                System.out.printf("Consumer %d: assigned request ID %d, processing request for the next %d seconds, current time is %d\n",
                        this.id, request.id, request.length, TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                long sleep = (long) (Math.random() * request.length * 1000) + 1;
                System.out.printf("Producer: sleeping for %d seconds\n", TimeUnit.MILLISECONDS.toSeconds(sleep));
                Thread.sleep(sleep);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
