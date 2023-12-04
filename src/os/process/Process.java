package os.process;

import data.Request;
import data.RequestType;
import data.Response;
import os.IOperatingSystem;
import os.SimulationConfig;
import os.filesystem.File;
import os.filesystem.FileSystem;

import java.util.ArrayList;
import java.util.Random;

public class Process {
    private final ArrayList<Response> responses;
    private final ArrayList<Request> failedRequests;
    private final IOperatingSystem os;
    private final Random random;

    private final int id;
    private int workTime;
    private int lastOperationTime;
    private boolean isSequenceAddress;
    private int sequenceAddresses;

    private File lastFile;
    private RequestType lastRequestType;

    public Process(int id, IOperatingSystem os) {
        this.id = id;
        this.os = os;
        responses = new ArrayList<>();
        failedRequests = new ArrayList<>();
        random = new Random((long) SimulationConfig.RANDOM_GENERATOR_SEED * id + 11L * id);
    }

    public int getId() {
        return id;
    }

    public void resetWorkTime() {
        workTime = 0;
    }

    public int getWorkTime() {
        return workTime;
    }

    public int getLastOperationTime() {
        return lastOperationTime;
    }

    public void addResponse(Response response) {
        responses.add(response);
    }

    public ProcessStatus work() {
        lastOperationTime = 0;

        if (!responses.isEmpty()) {
            processResponse();
            return ProcessStatus.WORK_AFTER_RESPONSE_PRECESSING;
        }

        Request request = failedRequests.isEmpty() ? generateRequest() : failedRequests.remove(0);
        boolean success = os.request(request);


        if (!success) {
            failedRequests.add(request);
            return ProcessStatus.WAIT_FOR_QUEUE_SPACE;
        }

        if (request.getType() == RequestType.READ) {
            return ProcessStatus.WAIT_FOR_RESPONSE;
        }

        return ProcessStatus.WORK_AFTER_REQUEST;
    }

    private Request generateRequest() {
        RequestType requestType;
        File file;
        int address;

        if (isSequenceAddress) {
            requestType = lastRequestType;
            file = lastFile;
            address = file.getNextAddress();
            sequenceAddresses += 1;
            if (sequenceAddresses >= 15) {
                isSequenceAddress = random.nextInt(10) < 6 && file.getSize() > 150;
                if (!isSequenceAddress) {
                    sequenceAddresses = 0;
                }
            }
        } else {
            requestType = random.nextInt(10) < 5 ? RequestType.READ : RequestType.WRITE;
            file = FileSystem.getInstance().getRandomFile();
            address = file.getRandomAddress();

            isSequenceAddress = random.nextInt(10) < 5 && file.getSize() > 150;
            lastFile = file;
            lastRequestType = requestType;
        }

        Request request = new Request(requestType, address, id);
        request.setRequestCreationTime(os.getSysTime());

        lastOperationTime = requestType == RequestType.WRITE ? SimulationConfig.WRITE_REQUEST_CREATING_TIME : 0;

        if (requestType == RequestType.WRITE) {
            workTime += lastOperationTime;
            request.setRequestCreationTime(request.getRequestCreationTime() + lastOperationTime);
            request.setData((byte) random.nextInt(127));
        }

        return request;
    }

    private void processResponse() {
        Response response = responses.remove(0);
        if (response.getRequestType() == RequestType.READ) {
            lastOperationTime = SimulationConfig.READ_REQUEST_PROCESSING_TIME;
        } else {
            lastOperationTime = 0;
        }
        workTime += lastOperationTime;
    }

    @Override
    public String toString() {
        return "Process{" +
                "id=" + id +
                ", responses=" + responses +
                ", failedRequests=" + failedRequests +
                ", workTime=" + workTime +
                '}';
    }
}
