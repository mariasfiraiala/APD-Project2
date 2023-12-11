/* Implement this class. */

import java.time.Duration;
import java.time.Instant;
import java.util.PriorityQueue;

public class MyHost extends Host {

    private PriorityQueue<Task> pq = new PriorityQueue<Task>();
    private Task executingTask;

    private boolean isExecuting = false;
    private static final Object lock = new Object();

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
                isExecuting = false;
            }
        }
    }

    @Override
    public void run() {
        while (true) {
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
    }
}
