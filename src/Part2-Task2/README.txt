For our java implementation, we first start by creating a Request object that has a length and id field. We then take in consumer_ct, max_req_length, producer_max_sleep_duration from the user. We then create a requests queue and a producer thread with the maxRequestLength, prodSleepDur and consumerCount from the user, and the requests queue.

The producer thread goes through each request and if the size of the requests is the same as consumerCounter, the thread is told to wait. Once it is done waiting, the request is added to the request queue and notifies every other thread about it.

Then a consumer thread is initialized. A for loop that starts at zero and iterates through until it reaches the consumerCount. Each iteration creates a new consumer thread.

The consumer thread checks if the requests queue is empty and if it is, it waits until it isn't. The consumer then removes a request and notifies all other threads about it.