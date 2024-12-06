package encryption.algorithm_parser;

import encryption.algorithm.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public interface Tree {
    static Tree from(List<Tree> trees) {
        if (trees.isEmpty()) {
            throw new IllegalArgumentException("could not construct a tree out of empty tree list");
        } else if (trees.size() == 1) {
            return trees.get(0);
        }
        return new TreeNode(trees);
    }

    private static TreeLeaf from(String value) {
        return new TreeLeaf(value);
    }

    static Tree readFrom(Queue<String> tokens) {
        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("unexpected empty tokens");
        }
        List<Tree> trees = new LinkedList<>();
        while (!tokens.isEmpty()) {
            String token = tokens.peek();
            if (token.equals(")")) {
                throw new IllegalArgumentException("unexpected \")\" token was not preceded with a corresponding \"(\" token");
            } else if (token.equals("(")) {
                trees.add(readBracketedTree(tokens));
            } else {
                trees.add(from(tokens.poll()));
            }
        }
        return from(trees);
    }

    private static Tree readBracketedTree(Queue<String> tokens) {
        List<Tree> trees = new LinkedList<>();
        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("unexpected empty tokens");
        }
        if (!tokens.peek().equals("(")) {
            throw new IllegalArgumentException("unexpected token \"" + tokens.peek() +
                                               "\" should have been \"(\"");
        }
        tokens.poll();
        while (true) {
            if (tokens.isEmpty()) {
                throw new IllegalArgumentException("token \"(\" was not met with a corresponding \")\" token");
            }
            if (tokens.peek().equals(")")) {
                tokens.poll();
                break;
            } else if (tokens.peek().equals("(")) {
                trees.add(readBracketedTree(tokens));
            } else {
                trees.add(from(tokens.poll()));
            }
        }
        return from(trees);
    }

    EncryptionAlgorithm decodeEncryptionAlgorithm();

    default TreeLeaf asLeaf() {
        throw new IllegalArgumentException(this.getClass().getSimpleName() + " is not a leaf");
    }

    int decodeInt();
}
