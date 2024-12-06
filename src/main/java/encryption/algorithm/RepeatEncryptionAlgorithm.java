package encryption.algorithm;

import encryption.key.EncryptionKey;

import java.io.InputStream;
import java.util.Random;

public class RepeatEncryptionAlgorithm implements EncryptionAlgorithm {
    private final EncryptionAlgorithm encryptionAlgorithm;
    private final int times;

    public RepeatEncryptionAlgorithm(EncryptionAlgorithm encryptionAlgorithm,
                                     int times) {
        this.encryptionAlgorithm = encryptionAlgorithm;
        this.times = times;
    }

    @Override
    public InputStream encrypt(EncryptionKey key, InputStream inputStream) {
        InputStream result = inputStream;
        if (times >= 0) {
            for (int i = 0; i < times; i++) {
                result = encryptionAlgorithm.encrypt(key, result);
            }
            return result;
        }
        for (int i = 0; i > times; i--) {
            result = encryptionAlgorithm.decrypt(key, result);
        }
        return result;
    }

    @Override
    public InputStream decrypt(EncryptionKey key, InputStream inputStream) {
        InputStream result = inputStream;
        if (times >= 0) {
            for (int i = 0; i < times; i++) {
                result = encryptionAlgorithm.decrypt(key, result);
            }
            return result;
        }
        for (int i = 0; i > times; i--) {
            result = encryptionAlgorithm.encrypt(key, result);
        }
        return result;
    }

    @Override
    public EncryptionKey generateKey(Random random) {
        return encryptionAlgorithm.generateKey(random);
    }
}
