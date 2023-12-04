package os.process;

public enum ProcessStatus {
    WORK_AFTER_REQUEST,
    WORK_AFTER_RESPONSE_PRECESSING,
    WAIT_FOR_QUEUE_SPACE,
    WAIT_FOR_RESPONSE
}
