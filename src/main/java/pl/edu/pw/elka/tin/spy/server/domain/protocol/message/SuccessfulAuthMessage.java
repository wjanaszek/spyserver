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
    private final String secret;

    @Override
    public Header header() {
        return header;
    }

    @Override
    public byte[] toByteArray() {
        byte[] header = this.header.getValue().getBytes(StandardCharsets.UTF_8);
        byte[] rawSecret = secret.getBytes();
        int rawSecretSize = rawSecret.length;
		//messageSize = header(3) + int for rawSecretSize + rawSecretSize
        int messageSize = 3 + 4 + rawSecretSize;

		ByteBuffer bb = ByteBuffer.allocate(messageSizeFieldInBytes + messageSize);
		bb.putInt(messageSize);
		bb.put(header);
        bb.putInt(rawSecretSize);
        bb.put(rawSecret);
        return bb.array();
    }
}
