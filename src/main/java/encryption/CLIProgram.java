package encryption;

import encryption.algorithm.EncryptionAlgorithm;
import encryption.key.EncryptionKey;

import java.io.*;
import java.nio.file.Path;
import java.util.Random;
import java.util.Scanner;

public class CLIProgram {
    public static void main(String[] args) throws IOException, MultiException {
        Scanner scanner = new Scanner(System.in);
        EncryptionAlgorithm encryptionAlgorithm = EncryptionAlgorithm.decode(new StringReader(cliRequest(scanner, "enter encryption mechanism")));
        switch (EncryptionOption.readFrom(cliRequest(scanner, "enter encryption option: encrypt / decrypt"))) {
            case ENCRYPT -> {
                Path filePath = Path.of(cliRequest(scanner, "enter file for encryption"));
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
                Path filePath = Path.of(cliRequest(scanner, "enter file for decryption"));
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

    private static String cliRequest(Scanner scanner, String x) {
        System.out.println(x);
        return scanner.nextLine();
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
}
