/* Implement this class. */

import java.util.Comparator;
import java.util.PriorityQueue;

public class MyHost extends Host {

    private PriorityQueue<Task> pq = new PriorityQueue<Task>();
    private Task executingTask;

    private boolean isExecuting;

    @Override
    public void run() {
    }

    public boolean isExecuting() {
        return isExecuting;
    }

    public void setExecuting(boolean executing) {
        isExecuting = executing;
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
