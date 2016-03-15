import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

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
        if (args.length != 3) {
            System.out.println("3 arguments required: N M P; consumer_ct, max_req_length, producer_max_sleep_duration");
            System.exit(0);
        }
        int consumerCount = Integer.parseInt(args[0]);
        int maxRequestLength = Integer.parseInt(args[1]);
        int prodSleepDur = Integer.parseInt(args[2]);

        Queue<Request> requests = new LinkedList<>();
        Thread producer = new Thread(new Producer(maxRequestLength, prodSleepDur, requests, consumerCount));
        producer.start();

        Thread[] consumers = new Thread[consumerCount];
        for (int i = 0; i < consumerCount; i++) {
            consumers[i] = new Thread(new Consumer(requests, i));
            consumers[i].start();
        }
    }
}

class Producer implements Runnable {
    final Queue<Request> requests;
    int consumerCount;
    int maxRequestLength;
    int prodSleepDur;

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
                synchronized (requests) {
                    while (requests.size() == consumerCount) {
                        requests.wait();
                    }
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

    public Consumer(Queue<Request> requests, int id) {
        this.requests = requests;
        this.id = id;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Request request;
                synchronized (requests) {
                    while (requests.isEmpty()) {
                        requests.wait();
                    }
                    request = requests.remove();
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
