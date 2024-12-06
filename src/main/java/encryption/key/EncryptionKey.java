package encryption.key;

import encryption.MultiException;
import encryption.RewindableInputStream;

import java.io.InputStream;

public interface EncryptionKey {
    static EncryptionKey decode(InputStream inputStream) throws MultiException {
        MultiException multiException = new MultiException("stream did not meet any encoding format");
        RewindableInputStream rewindableInputStream = new RewindableInputStream(inputStream);
        try {
            return ShiftUpEncryptionKey.decode(rewindableInputStream);
        } catch (Exception e) {
            multiException.addReason(e);
        }
        rewindableInputStream.rewind();
        try {
            return ShiftMultEncryptionKey.decode(rewindableInputStream);
        } catch (Exception e) {
            multiException.addReason(e);
        }
        rewindableInputStream.rewind();
        try {
            return DoubleEncryptionKey.decode(rewindableInputStream);
        } catch (Exception e) {
            multiException.addReason(e);
        }
        throw multiException;
    }

    default ShiftUpEncryptionKey extractShiftUpKey() {
        throw new IllegalArgumentException("incompatible key " + this.getClass().getSimpleName() +
                                           " while expecting " + ShiftUpEncryptionKey.class.getSimpleName());
    }

    default ShiftMultEncryptionKey extractShiftMultKey() {
        throw new IllegalArgumentException("incompatible key " + this.getClass().getSimpleName() +
                                           " while expecting " + ShiftMultEncryptionKey.class.getSimpleName());
    }

    default DoubleEncryptionKey extractDoubleKey() {
        throw new IllegalArgumentException("incompatible key " + this.getClass().getSimpleName() +
                                           " while expecting " + DoubleEncryptionKey.class.getSimpleName());
    }

    InputStream encode();
}
