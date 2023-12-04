package data;

public class Request {
    private static int REQUEST_ID;
    private final RequestType type;
    private final int address;
    private final int processId;
    private final int requestId;
    private int requestCreationTime;
    private int enqueueTime;
    private byte data;

    public Request(RequestType type, int address, int processId) {
        this.type = type;
        this.address = address;
        this.processId = processId;
        requestId = REQUEST_ID++;
    }

    public int getRequestId() {
        return requestId;
    }

    public RequestType getType() {
        return type;
    }

    public int getAddress() {
        return address;
    }

    public int getProcessId() {
        return processId;
    }

    public void setRequestCreationTime(int requestCreationTime) {
        this.requestCreationTime = requestCreationTime;
    }

    public int getRequestCreationTime() {
        return requestCreationTime;
    }

    public int getEnqueueTime() {
        return enqueueTime;
    }

    public void setEnqueueTime(int enqueueTime) {
        this.enqueueTime = enqueueTime;
    }

    public void setData(byte data) {
        this.data = data;
    }

    public byte getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestId=" + requestId +
                ", processId=" + processId +
                ", type=" + type +
                ", requestCreationTime=" + requestCreationTime +
                ", enqueueTime=" + enqueueTime +
                ", address=" + address +
                ", data=" + data +
                '}';
    }
}
