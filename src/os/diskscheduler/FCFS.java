package os.diskscheduler;

import data.Request;
import os.IOperatingSystem;

import java.util.ArrayDeque;

/**
 * FCFS - First Come First Served
 */
public class FCFS extends DiskScheduler {
    public FCFS(int queueCapacity, IOperatingSystem os) {
        super(queueCapacity, os);
        queue = new ArrayDeque<>(queueCapacity);
    }

    @Override
    protected Request nextRequest() {
        return queue.remove();
    }


}
