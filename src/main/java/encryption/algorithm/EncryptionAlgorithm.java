package encryption.algorithm;

import encryption.algorithm_parser.Tokenizer;
import encryption.algorithm_parser.Tree;
import encryption.key.EncryptionKey;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Random;

public interface EncryptionAlgorithm {
    static EncryptionAlgorithm decode(Reader reader) throws IOException {
        return Tree.readFrom(Tokenizer.tokenize(reader)).decodeEncryptionAlgorithm();
    }

    InputStream encrypt(EncryptionKey key, InputStream inputStream);

    InputStream decrypt(EncryptionKey key, InputStream inputStream);

    EncryptionKey generateKey(Random random);
}
