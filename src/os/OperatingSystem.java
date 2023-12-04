package os;

import data.Request;
import data.Response;
import os.diskscheduler.CLOOK;
import os.diskscheduler.DiskScheduler;
import os.diskscheduler.FCFS;
import os.diskscheduler.SSTF;
import os.process.Process;
import os.process.ProcessStatus;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class OperatingSystem implements IOperatingSystem {
    private final int maxRequestNumber;

    private int requestCount;
    private int requestPerSecondCount;
    private int responseCount;

    private final Queue<Process> runQueue;
    private final Set<Process> sleepQueue;
    private final ArrayList<Response> responses;

    private final DiskScheduler diskScheduler;

    private int sysTime;

    private Process currentProcess;

    public OperatingSystem() {
        maxRequestNumber = SimulationConfig.REQUESTS_NUMBER;

        int processNumber = SimulationConfig.PROCESSES_NUMBER;
        runQueue = initializeProcesses(processNumber);
        sleepQueue = new HashSet<>(processNumber);
        responses = new ArrayList<>();

        diskScheduler = switch (SimulationConfig.ALGORITHM) {
            case FCFS -> new FCFS(SimulationConfig.MAX_QUEUE_CAPACITY, this);
            case SSTF -> new SSTF(SimulationConfig.MAX_QUEUE_CAPACITY, this);
            case CLOOK -> new CLOOK(SimulationConfig.MAX_QUEUE_CAPACITY, this);
        };
    }

    public void start() {
        currentProcess = peekProcess(); // take process from queue
        while (requestCount < maxRequestNumber) {
            System.out.println(sysTime + "ms: NEW STEP OF CYCLE.");
            System.out.println(sysTime + "ms: SLEEP QUEUE SIZE: " + sleepQueue.size());

            ProcessStatus status = currentProcess.work();

            if (status == ProcessStatus.WORK_AFTER_RESPONSE_PRECESSING) {
                int nextSysTime = sysTime + currentProcess.getLastOperationTime();
                skipToTime(nextSysTime);
            }

            switch (status) {
                case WORK_AFTER_REQUEST, WORK_AFTER_RESPONSE_PRECESSING -> {
                    if (currentProcess.getWorkTime() >= SimulationConfig.PROCESSOR_TIME_QUANTUM) {
                        nextProcess(false);
                    }
                }
                case WAIT_FOR_QUEUE_SPACE -> {
                    System.out.println(sysTime + "ms: QUEUE IS FULL. WAIT FOR FREE SPACE.");

                    int previousSysTime = sysTime;
                    if (diskScheduler.getDiskReleaseTime() < sysTime) {
                        throw new RuntimeException("Time synchronisation error during waiting.");
                    }
                    int nextSysTime = diskScheduler.getDiskReleaseTime(); // queue is full, wait for free space
                    skipToTime(nextSysTime);

                    if (nextSysTime - previousSysTime >= 1000) {
                        requestPerSecondCount = 0;
                    }
                }
                case WAIT_FOR_RESPONSE -> nextProcess(true);
            }

            processResponseQueue();

            if (requestPerSecondCount == SimulationConfig.REQUESTS_PER_SECOND) {
                System.out.println(sysTime + "ms: REACH MAX REQUEST PER SECOND. REQUEST COUNT: " + requestCount);

                int nextSysTime = sysTime + 1000;
                skipToTime(nextSysTime);
                requestPerSecondCount = 0;
                System.out.println(sysTime + "ms: NEXT SECOND.");
            }

            if (sysTime >= diskScheduler.getDiskReleaseTime()) {
                int nexTime = sysTime;
                sysTime = diskScheduler.getDiskReleaseTime();
                skipToTime(nexTime);
            }

            System.out.println(sysTime + "ms: SLEEP QUEUE SIZE: " + sleepQueue.size());
        }

        System.out.println(sysTime + "ms: ALL REQUESTS WAS SENT. REQUESTS COUNT: " + requestCount);

        while (responseCount != requestCount) { // process the requests that remained in the queue
            skipToTime(diskScheduler.getDiskReleaseTime());
        }

        System.out.println("FULL PROCESSING TIME: " + sysTime + "ms.");
    }

    private Queue<Process> initializeProcesses(int processNumber) {
        Queue<Process> processes = new ArrayDeque<>(processNumber);
        for (int i = 0; i < processNumber; i++) {
            processes.offer(new Process(i, this));
        }
        return processes;
    }

    private void nextProcess(boolean sleepCurrent) {
        System.out.println(sysTime + "ms: SLEEP: " + sleepCurrent + ". CURRENT PROCESS HAS FINISHED WORK: " + currentProcess);
        currentProcess.resetWorkTime();
        if (sleepCurrent) {
            sleepQueue.add(currentProcess);
        } else {
            runQueue.offer(currentProcess);
        }

        if (runQueue.isEmpty()) { // all processes wait fo responses
            System.out.println(sysTime + "ms: ALL PROCESSES WAIT FOR RESPONSE.");

            int minResponseExecutionTime = Integer.MAX_VALUE;
            for (Response response : responses) {
                if (response.getExecutionTime() < minResponseExecutionTime) {
                    minResponseExecutionTime = response.getExecutionTime();
                }
            }
            int previousSysTime = sysTime;
            int nextTime = minResponseExecutionTime;
            skipToTime(nextTime);

            if (sysTime - previousSysTime >= 1000) {
                requestPerSecondCount = 0;
            }

            System.out.println(previousSysTime + "ms: SKIP TO SYS_TIME: " + sysTime + "ms.");

            processResponseQueue();
        }

        if (!runQueue.isEmpty()) {
            currentProcess = peekProcess();
        }

        System.out.println(sysTime + "ms: NEXT PROCESS: " + currentProcess);
    }

    private Process peekProcess() {
        return runQueue.remove();
    }

    private Process getProcessById(int id) {
        if (id == currentProcess.getId()) {
            return currentProcess;
        }

        for (Process process : sleepQueue) {
            if (process.getId() == id) {
                sleepQueue.remove(process);
                runQueue.add(process);
                return process;
            }
        }

        for (Process process : runQueue) {
            if (process.getId() == id) {
                return process;
            }
        }

        throw new RuntimeException("Process doesn't exist.");
    }

    private void processResponseQueue() {
        Iterator<Response> iterator = responses.iterator();
        while (iterator.hasNext()) {
            Response response = iterator.next();
            if (response.getExecutionTime() <= sysTime) {
                Process process = getProcessById(response.getProcessId());
                process.addResponse(response);
                iterator.remove();
            }
        }
    }

    private void skipToTime(int nextSysTime) {
        int diskReleaseTime = diskScheduler.getDiskReleaseTime();
        if (diskReleaseTime >= sysTime && diskReleaseTime <= nextSysTime) {
            sysTime = diskReleaseTime;
            System.out.println(sysTime + "ms: DO DISK SCHEDULING.");
            diskScheduler.schedule();
            if (diskScheduler.getQueueSize() > 0) {
                skipToTime(nextSysTime);
            }
        }
        sysTime = nextSysTime;
    }

    private void writeLog(Response response) {
        try (FileWriter fileWriter = new FileWriter(SimulationConfig.RESPONSES_LOG_PATH, true)) {
            fileWriter.append(response.toString()).append("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean request(Request request) {
        if (request.getProcessId() != currentProcess.getId()) {
            throw new RuntimeException("Synchronization error between processes.");
        }

        int nextSysTime = sysTime + currentProcess.getLastOperationTime();
        skipToTime(nextSysTime);

        System.out.println(sysTime + "ms: TRY TO ADD NEW REQUEST.");

        boolean status = diskScheduler.add(request);

        if (status) {
            requestCount += 1;
            requestPerSecondCount += 1;
            if (requestCount == 1) {
                System.out.println(sysTime + "ms: DO DISK SCHEDULING.");
                diskScheduler.schedule();
            }
            System.out.println(sysTime + "ms: REQUESTS NUMBER: " + requestCount);
        }

        return status;
    }

    @Override
    public void response(Response response) {
        responseCount += 1;
        System.out.println(sysTime + "ms | " + response.getExecutionTime() + "ms: RECEIVED NEW RESPONSE: " + response + ".\nRESPONSE COUNT: " + responseCount);
        writeLog(response);
        if (sysTime >= response.getExecutionTime()) {
            Process process = getProcessById(response.getProcessId());
            process.addResponse(response);
            return;
        }
        responses.add(response);
    }

    @Override
    public int getSysTime() {
        return sysTime;
    }
}
