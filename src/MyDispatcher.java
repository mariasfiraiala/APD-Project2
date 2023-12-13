/* Implement this class. */

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MyDispatcher extends Dispatcher {
    private AtomicInteger lastWorker;

    public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
        super(algorithm, hosts);
        this.lastWorker = new AtomicInteger(-1);
    }

    @Override
    public void addTask(Task task) {
        switch (algorithm) {
            case ROUND_ROBIN -> RoundRobin(task);
            case SHORTEST_QUEUE -> ShortestQueue(task);
            case SIZE_INTERVAL_TASK_ASSIGNMENT -> SizeIntervalTaskAssignment(task);
            case LEAST_WORK_LEFT -> LeastWorkLeft(task);
        }
    }

    private void RoundRobin(Task task) {
        hosts.get((lastWorker.addAndGet(1)) % hosts.size()).addTask(task);
    }

    private void ShortestQueue(Task task) {
        int minQueue = Integer.MAX_VALUE;
        Host minHost = new MyHost();

        for (Host h : hosts) {
            int tasks = h.getQueueSize();
            tasks += ((MyHost)h).isExecuting() ? 1 : 0;

            if (tasks < minQueue) {
                minQueue = tasks;
                minHost = h;
            }
        }

        minHost.addTask(task);
    }

    private void SizeIntervalTaskAssignment(Task task) {
        switch (task.getType()) {
            case SHORT -> hosts.get(0).addTask(task);
            case MEDIUM -> hosts.get(1).addTask(task);
            case LONG -> hosts.get(2).addTask(task);
        }
    }

    private void LeastWorkLeft(Task task) {
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

