package os.diskscheduler;

import data.RequestType;
import data.Response;
import hdd.HardDiskDrive;
import data.Request;
import os.IOperatingSystem;

import java.util.Queue;

public abstract class DiskScheduler {
    protected final int queueCapacity;
    protected int diskReleaseTime;

    protected Queue<Request> queue;
    protected HardDiskDrive hardDiskDrive;
    protected final IOperatingSystem os;

    public DiskScheduler(int queueCapacity, IOperatingSystem os) {
        this.queueCapacity = queueCapacity;
        this.os = os;
        hardDiskDrive = HardDiskDrive.getInstance();
    }

    public int getQueueSize() {
        return queue.size();
    }

    public int getDiskReleaseTime() {
        return diskReleaseTime;
    }

    public boolean add(Request request) {
        if (queue.size() == queueCapacity) {
            System.out.println(os.getSysTime() + "ms: FAILED. QUEUE IS FULL.");
            System.out.println(os.getSysTime() + "ms: REQUESTS QUEUE SIZE: " + queue.size());
            return false;
        }

        request.setEnqueueTime(os.getSysTime());
        boolean status = queue.offer(request);

        System.out.println(os.getSysTime() + "ms: ADDED NEW REQUEST: " + request);
        System.out.println(os.getSysTime() + "ms: REQUESTS QUEUE SIZE: " + queue.size());

        return status;
    }

    public void schedule() {
        if (diskReleaseTime != os.getSysTime()) {
            System.out.println("SYS_TIME: " + os.getSysTime());
            System.out.println("DISK RELEASE TIME: " + diskReleaseTime);
            throw new RuntimeException("Time synchronisation error.");
        }

        if (queue.isEmpty()) {
            return;
        }

        Request request = nextRequest();
        Response response;

        if (request.getType() == RequestType.READ) {
            response = hardDiskDrive.read(request.getAddress());
        } else {
            response = hardDiskDrive.write(request.getAddress(), request.getData());
        }

        // diskReleaseTime == the completion time of the last request, i.e. the start time of the current request
        response.setDequeueTime(diskReleaseTime);
        response.setRequestType(request.getType());
        response.setRequestId(request.getRequestId());
        response.setProcessId(request.getProcessId());
        response.setEnqueueTime(request.getEnqueueTime());
        response.setRequestCreationTime(request.getRequestCreationTime());

        diskReleaseTime += response.getDiskProcessingTime();

        response.setExecutionTime(diskReleaseTime);
        response.setProcessingTime(diskReleaseTime - response.getEnqueueTime());

        os.response(response);
    }

    protected abstract Request nextRequest();
}
