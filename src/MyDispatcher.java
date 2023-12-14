/* Implement this class. */

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MyDispatcher extends Dispatcher {
    private final AtomicInteger lastWorker;

    public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
        super(algorithm, hosts);
        this.lastWorker = new AtomicInteger(-1);
    }

    @Override
    public void addTask(Task task) {
        switch (algorithm) {
            case ROUND_ROBIN -> roundRobin(task);
            case SHORTEST_QUEUE -> shortestQueue(task);
            case SIZE_INTERVAL_TASK_ASSIGNMENT -> sizeIntervalTaskAssignment(task);
            case LEAST_WORK_LEFT -> leastWorkLeft(task);
        }
    }

    private void roundRobin(Task task) {
        hosts.get((lastWorker.addAndGet(1)) % hosts.size()).addTask(task);
    }

    private void shortestQueue(Task task) {
        int minQueue = Integer.MAX_VALUE;
        Host minHost = new MyHost();

        for (Host h : hosts) {
            int tasks = h.getQueueSize();

            if (tasks < minQueue) {
                minQueue = tasks;
                minHost = h;
            }
        }

        minHost.addTask(task);
    }

    private void sizeIntervalTaskAssignment(Task task) {
        switch (task.getType()) {
            case SHORT -> hosts.get(0).addTask(task);
            case MEDIUM -> hosts.get(1).addTask(task);
            case LONG -> hosts.get(2).addTask(task);
        }
    }

    private void leastWorkLeft(Task task) {
        long minWork = Long.MAX_VALUE;
        Host minHost = null;

        for (Host h : hosts) {
            if (h.getWorkLeft() < minWork) {
                minWork = h.getWorkLeft();
                minHost = h;
            }
        }

        minHost.addTask(task);
    }
}

