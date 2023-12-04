import os.OperatingSystem;
import os.SimulationConfig;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Main {

    public static void main(String[] args) {
        try (PrintWriter printWriter = new PrintWriter(SimulationConfig.RESPONSES_LOG_PATH)) {
            printWriter.write("");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        OperatingSystem operatingSystem = new OperatingSystem();
        operatingSystem.start();
    }
}
