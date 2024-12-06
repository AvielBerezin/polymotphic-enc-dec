package encryption;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class RewindableInputStream extends InputStream {
    private final InputStream inputStream;
    private ByteBuffer byteBuffer;

    public RewindableInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.limit(0);
    }

    public void rewind() {
        byteBuffer.rewind();
    }

    @Override
    public int read() throws IOException {
        if (byteBuffer.position() < byteBuffer.limit()) {
            return byteBuffer.get();
        }
        int read = inputStream.read();
        if (read == -1) {
            return -1;
        }
        byteBuffer = extendByteBuffer(byteBuffer, byteBuffer.limit() + 1);
        int position = byteBuffer.position();
        byteBuffer.position(byteBuffer.limit());
        byteBuffer.limit(byteBuffer.limit() + 1);
        byteBuffer.put((byte) read);
        byteBuffer.position(position);
        return read & 0xff;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        if (len < 0) {
            throw new IllegalArgumentException("cannot read negative amount of bytes " + len);
        }
        byteBuffer = extendByteBuffer(byteBuffer, byteBuffer.position() + len);
        if (byteBuffer.remaining() >= len) {
            byteBuffer.get(b, off, len);
            return len;
        }
        int bytesAdded = inputStream.read(byteBuffer.array(), byteBuffer.limit(), len - byteBuffer.remaining());
        if (bytesAdded == -1) {
            return -1;
        }
        byteBuffer.limit(byteBuffer.limit() + bytesAdded);
        int toBeRead = byteBuffer.remaining();
        byteBuffer.get(b, off, toBeRead);
        return toBeRead;
    }

    private ByteBuffer extendByteBuffer(ByteBuffer src, int minimalSize) {
        if (src.capacity() >= minimalSize) {
            return src;
        }
        int prevPosition = src.position();
        int extendedCapacity = Math.max(src.capacity(), 1);
        while (extendedCapacity < minimalSize) {
            extendedCapacity *= 2;
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(extendedCapacity);
        byteBuffer.limit(src.limit());
        src.position(0);
        byteBuffer.put(src);
        byteBuffer.position(prevPosition);
        return byteBuffer;
    }
}
