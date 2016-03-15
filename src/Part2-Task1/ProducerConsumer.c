#include <stdio.h>
#include <pthread.h>
#include <stdlib.h>
#include <semaphore.h>

typedef struct request {
	int id;
	int length;
}request;

typedef struct master_args {
	int max_sleep;
	int max_req_len;
}master_args;

typedef struct thread_args {
	int consumer_ct;
	sem_t *mutex;
	sem_t *fill_ct;
	sem_t *empty_ct;
	master_args *m_args;
	request *request_queue[];
}thread_args;

void master_thread(thread_args *args) {
	int id = 0;
	while(1) {
		request *new_req = (request *) malloc(sizeof(request));
		sem_wait(args->empty_ct);
		sem_wait(args->mutex);
			
	}		
}

void slave_thread(thread_args *args) {

}

int main(int argc, char *argv[]) {
	if(argc != 4) {
		printf("3 arguments required: N M P; consumer_ct, max_req_length, producer_max_sleep_duration\n");
		exit(1);
	}
	
	int buffer_size = atoi(argv[1]);
	master_args *m_args = (master_args *) malloc(sizeof(master_args) + sizeof(request) * buffer_size);
	thread_args *args = (thread_args *) malloc(sizeof(thread_args));
	
	sem_t *mutex;	
	sem_t *fill_ct;
	sem_t *empty_ct;
	sem_init(mutex, 0, 1);
	sem_init(fill_ct, 0, buffer_size);
	sem_init(empty_ct, 0, buffer_size);

	m_args->max_req_len = atoi(argv[2]);
	m_args->max_sleep = atoi(argv[3]);

	args->fill_ct = fill_ct;
	args->empty_ct = empty_ct;	
	args->consumer_ct = buffer_size;

	pthread_t master;
	pthread_t slaves[buffer_size];

	pthread_create(&master, NULL, (void *) &master_thread, args);
		
	for(int i = 0; i < buffer_size; i++) {
		pthread_create(&slaves[i], NULL, (void *) &slave_thread, args);
	}

	pthread_join(master, NULL);

	for(int i = 0; i < buffer_size; i++) {
		pthread_join(slaves[i], NULL);
	}
	
	free(m_args);
	free(args->request_queue);
	free(args);
	sem_destroy(fill_ct);
	sem_destroy(empty_ct);
}

