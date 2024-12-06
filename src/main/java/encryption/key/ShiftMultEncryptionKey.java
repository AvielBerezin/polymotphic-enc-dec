package encryption.key;

import encryption.RewindableInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Arrays;

public class ShiftMultEncryptionKey implements EncryptionKey {
    private final byte value;

    public ShiftMultEncryptionKey(byte value) {
        if (value % 2 == 0) {
            throw new IllegalArgumentException("mult-shifting value must be odd");
        }
        this.value = value;
    }

    public static ShiftMultEncryptionKey decode(InputStream inputStream) throws IOException {
        byte[] identifierBytes = ShiftMultEncryptionKey.class.getSimpleName().getBytes();
        byte[] bytesToRead = new byte[identifierBytes.length + 1];
        int position = 0;
        while (position < bytesToRead.length) {
            int read = inputStream.read(bytesToRead, position, bytesToRead.length - position);
            if (read == -1) {
                throw new RuntimeException("eof was reached too early");
            }
            position += read;
        }
        if (!Arrays.equals(identifierBytes, 0, identifierBytes.length, bytesToRead, 0, identifierBytes.length)) {
            throw new RuntimeException("expected identifier " + ShiftMultEncryptionKey.class.getSimpleName() +
                                       " but received " + new String(bytesToRead, 0, identifierBytes.length));
        }
        return new ShiftMultEncryptionKey(bytesToRead[bytesToRead.length - 1]);
    }

    @Override
    public ShiftMultEncryptionKey extractShiftMultKey() {
        return this;
    }

    @Override
    public InputStream encode() {
        return new SequenceInputStream(new ByteArrayInputStream(ShiftMultEncryptionKey.class.getSimpleName().getBytes()),
                                       new ByteArrayInputStream(new byte[]{value}));
    }

    public byte get() {
        return value;
    }

    public ShiftMultEncryptionKey inverse() {
        return new ShiftMultEncryptionKey((byte) mod256Pow(Byte.toUnsignedInt(value), 255));
    }

    private int mod256Pow(int value, int power) {
        if (power == 0) {
            return 1;
        }
        if (power == 1) {
            return value;
        }
        int halfPow = mod256Pow(value, power / 2);
        if (power % 2 == 0) {
            return halfPow * halfPow;
        }
        return value * halfPow * halfPow;
    }
}
