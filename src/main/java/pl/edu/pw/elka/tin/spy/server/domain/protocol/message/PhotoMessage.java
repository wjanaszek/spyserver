package pl.edu.pw.elka.tin.spy.server.domain.protocol.message;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.elka.tin.spy.server.domain.protocol.Header;

@Data
@RequiredArgsConstructor
public class PhotoMessage implements Message {
    private Header header = Header.PHOTO;
    private final byte[] photo;

    @Override
    public Header header() {
        return header;
    }
}
