package os;

public class SimulationConfig {
    // -----------------------------------------------------------------------------------------------------------------
    // HARD DRIVE DISK
    // -----------------------------------------------------------------------------------------------------------------
    /**
     * Number of tracks on the plate
     */
    public static final int TRACKS = 500;

    /**
     * Number of sectors on one track
     */
    public static final int SECTORS_PER_TRACK = 100;

    /**
     * Time of movement on one track by the drive mechanism from the current position (in milliseconds)
     */
    public static final int TRACK_SHIFT_TIME = 10;

    /**
     * Time for the drive mechanism to move from the first track to the outer track (closest to the outer edge of the plate)
     * or from the outer track to the first track (in milliseconds)
     */
    public static final int MAX_SEEK_TIME = 130;

    /**
     * Rotation delay time (in milliseconds)
     */
    public static final int ROTATION_LATENCY = 8;

    // -----------------------------------------------------------------------------------------------------------------

    // -----------------------------------------------------------------------------------------------------------------
    // FILE SYSTEM
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Amount of files in file system
     */
    public static final int FILES_NUMBER = 50;

    /**
     * Maximal file size in blocks (block size == sector size)
     */
    public static final int MAX_FILE_SIZE = 500;

    // -----------------------------------------------------------------------------------------------------------------

    // -----------------------------------------------------------------------------------------------------------------
    // OPERATING SYSTEM
    // -----------------------------------------------------------------------------------------------------------------
    /**
     * Seed for random module
     */

    public static final int RANDOM_GENERATOR_SEED = 13570;
    /**
     * The maximum number of processes in the system
     */
    public static final int PROCESSES_NUMBER = 10;

    /**
     * The maximum amount of time given to one process at a time (in milliseconds)
     */
    public static final int PROCESSOR_TIME_QUANTUM = 20;

    /**
     * The maximum number of requests
     */
    public static final int REQUESTS_NUMBER = 100000;

    /**
     * The number of requests per second
     */
    public static final int REQUESTS_PER_SECOND = 50;

    /**
     * The maximum size of the request queue
     */
    public static final int MAX_QUEUE_CAPACITY = 20;

    /**
     * The amount of time it takes to create a write request
     */
    public static final int WRITE_REQUEST_CREATING_TIME = 7;

    /**
     * The amount of time it takes to process a read request
     */
    public static final int READ_REQUEST_PROCESSING_TIME = 7;

    /**
     * Disk scheduling algorithm
     */
    public static final Algorithm ALGORITHM = Algorithm.CLOOK;

    /**
     * The path to the log file
     */
    public static final String RESPONSES_LOG_PATH = "src/logs/responses_" + ALGORITHM.name().toLowerCase() +".txt";

    // -----------------------------------------------------------------------------------------------------------------
}
