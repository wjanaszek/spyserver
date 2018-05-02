package pl.edu.pw.elka.tin.spy.server.domain.protocol;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class RawMessageParser {
    public static Message parse(byte[] message) {
        ByteBuffer bb = ByteBuffer.wrap(message);

        Header messageHeader = readHeader(bb);

        switch (messageHeader) {
            case PHOTO:
                int length = bb.remaining();
                byte[] photo = new byte[length];
                bb.get(photo, 0, length);
                return new PhotoMessage(messageHeader, photo);
            default:
                return SimpleMessage.UnrecognisedHeader;
        }
    }

    private static Header readHeader(ByteBuffer bb) {
        byte[] rawHeader = new byte[3];
        bb.get(rawHeader, 0, 3);
        return Header.fromString(new String(rawHeader, StandardCharsets.UTF_8));
    }
}
