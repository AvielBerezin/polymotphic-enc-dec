package encryption.algorithm;

import encryption.key.EncryptionKey;
import encryption.key.ShiftUpEncryptionKey;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class ShiftUpEncryptionAlgorithm implements EncryptionAlgorithm {
    @Override
    public InputStream encrypt(EncryptionKey key, InputStream inputStream) {
        return new ShiftUpInputStream(inputStream, key.extractShiftUpKey().get());
    }

    @Override
    public InputStream decrypt(EncryptionKey key, InputStream inputStream) {
        return new ShiftUpInputStream(inputStream, key.extractShiftUpKey().inverse().get());
    }

    @Override
    public EncryptionKey generateKey(Random random) {
        return new ShiftUpEncryptionKey((byte) random.nextInt());
    }

    private static class ShiftUpInputStream extends InputStream {
        private final InputStream inputStream;
        private final byte shift;

        public ShiftUpInputStream(InputStream inputStream, byte shift) {
            this.inputStream = inputStream;
            this.shift = shift;
        }

        @Override
        public int read() throws IOException {
            int read = inputStream.read();
            if (read == -1) {
                return -1;
            }
            return 0xff & (read + shift);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int read = super.read(b, off, len);
            if (read == -1) {
                return -1;
            }
            for (int i = off; i < read; i++) {
                b[i] += shift;
            }
            return read;
        }
    }
}
