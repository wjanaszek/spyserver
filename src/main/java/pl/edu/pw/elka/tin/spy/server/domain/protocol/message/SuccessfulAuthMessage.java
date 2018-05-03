package pl.edu.pw.elka.tin.spy.server.domain.protocol.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.elka.tin.spy.server.domain.protocol.Header;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class SuccessfulAuthMessage implements Message, SendMessage {

    @Getter
    private Header header = Header.AUTHENTICATION_REQUEST;
    private final String secret;

    @Override
    public byte[] toByteArray() {
        byte[] header = this.header.getValue().getBytes(StandardCharsets.UTF_8);
        byte[] rawSecret = secret.getBytes();

        ByteBuffer bb = ByteBuffer.allocate(messageSizeFieldInBytes + header.length + rawSecret.length);
        bb.putInt(header.length);
        bb.put(header);
        bb.putInt(rawSecret.length);
        bb.put(rawSecret);
        return bb.array();
    }
}
