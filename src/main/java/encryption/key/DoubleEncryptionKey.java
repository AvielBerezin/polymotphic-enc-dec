package encryption.key;

import encryption.MultiException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.*;

public class DoubleEncryptionKey implements EncryptionKey {
    private final EncryptionKey encryptionKey1;
    private final EncryptionKey encryptionKey2;

    public DoubleEncryptionKey(EncryptionKey encryptionKey1, EncryptionKey encryptionKey2) {
        this.encryptionKey1 = encryptionKey1;
        this.encryptionKey2 = encryptionKey2;
    }

    public static DoubleEncryptionKey decode(InputStream inputStream) throws IOException, MultiException {
        byte[] identifierBytes = DoubleEncryptionKey.class.getSimpleName().getBytes();
        byte[] bytesToRead = new byte[identifierBytes.length];
        int position = 0;
        while (position < bytesToRead.length) {
            int read = inputStream.read(bytesToRead, position, bytesToRead.length - position);
            if (read == -1) {
                throw new RuntimeException("eof was reached too early");
            }
            position += read;
        }
        if (!Arrays.equals(identifierBytes, 0, identifierBytes.length, bytesToRead, 0, identifierBytes.length)) {
            throw new RuntimeException("expected identifier " + DoubleEncryptionKey.class.getSimpleName() +
                                       " but received " + new String(bytesToRead, 0, identifierBytes.length));
        }
        EncryptionKey arg1 = EncryptionKey.decode(inputStream);
        EncryptionKey arg2 = EncryptionKey.decode(inputStream);
        return new DoubleEncryptionKey(arg1, arg2);
    }

    @Override
    public DoubleEncryptionKey extractDoubleKey() {
        return this;
    }

    @Override
    public InputStream encode() {
        List<InputStream> inputStreams = new ArrayList<>(3);
        inputStreams.add(new ByteArrayInputStream(DoubleEncryptionKey.class.getSimpleName().getBytes()));
        inputStreams.add(encryptionKey1.encode());
        inputStreams.add(encryptionKey2.encode());
        return new SequenceInputStream(Collections.enumeration(inputStreams));
    }

    public EncryptionKey encryptionKey1() {
        return encryptionKey1;
    }

    public EncryptionKey encryptionKey2() {
        return encryptionKey2;
    }
}
