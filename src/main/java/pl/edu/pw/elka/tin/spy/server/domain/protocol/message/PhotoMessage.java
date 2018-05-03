package pl.edu.pw.elka.tin.spy.server.domain.protocol.message;


import lombok.AllArgsConstructor;
import lombok.Data;
import pl.edu.pw.elka.tin.spy.server.domain.protocol.Header;

@Data
@AllArgsConstructor
public class PhotoMessage implements Message {
    private Header header;
    private byte[] photo;
}
