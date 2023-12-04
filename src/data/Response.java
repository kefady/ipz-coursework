package data;

public class Response {
    private final int diskProcessingTime;
    private int processingTime;
    private int requestCreationTime;
    private int enqueueTime;
    private int dequeueTime;
    private int executionTime;
    private int processId;
    private int requestId;
    private int address;
    private RequestType requestType;
    private byte data;

    public Response(int diskProcessingTime) {
        this.diskProcessingTime = diskProcessingTime;
    }

    public int getDiskProcessingTime() {
        return diskProcessingTime;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(int processingTime) {
        this.processingTime = processingTime;
    }

    public int getRequestCreationTime() {
        return requestCreationTime;
    }

    public void setRequestCreationTime(int requestCreationTime) {
        this.requestCreationTime = requestCreationTime;
    }

    public int getEnqueueTime() {
        return enqueueTime;
    }

    public void setEnqueueTime(int enqueueTime) {
        this.enqueueTime = enqueueTime;
    }

    public int getDequeueTime() {
        return dequeueTime;
    }

    public void setDequeueTime(int dequeueTime) {
        this.dequeueTime = dequeueTime;
    }

    public int getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(int executionTime) {
        this.executionTime = executionTime;
    }

    public int getProcessId() {
        return processId;
    }

    public void setProcessId(int processId) {
        this.processId = processId;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public byte getData() {
        return data;
    }

    public void setData(byte data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Response{" +
                "requestId=" + requestId +
                ", processId=" + processId +
                ", processingTime=" + processingTime +
                ", diskProcessingTime=" + diskProcessingTime +
                ", enqueueTime=" + enqueueTime +
                ", dequeueTime=" + dequeueTime +
                ", executionTime=" + executionTime +
                ", requestCreationTime=" + requestCreationTime +
                ", address=" + address +
                ", requestType=" + requestType +
                ", data=" + data +
                '}';
    }
}
