package pl.edu.pw.elka.tin.spy.server.domain.protocol.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.elka.tin.spy.server.domain.protocol.Header;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class SuccessfulAuthMessage implements Message, SendMessage {

    @Getter
    private Header header = Header.SUCCESSFUL_AUTH;
    private final byte[] secret;

    @Override
    public Header header() {
        return header;
    }

    @Override
    public byte[] toByteArray() {
        byte[] header = this.header.getValue().getBytes(StandardCharsets.UTF_8);
        int rawSecretSize = secret.length;
        int payloadSize = headerSizeInBytes + intFieldInBytes + rawSecretSize;

		ByteBuffer bb = ByteBuffer.allocate(messageSizeFieldInBytes + payloadSize);
		bb.putInt(payloadSize);
		bb.put(header);
        bb.putInt(rawSecretSize);
        bb.put(secret);
        return bb.array();
    }
}
