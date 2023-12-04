package hdd;

import data.Response;
import os.SimulationConfig;

import java.util.Arrays;
import java.util.Random;

public class HardDiskDrive {
    private final int tracks;
    private final int sectorsPerTrack;
    private final int trackShiftTime;
    private final int maxSeekTime;
    private final int rotationLatency;

    private final LBA lba;
    private final byte[][] sectors;
    private final Random random;

    private int currentTrack;
    private int currentSector;

    private HardDiskDrive() {
        tracks = SimulationConfig.TRACKS;
        sectorsPerTrack = SimulationConfig.SECTORS_PER_TRACK;
        trackShiftTime = SimulationConfig.TRACK_SHIFT_TIME;
        maxSeekTime = SimulationConfig.MAX_SEEK_TIME;
        rotationLatency = SimulationConfig.ROTATION_LATENCY;

        currentTrack = 0;
        lba = new LBA(tracks, sectorsPerTrack);

        sectors = new byte[tracks][sectorsPerTrack];
        for (byte[] sector : sectors) {
            Arrays.fill(sector, (byte) -1);
        }

        random = new Random(SimulationConfig.RANDOM_GENERATOR_SEED);
    }

    private static final class HardDriveDiskHolder {
        private static final HardDiskDrive INSTANCE = new HardDiskDrive();
    }

    public static HardDiskDrive getInstance() {
        return HardDriveDiskHolder.INSTANCE;
    }

    public Response read(int address) {
        return processIO(address, (byte) -1, true);
    }

    public Response write(int address, byte data) {
        return processIO(address, data, false);
    }

    public int[] add(int fileSize) {
        if (!containsFreeSpace(fileSize)) {
            return new int[]{-1};
        }

        currentTrack = random.nextInt(500);
        currentSector = random.nextInt(100);
        int writtenSectors = 0;

        if (fileSize == 1 && sectors[currentTrack][currentSector] == -1) {
            sectors[currentTrack][currentSector] = (byte) random.nextInt(127);
            return new int[]{lba.translateToAddress(currentTrack, currentSector)};
        }

        int[] addresses = new int[fileSize];

        for (int i = 0; i < fileSize; i++) {
            while (sectors[currentTrack][currentSector] != -1) {
                nextSector();
            }
            sectors[currentTrack][currentSector] = (byte) random.nextInt(127);
            addresses[i] = lba.translateToAddress(currentTrack, currentSector);
            writtenSectors++;

            if (writtenSectors <= 100) {
                nextSector();
            } else {
                currentTrack = random.nextInt(500);
                currentSector = random.nextInt(100);
                writtenSectors = 0;
            }
        }

        resetToStart();
        return addresses;
    }

    private int seek(int track) {
        int tracksPassed = Math.abs(currentTrack - track);
        int seekTime = tracksPassed * trackShiftTime;
        if ((track == 0 || track == tracks - 1) && seekTime > maxSeekTime) {
            seekTime = maxSeekTime;
        }
        currentTrack = track;
        return seekTime;
    }

    private Response processIO(int address, byte data, boolean isRead) {
        int[] coordinates = lba.translateToCoordinates(address);
        int track = coordinates[0];
        int sector = coordinates[1];
        int positionTime = 0;

        if (track != currentTrack) {
            positionTime = seek(track);
        }

        positionTime += rotationLatency;

        Response response = new Response(positionTime);

        if (isRead) {
            byte dataResult = sectors[track][sector];
            response.setData(dataResult);
        } else {
            sectors[track][sector] = data;
        }

        response.setAddress(address); // just for log

        return response;
    }

    private boolean containsFreeSpace(int n) {
        int count = 0;

        for (byte[] row : sectors) {
            for (int element : row) {
                if (element == -1) {
                    count++;
                    if (count >= n) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void nextSector() {
        if (currentSector == sectorsPerTrack - 1) {
            if (currentTrack == tracks - 1) {
                resetToStart();
            } else {
                currentTrack++;
                currentSector = 0;
            }
        } else {
            currentSector++;
        }
    }

    private void resetToStart() {
        currentTrack = 0;
        currentSector = 0;
    }
}