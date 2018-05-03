package pl.edu.pw.elka.tin.spy.server.domain.protocol.message;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.elka.tin.spy.server.domain.protocol.Header;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Data
@RequiredArgsConstructor
public class SuccessfulRegistrationMessage implements Message, SendMessage {
    private Header header = Header.SUCCESSFUL_REGISTRATION;
    private final int clientID;

    @Override
    public byte[] toByteArray() {
        byte[] header = this.header.getValue().getBytes(StandardCharsets.UTF_8);

        ByteBuffer bb = ByteBuffer.allocate(messageSizeFieldInBytes + header.length + intFieldInBytes);
        bb.putInt(header.length);
        bb.put(header);
        bb.putInt(clientID);
        return bb.array();
    }
}
