package hdd;

public class LBA {
    private final int tracks;
    private final int sectorsPerTrack;

    public LBA(int tracks, int sectorsPerTrack) {
        this.tracks = tracks;
        this.sectorsPerTrack = sectorsPerTrack;
    }

    public int translateToAddress(int track, int sector) {
        if (track < 0 || track >= tracks || sector < 0 || sector >= sectorsPerTrack) {
            throw new IllegalArgumentException("Wrong sector coordinates.");
        }

        return track * sectorsPerTrack + sector;
    }

    public int[] translateToCoordinates(int address) {
        if (address < 0 || address >= tracks * sectorsPerTrack) {
            throw new IllegalArgumentException("Wrong address.");
        }

        int trackNumber = address / sectorsPerTrack;
        int sectorNumber = address % sectorsPerTrack;

        return new int[]{trackNumber, sectorNumber};
    }
}
