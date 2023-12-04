package os.filesystem;

import hdd.HardDiskDrive;
import os.SimulationConfig;

import java.util.Random;

public class FileSystem {
    private final File[] files;
    private final Random random;

    private FileSystem() {
        files = new File[SimulationConfig.FILES_NUMBER];
        random = new Random(SimulationConfig.RANDOM_GENERATOR_SEED);

        initializeFileSystem();
    }

    private static final class FileSystemHolder {
        private static final FileSystem INSTANCE = new FileSystem();
    }

    public static FileSystem getInstance() {
        return FileSystem.FileSystemHolder.INSTANCE;
    }

    public File getFile(int position) {
        if (position < 0 || position > files.length) {
            position = 0;
        }
        return files[position];
    }

    public File getRandomFile() {
        return getFile(random.nextInt(files.length));
    }

    public File[] getFiles() {
        return files;
    }

    private void initializeFileSystem() {
        int maxFileSize = SimulationConfig.MAX_FILE_SIZE;
        int fileNumber = SimulationConfig.FILES_NUMBER;

        if (fileNumber > Math.round((float) (SimulationConfig.TRACKS * SimulationConfig.SECTORS_PER_TRACK) / maxFileSize)) {
            System.out.printf("WARNING: %d files may not fit on the disk.%n", fileNumber);
        }

        HardDiskDrive hardDiskDrive = HardDiskDrive.getInstance();

        for (int i = 0; i < fileNumber; i++) {
            int fileSize = random.nextInt(maxFileSize);
            int[] addresses = hardDiskDrive.add(fileSize);

            if (addresses[0] == -1) {
                throw new RuntimeException("No more free space.");
            }

            files[i] = new File(addresses);
        }
    }
}
