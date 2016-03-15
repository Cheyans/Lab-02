#include <stdio.h>
#include <pthread.h>
#include <stdlib.h>
#include <semaphore.h>
#include <unistd.h>
#include <time.h>

//request struct
typedef struct request {
	int id;
	int length;
}request;


//Global variables storing the current state of consumer producer
int head = -1;
int tail;
request **queue;
int size;

sem_t mutex;
sem_t fill_ct;
sem_t empty_ct;

int max_sleep;
int max_req_len;

//enqueue function that uses queue as a circular buffer and assumes
//there will never be requests enqueued when the queue is full because
//of the empty_ct semaphore
void enqueue(request *request) {
    //if head has been initialized
	if(head != -1) {
	    //if the next request added doesn't go over the bounds of the queue
		if(tail + 1 < size) {
		    //add the request and move the tail pointer
			tail++;
			queue[tail] = request;
		} else {
		    //add request to beginning of array and move tail pointer
			tail = 0;
			queue[tail] = request;	
		}
	} else {
	    //initialize head and tail to the first request
		head = 0;
		queue[head] = request; 
		queue[tail] = request;	
	}
}

//dequeue function that uses queue as a circular buffer and assumes
//there will never be requests dequeued when the queue is full because
//of the full_ct semaphore
request *dequeue(){
    //pops request off queue and sets that array index to null
	request *temp = queue[head];
	queue[head] = NULL;
	//if head pointer is not moved past array bounds
	if(head + 1 < size)
	    //increase head size
		head++;
	else
	    //move head to beginning of bounds
		head = 0;
	return temp;
}


void master_thread() {
	int id = 0;
	while(1) {
	    //create a new request
		request *new_req = (request *) malloc(sizeof(request));
		new_req->id = id++;
		new_req->length = rand() % (max_req_len) + 1;
		//wait for queue to have slot and acquire lock on array
		sem_wait(&empty_ct);
		sem_wait(&mutex);
		//queue request
		enqueue(new_req);
		printf("Producer: produced request ID %d, length %d seconds at time %u\n", new_req->id, new_req->length, (unsigned)time(NULL));
		//release queue and increase full count
		sem_post(&mutex);
		sem_post(&fill_ct);
		//sleep for random amount of time
		int sec = rand() % (max_sleep) + 1;
		printf("Producer: sleeping for %d seconds\n", sec);
		sleep(sec);
	}		
}

void slave_thread(void *t_id) {
	while(1) {
		//wait for queue to not be empty and acquire lock on array
		sem_wait(&fill_ct);
		sem_wait(&mutex);
		//pop request off queue
		request *req = dequeue();
		printf("Consumer %d: assigned request ID %d, processing request for the next %d seconds, current time is %u\n", *(int *) t_id, req->id, req->length, (unsigned) time(NULL));
		//release lock on queue and increase empty count
		sem_post(&mutex);
		sem_post(&empty_ct);
		//process request
		sleep(req->length);
		printf("Consumer %d: completed request ID %d at time %u\n", *(int *) t_id, req->id, (unsigned) time(NULL));
	}
}

int main(int argc, char *argv[]) {
	if(argc != 4) {
		printf("3 arguments required: N M P; consumer_ct, max_req_length, producer_max_sleep_duration\n");
		exit(1);
	}

	//create queue
	size = atoi(argv[1]);
	queue = (request **) malloc(sizeof(request *) * size);

    //initialize semaphore/mutexes to appropraite value
	sem_init(&mutex, 0, 1);
	sem_init(&fill_ct, 0, size);
	sem_init(&empty_ct, 0, size);
	//need to reset fill_ct to 0
	for(int i = size; i>0; i--) {
		sem_wait(&fill_ct);
	}

	max_req_len = atoi(argv[2]);
	max_sleep = atoi(argv[3]);

    //keep track of all threads
	pthread_t master;
	pthread_t slaves[size];
	int *ids[size];

    //create master thread
	pthread_create(&master, NULL, (void *) &master_thread, NULL);

	//create slave threads with appropriate ids
	for(int i = 0; i < size; i++) {
		ids[i] = (int *) malloc(sizeof(int));
		*ids[i] = i;
		pthread_create(&slaves[i], NULL, (void *) &slave_thread, (void *) ids[i]);
	}

    //join on the threads when they are finished (this never happens because infinite loop in both threads
	pthread_join(master, NULL);

	for(int i = 0; i < size; i++) {
		pthread_join(slaves[i], NULL);
	}
}

