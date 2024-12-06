package encryption.algorithm_parser;

import encryption.algorithm.EncryptionAlgorithm;
import encryption.algorithm.ShiftMultEncryptionAlgorithm;
import encryption.algorithm.ShiftUpEncryptionAlgorithm;

public class TreeLeaf implements Tree {
    private final String value;

    protected TreeLeaf(String value) {
        this.value = value;
    }

    @Override
    public EncryptionAlgorithm decodeEncryptionAlgorithm() {
        if (value().equals(ShiftUpEncryptionAlgorithm.class.getSimpleName())) {
            return new ShiftUpEncryptionAlgorithm();
        }
        if (value().equals(ShiftMultEncryptionAlgorithm.class.getSimpleName())) {
            return new ShiftMultEncryptionAlgorithm();
        }
        throw new IllegalArgumentException("unexpected tree leaf " + value());
    }

    @Override
    public TreeLeaf asLeaf() {
        return this;
    }

    @Override
    public int decodeInt() {
        return Integer.parseInt(value());
    }

    public String value() {
        return value;
    }
}
