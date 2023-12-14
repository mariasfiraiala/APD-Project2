Copyright 2023 Maria Sfiraiala (maria.sfiraiala@stud.acs.upb.ro)

# Load Balancer - Project2

## Description

### Dispatcher

The load balancer gets tasks via the `addTask()` method and, based on the algorithm, calls the appropriate method:

* `roundRobin()`: simply increment the id of the last worker that was assigned work

* `shortestQueue()`: query all hosts for the smallest queue

* `sizeIntervalTaskAssignment`: based on the size of the task, send it to the first, second or third host (we know for a fact that there are only 3 hosts)

* `leastWorkLeft()`: query all hosts for the smallest sum of milliseconds remained to be executed

> **Note**: The value that stores the id of the last host that was assigned work should be an `AtomicInteger` due to the fact that there are multiple `TaskGenerators` threads that send work to the load balancer.

### Host

The worker saves the tasks received from the load balancer in a `PriorityBlockingQueue` in order synchronize the addition and retrieval of data.
The `addTask()` method is used to both increase the work load of the host and preempt any "thread" (that can pe paused) in the scenario that a more important one arrives.

The preemption of the running "thread" is done via a flag, `biggerPriority`, that, once set, stops the busy waiting loop, adds the new task to the queue and, subsequently, reruns the scheduling algorithm.

The scheduling algorithm is simulated by the custom comparator associated to the `PriorityBlockingQueue` entity.

The time spent busy waiting is counted with the help of a `sleep(100)` call.
After this call ends, we retrieve 100 milliseconds from the remaining time of the current "thread".

> **Note**: Simulating the task's running time is hard, due to the fact that the while loop takes so little to execute and, as a result, querying the time results in near 0 values (for milliseconds).
A previous implementation  of mine (though logically speaking, fine) was faulty due to this exact reason:

```Java
Instant start = Instant.now();
while (executingTask.getLeft() > 0) {
    Instant end = Instant.now();
    Duration timeElapsed = Duration.between(start, end);
    start = Instant.now();
    executingTask.setLeft(executingTask.getLeft() - timeElapsed.toMillis());
}
```

Hence the approximation with the 100 milliseconds.

## Observations Regarding the Project

A very nice project, I really enjoyed working on it, except for the time spent debugging weird time approximation issues.
I wish the tests were more lenient in this regard.

I feel that now I understand the replicated workers model better, so GG, thanks for that!
