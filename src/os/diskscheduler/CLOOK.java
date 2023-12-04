package os.diskscheduler;

import data.Request;
import os.IOperatingSystem;

import java.util.*;

public class CLOOK extends DiskScheduler {
    private int lastAddress;

    public CLOOK(int queueCapacity, IOperatingSystem os) {
        super(queueCapacity, os);

        queue = new ArrayDeque<>(queueCapacity);
    }

    @Override
    protected Request nextRequest() {
        List<Request> requests = new ArrayList<>(queue);
        requests.sort(Comparator.comparingInt(Request::getAddress));

        Request request = requests.stream()
                .filter(value -> value.getAddress() >= lastAddress)
                .findFirst()
                .orElse(requests.get(0));

        if (!queue.remove(request)) {
            throw new RuntimeException("Request doesn't exist.");
        }

        lastAddress = request.getAddress();
        return request;
    }

}
