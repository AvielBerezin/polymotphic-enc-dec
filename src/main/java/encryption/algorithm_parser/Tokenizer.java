package encryption.algorithm_parser;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.Queue;

public class Tokenizer {
    /**
     * Tokens consist of:
     * <ul>
     *     <li>(</li>
     *     <li>)</li>
     *     <li>string with no white characters, no '(' and no ')' </li>
     * </ul>
     * This method either exhausts the reader
     * unless the reader throws an exception on read operation.
     */
    public static Queue<String> tokenize(Reader reader) throws IOException {
        LinkedList<StringBuilder> tokenBuilders = new LinkedList<>();
        tokenBuilders.add(new StringBuilder());
        int charOrEOF = reader.read();
        while (charOrEOF != -1) {
            char nextChar = (char) charOrEOF;
            if (nextChar <= ' ') {
                if (!tokenBuilders.getLast().isEmpty()) {
                    tokenBuilders.add(new StringBuilder());
                }
            } else if (nextChar == '(' || nextChar == ')') {
                if (!tokenBuilders.getLast().isEmpty()) {
                    tokenBuilders.add(new StringBuilder());
                }
                tokenBuilders.getLast().append(nextChar);
                tokenBuilders.add(new StringBuilder());
            } else {
                tokenBuilders.getLast().append(nextChar);
            }
            charOrEOF = reader.read();
        }
        if (tokenBuilders.getLast().isEmpty()) {
            tokenBuilders.removeLast();
        }
        LinkedList<String> tokens = new LinkedList<>();
        for (StringBuilder tokenBuilder : tokenBuilders) {
            tokens.add(tokenBuilder.toString());
        }
        return tokens;
    }
}
