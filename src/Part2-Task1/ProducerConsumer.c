#include <stdio.h>
#include <pthread.h>
#include <stdlib.h>
#include <semaphore.h>
#include <unistd.h>
#include <time.h>

typedef struct request {
	int id;
	int length;
}request;

int head = -1;
int tail;
request **queue;
int size;

sem_t mutex;
sem_t fill_ct;
sem_t empty_ct;

int max_sleep;
int max_req_len;

void enqueue(request *request) {
	if(head != -1) {
		if(tail + 1 < size) {
			tail++;
			queue[tail] = request;
		} else {
			tail = 0;
			queue[tail] = request;	
		}
	} else {
		head = 0;
		queue[head] = request; 
		queue[tail] = request;	
	}
}

request *dequeue(){
	request *temp = queue[head];
	queue[head] = NULL;
	if(head + 1 < size)
		head++;
	else
		head = 0;
	return temp;
}


void master_thread() {
	int id = 0;
	while(1) {
		request *new_req = (request *) malloc(sizeof(request));
		new_req->id = id++;
		new_req->length = rand() % (max_req_len) + 1;
		sem_wait(&empty_ct);
		sem_wait(&mutex);
		enqueue(new_req);
		printf("Producer: produced request ID %d, length %d seconds at time %u\n", new_req->id, new_req->length, (unsigned)time(NULL));
		sem_post(&mutex);
		sem_post(&fill_ct);			
		int sec = rand() % (max_sleep) + 1;
		printf("Producer: sleeping for %d seconds\n", sec);
		sleep(sec);
	}		
}

void slave_thread(void *t_id) {
	while(1) {
		sem_wait(&fill_ct);
		sem_wait(&mutex);
		request *req = dequeue();
		printf("Consumer %d: assigned request ID %d, processing request for the next %d seconds, current time is %u\n", *(int *) t_id, req->id, req->length, (unsigned) time(NULL));
		sem_post(&mutex);
		sem_post(&empty_ct);			
		sleep(req->length);
		printf("Consumer %d: completed request ID %d at time %u\n", *(int *) t_id, req->id, (unsigned) time(NULL));
	}		
}

int main(int argc, char *argv[]) {
	if(argc != 4) {
		printf("3 arguments required: N M P; consumer_ct, max_req_length, producer_max_sleep_duration\n");
		exit(1);
	}
	
	size = atoi(argv[1]);
	queue = (request **) malloc(sizeof(request *) * size);

	sem_init(&mutex, 0, 1);
	sem_init(&fill_ct, 0, size);
	for(int i = size; i>0; i--) {
		sem_wait(&fill_ct);
	}
	
	sem_init(&empty_ct, 0, size);

	max_req_len = atoi(argv[2]);
	max_sleep = atoi(argv[3]);

	pthread_t master;
	pthread_t slaves[size];
	int *ids[size];

	pthread_create(&master, NULL, (void *) &master_thread, NULL);
	
	for(int i = 0; i < size; i++) {
		ids[i] = (int *) malloc(sizeof(int));
		*ids[i] = i;
		pthread_create(&slaves[i], NULL, (void *) &slave_thread, (void *) ids[i]);
	}

	pthread_join(master, NULL);

	for(int i = 0; i < size; i++) {
		pthread_join(slaves[i], NULL);
	}
}

