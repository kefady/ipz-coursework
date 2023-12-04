package os.filesystem;

import os.SimulationConfig;

import java.util.Arrays;
import java.util.Random;

public class File {
    private final int[] addresses;
    private final int size;
    private final Random random;

    private int lastPosition;

    public File(int[] addresses) {
        this.addresses = addresses;
        size = addresses.length;
        random = new Random(SimulationConfig.RANDOM_GENERATOR_SEED);
    }

    public int getSize() {
        return size;
    }

    public int getNextAddress() {
        return getAddress(lastPosition + 1);
    }

    public int getRandomAddress() {
        return getAddress(random.nextInt());
    }

    private int getAddress(int position) {
        if (position < 0 || position >= addresses.length) {
            return addresses[0];
        }
        lastPosition = position;
        return addresses[position];
    }

    @Override
    public String toString() {
        return "File{" +
                "size=" + size +
                ", addresses=" + Arrays.toString(addresses) +
                '}';
    }
}
