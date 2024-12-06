package encryption.algorithm_parser;

import encryption.algorithm.DoubleEncryptionAlgorithm;
import encryption.algorithm.EncryptionAlgorithm;
import encryption.algorithm.RepeatEncryptionAlgorithm;

import java.util.List;

public class TreeNode implements Tree {
    private final List<Tree> children;

    protected TreeNode(List<Tree> children) {
        this.children = children;
    }

    @Override
    public EncryptionAlgorithm decodeEncryptionAlgorithm() {
        if (children.size() != 3) {
            throw new IllegalArgumentException("could not decode encryption algorithm from " + TreeNode.class.getSimpleName() +
                                               " of size " + children.size());
        }
        String operation = children.get(0).asLeaf().value();
        Tree arg1 = children.get(1);
        Tree arg2 = children.get(2);
        if (operation.equals(RepeatEncryptionAlgorithm.class.getSimpleName())) {
            return new RepeatEncryptionAlgorithm(arg1.decodeEncryptionAlgorithm(), arg2.decodeInt());
        } else if (operation.equals(DoubleEncryptionAlgorithm.class.getSimpleName())) {
            return new DoubleEncryptionAlgorithm(arg1.decodeEncryptionAlgorithm(), arg2.decodeEncryptionAlgorithm());
        }
        throw new IllegalArgumentException("unrecognized encryption algorithm operation " + operation);
    }

    @Override
    public int decodeInt() {
        throw new IllegalArgumentException("could not decode a " + TreeNode.class.getSimpleName() + " into an int");
    }
}
