package pl.edu.pw.elka.tin.spy.server.domain.protocol;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class SimpleMessage implements Message, SendMessage {
    private Header header;

    public static SimpleMessage PhotoRequest = new SimpleMessage(Header.PHOTO_REQUEST);
    public static SimpleMessage UnrecognisedHeader = new SimpleMessage(Header.UNRECOGNISED);

    public SimpleMessage(Header header) {
        this.header = header;
    }

    @Override
    public byte[] toByteArray() {
        ByteBuffer bb = ByteBuffer.allocate(7);
        bb.putInt(3);
        bb.put(header.getValue().getBytes(StandardCharsets.UTF_8));
        return bb.array();
    }
}
