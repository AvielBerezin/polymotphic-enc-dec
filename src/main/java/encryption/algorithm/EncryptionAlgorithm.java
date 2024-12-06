package encryption.algorithm;

import encryption.key.EncryptionKey;

import java.io.InputStream;
import java.util.Random;

public interface EncryptionAlgorithm {
    InputStream encrypt(EncryptionKey key, InputStream inputStream);

    InputStream decrypt(EncryptionKey key, InputStream inputStream);

    EncryptionKey generateKey(Random random);
}
