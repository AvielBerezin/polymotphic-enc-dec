package encryption;

import encryption.algorithm.*;
import encryption.key.EncryptionKey;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException, MultiException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("enter encryption mechanism");
        String encryptionMechanism = scanner.nextLine();
        EncryptionAlgorithm encryptionAlgorithm = decodeEncryptionAlgorithm(new ByteArrayInputStream(encryptionMechanism.getBytes()));
        System.out.println("enter encryption option: encrypt / decrypt");
        EncryptionOption encryptionOption = readEncryptionOption(scanner.nextLine());
        switch (encryptionOption) {
            case ENCRYPT -> {
                System.out.println("enter file for encryption");
                Path filePath = Path.of(scanner.nextLine());
                EncryptionKey encryptionKey = encryptionAlgorithm.generateKey(new Random());
                try (InputStream encryptedStream = encryptionAlgorithm.encrypt(encryptionKey, new FileInputStream(filePath.toFile()))) {
                    Path encryptedFile = suffixFile(filePath, "_encrypted");
                    try (FileOutputStream fileOutputStream = new FileOutputStream(encryptedFile.toFile())) {
                        Path encryptionKeyFile = filePath.toAbsolutePath().getParent().resolve("key.dat");
                        try (FileOutputStream keyOutputStream = new FileOutputStream(encryptionKeyFile.toFile());
                             InputStream encodedKeyStream = encryptionKey.encode()) {
                            encodedKeyStream.transferTo(keyOutputStream);
                        }
                        System.out.println("encryption key was written to " + encryptionKeyFile);
                        encryptedStream.transferTo(fileOutputStream);
                    }
                    System.out.println("file " + filePath.toAbsolutePath() + " was encrypted into " + encryptedFile.toAbsolutePath());
                }
            }
            case DECRYPT -> {
                System.out.println("enter file for decryption");
                Path filePath = Path.of(scanner.nextLine());
                System.out.println("enter encryption key path");
                Path decryptionKeyPath = Path.of(scanner.nextLine());
                EncryptionKey encryptionKey;
                try (FileInputStream decryptionKeyStream = new FileInputStream(decryptionKeyPath.toFile())) {
                    encryptionKey = EncryptionKey.decode(decryptionKeyStream);
                }
                try (InputStream decryptedStream = encryptionAlgorithm.decrypt(encryptionKey, new FileInputStream(filePath.toFile()))) {
                    Path decryptedFile = suffixFile(filePath, "_decrypted");
                    try (FileOutputStream fileOutputStream = new FileOutputStream(decryptedFile.toFile())) {
                        decryptedStream.transferTo(fileOutputStream);
                    }
                    System.out.println("file " + filePath.toAbsolutePath() + " was decrypted into " + decryptedFile.toAbsolutePath());
                }
            }
        }
    }

    private static Path suffixFile(Path filePath, String suffix) {
        int extIndex = filePath.getFileName().toString().lastIndexOf(".");
        if (extIndex == -1) {
            return filePath.toAbsolutePath().getParent().resolve(filePath.getFileName().toString() + suffix);
        }
        return filePath.toAbsolutePath().getParent().resolve(filePath.getFileName().toString().substring(0, extIndex) +
                                                             suffix +
                                                             filePath.getFileName().toString().substring(extIndex));
    }

    private static EncryptionOption readEncryptionOption(String encryptionOption) {
        EncryptionOption encryptionOptionE;
        if (encryptionOption.equals("encrypt")) {
            encryptionOptionE = EncryptionOption.ENCRYPT;
        } else if (encryptionOption.equals("decrypt")) {
            encryptionOptionE = EncryptionOption.DECRYPT;
        } else {
            throw new RuntimeException("bad input from user for encryption option: " + encryptionOption);
        }
        return encryptionOptionE;
    }

    private enum EncryptionOption {ENCRYPT, DECRYPT}

    private static EncryptionAlgorithm decodeEncryptionAlgorithm(InputStream inputStream) throws IOException {
        return readTree(tokenize(inputStream)).decodeEncryptionAlgorithm();
    }

    private static Tree readTree(Queue<StringBuilder> tokens) {
        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("unexpected empty tokens");
        }
        List<Tree> trees = new LinkedList<>();
        while (!tokens.isEmpty()) {
            String token = tokens.peek().toString();
            if (token.equals(")")) {
                throw new IllegalArgumentException("unexpected \")\" token was not preceded with a corresponding \"(\" token");
            } else if (token.equals("(")) {
                trees.add(readBracketedTree(tokens));
            } else {
                trees.add(Tree.from(tokens.poll().toString()));
            }
        }
        return Tree.from(trees);
    }

    private static Tree readBracketedTree(Queue<StringBuilder> tokens) {
        List<Tree> trees = new LinkedList<>();
        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("unexpected empty tokens");
        }
        if (!tokens.peek().toString().equals("(")) {
            throw new IllegalArgumentException("unexpected token \"" + tokens.peek().toString() +
                                               "\" should have been \"(\"");
        }
        tokens.poll();
        while (true) {
            if (tokens.isEmpty()) {
                throw new IllegalArgumentException("token \"(\" was not met with a corresponding \")\" token");
            }
            if (tokens.peek().toString().equals(")")) {
                tokens.poll();
                break;
            } else if (tokens.peek().toString().equals("(")) {
                trees.add(readBracketedTree(tokens));
            } else {
                trees.add(Tree.from(tokens.poll().toString()));
            }
        }
        return Tree.from(trees);
    }

    private interface Tree {
        static Tree from(List<Tree> trees) {
            if (trees.isEmpty()) {
                throw new IllegalArgumentException("could not construct a tree out of empty tree list");
            } else if (trees.size() == 1) {
                return trees.get(0);
            }
            return new Node(trees);
        }

        private static Leaf from(String value) {
            return new Leaf(value);
        }

        EncryptionAlgorithm decodeEncryptionAlgorithm();

        default Leaf asLeaf() {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + " is not a leaf");
        }

        int decodeInt();
    }

    private static class Node implements Tree {
        private final List<Tree> children;

        private Node(List<Tree> children) {
            this.children = children;
        }

        @Override
        public EncryptionAlgorithm decodeEncryptionAlgorithm() {
            if (children.size() != 3) {
                throw new IllegalArgumentException("could not decode encryption algorithm from " + Node.class.getSimpleName() +
                                                   " of size " + children.size());
            }
            String operation = children.get(0).asLeaf().value;
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
            throw new IllegalArgumentException("could not decode a " + Node.class.getSimpleName() + " into an int");
        }
    }

    private static class Leaf implements Tree {
        private final String value;

        private Leaf(String value) {
            this.value = value;
        }

        @Override
        public EncryptionAlgorithm decodeEncryptionAlgorithm() {
            if (value.equals(ShiftUpEncryptionAlgorithm.class.getSimpleName())) {
                return new ShiftUpEncryptionAlgorithm();
            }
            if (value.equals(ShiftMultEncryptionAlgorithm.class.getSimpleName())) {
                return new ShiftMultEncryptionAlgorithm();
            }
            throw new IllegalArgumentException("unexpected tree leaf " + value);
        }

        @Override
        public Leaf asLeaf() {
            return this;
        }

        @Override
        public int decodeInt() {
            return Integer.parseInt(value);
        }
    }

    private static LinkedList<StringBuilder> tokenize(InputStream inputStream) throws IOException {
        InputStreamReader reader = new InputStreamReader(inputStream);
        LinkedList<StringBuilder> tokens = new LinkedList<>();
        tokens.add(new StringBuilder());
        int charOrEOF = reader.read();
        while (charOrEOF != -1) {
            char nextChar = (char) charOrEOF;
            if (nextChar <= ' ') {
                if (!tokens.getLast().isEmpty()) {
                    tokens.add(new StringBuilder());
                }
            } else if (nextChar == '(' || nextChar == ')') {
                if (!tokens.getLast().isEmpty()) {
                    tokens.add(new StringBuilder());
                }
                tokens.getLast().append(nextChar);
                tokens.add(new StringBuilder());
            } else {
                tokens.getLast().append(nextChar);
            }
            charOrEOF = reader.read();
        }
        if (tokens.getLast().isEmpty()) {
            tokens.removeLast();
        }
        return tokens;
    }
}
