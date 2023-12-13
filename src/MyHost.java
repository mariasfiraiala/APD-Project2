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
    private AtomicBoolean hasFinished;
    private AtomicBoolean biggerPriority;

    public MyHost() {
        super();
        this.pq = new PriorityBlockingQueue<Task>(1, new CustomComparator());
        this.executingTask = null;
        this.isExecuting = false;
        this.hasFinished = new AtomicBoolean(false);
        this.biggerPriority = new AtomicBoolean(false);
    }

    private void execute() {
        if (!pq.isEmpty()) {
            executingTask = pq.poll();
            isExecuting = true;

            while (executingTask.getLeft() > 0 && !biggerPriority.get()) {
                try {
                    sleep(5, 0);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                executingTask.setLeft(executingTask.getLeft() - 5);
            }

            if (executingTask.getLeft() > 0) {
                pq.add(executingTask);
            } else {
                executingTask.finish();
            }
            biggerPriority.set(false);
            isExecuting = false;
            executingTask = null;
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
        if (executingTask != null && executingTask.isPreemptible() && task.getPriority() > executingTask.getPriority()) {
            biggerPriority.set(true);
        }
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

        if (t1.getStart() > t2.getStart()) {
            return 1;
        } else if (t1.getStart() < t2.getStart()) {
            return -1;
        }
        return 0;
    }
}

