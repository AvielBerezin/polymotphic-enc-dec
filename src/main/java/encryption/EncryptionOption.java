package encryption;

enum EncryptionOption {
    ENCRYPT, DECRYPT;

    public static EncryptionOption readFrom(String string) {
        EncryptionOption encryptionOptionE;
        if (string.equals("encrypt")) {
            encryptionOptionE = ENCRYPT;
        } else if (string.equals("decrypt")) {
            encryptionOptionE = DECRYPT;
        } else {
            throw new RuntimeException("unrecognized encryption option: " + string);
        }
        return encryptionOptionE;
    }
}
