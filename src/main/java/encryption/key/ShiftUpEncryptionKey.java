package encryption.key;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Arrays;

public class ShiftUpEncryptionKey implements EncryptionKey {
    private final byte value;

    public ShiftUpEncryptionKey(byte value) {
        this.value = value;
    }

    public static ShiftUpEncryptionKey decode(InputStream inputStream) throws IOException {
        byte[] identifierBytes = ShiftUpEncryptionKey.class.getSimpleName().getBytes();
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
            throw new RuntimeException("expected identifier " + ShiftUpEncryptionKey.class.getSimpleName() +
                                       " but received " + new String(bytesToRead, 0, identifierBytes.length));
        }
        return new ShiftUpEncryptionKey(bytesToRead[bytesToRead.length - 1]);
    }

    @Override
    public ShiftUpEncryptionKey extractShiftUpKey() {
        return this;
    }

    @Override
    public InputStream encode() {
        return new SequenceInputStream(new ByteArrayInputStream(ShiftUpEncryptionKey.class.getSimpleName().getBytes()),
                                       new ByteArrayInputStream(new byte[]{value}));
    }

    public byte get() {
        return value;
    }

    public ShiftUpEncryptionKey inverse() {
        return new ShiftUpEncryptionKey((byte) -value);
    }
}
