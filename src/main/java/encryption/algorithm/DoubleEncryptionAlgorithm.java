package encryption.algorithm;

import encryption.key.DoubleEncryptionKey;
import encryption.key.EncryptionKey;

import java.io.InputStream;
import java.util.Random;

public class DoubleEncryptionAlgorithm implements EncryptionAlgorithm {
    private final EncryptionAlgorithm encryptionAlgorithm1;
    private final EncryptionAlgorithm encryptionAlgorithm2;

    public DoubleEncryptionAlgorithm(EncryptionAlgorithm encryptionAlgorithm1,
                                     EncryptionAlgorithm encryptionAlgorithm2) {
        this.encryptionAlgorithm1 = encryptionAlgorithm1;
        this.encryptionAlgorithm2 = encryptionAlgorithm2;
    }

    @Override
    public InputStream encrypt(EncryptionKey key, InputStream inputStream) {
        DoubleEncryptionKey doubleEncryptionKey = key.extractDoubleKey();
        return encryptionAlgorithm1.encrypt(doubleEncryptionKey.encryptionKey1(),
                                            encryptionAlgorithm2.encrypt(doubleEncryptionKey.encryptionKey2(),
                                                                         inputStream));
    }

    @Override
    public InputStream decrypt(EncryptionKey key, InputStream inputStream) {
        DoubleEncryptionKey doubleEncryptionKey = key.extractDoubleKey();
        return encryptionAlgorithm2.decrypt(doubleEncryptionKey.encryptionKey2(),
                                            encryptionAlgorithm1.decrypt(doubleEncryptionKey.encryptionKey1(),
                                                                         inputStream));
    }

    @Override
    public EncryptionKey generateKey(Random random) {
        return new DoubleEncryptionKey(encryptionAlgorithm1.generateKey(random),
                                       encryptionAlgorithm2.generateKey(random));
    }
}
