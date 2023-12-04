package os.diskscheduler;

import data.Request;
import os.IOperatingSystem;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * SSTF - Shortest Seek Time First
 */
public class SSTF extends DiskScheduler {
    private int lastAddress;

    public SSTF(int queueCapacity, IOperatingSystem os) {
        super(queueCapacity, os);

        Comparator<Request> comparator = (o1, o2) -> {
            int o1Distance = Math.abs(o1.getAddress() - lastAddress);
            int o2Distance = Math.abs(o2.getAddress() - lastAddress);
            return Integer.compare(o1Distance, o2Distance);
        };

        queue = new PriorityQueue<>(queueCapacity, comparator);
    }

    @Override
    protected Request nextRequest() {
        Request request = queue.remove();
        lastAddress = request.getAddress();
        return request;
    }


}
