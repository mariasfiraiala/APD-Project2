/* Implement this class. */

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyHost extends Host {

    private PriorityBlockingQueue<Task> pq;
    private Task executingTask;

    private boolean isExecuting ;
    private final Object lock;
    private AtomicBoolean hasFinished;

    public MyHost() {
        super();
        this.pq = new PriorityBlockingQueue<Task>(1, new CustomComparator());
        this.executingTask = null;
        this.isExecuting = false;
        this.lock = new Object();
        this.hasFinished = new AtomicBoolean(false);
    }

    private void execute() {
        if (!pq.isEmpty()) {
            synchronized (lock) {
                executingTask = pq.poll();
                isExecuting = true;

                Instant start = Instant.now();
                while (true) {
                    Instant end = Instant.now();
                    Duration timeElapsed = Duration.between(start, end);
                    if (timeElapsed.toMillis() >= executingTask.getLeft()) {
                        break;
                    }
                }

                executingTask.setLeft(0);
                executingTask.finish();
                isExecuting = false;
                executingTask = null;
            }
        }
    }

    @Override
    public void run() {
        while (!hasFinished.get()) {
            execute();
        }
    }

    public boolean isExecuting() {
        return isExecuting;
    }

    @Override
    public void addTask(Task task) {
        pq.add(task);
    }

    @Override
    public int getQueueSize() {
        return pq.size();
    }

    @Override
    public long getWorkLeft() {
        long workLeft = 0;
        for (Task t : pq) {
            workLeft += t.getLeft();
        }

        workLeft += isExecuting() ? executingTask.getLeft() : 0;

        return workLeft;
    }

    @Override
    public void shutdown() {
        hasFinished.set(true);
    }
}

class CustomComparator implements Comparator<Task> {
    public int compare(Task t1, Task t2){
        if (t1.getPriority() < t2.getPriority()) {
            return 1;
        } else if (t1.getPriority() > t2.getPriority()) {
            return -1;
        }
        return 0;
    }
}

