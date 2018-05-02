package pl.edu.pw.elka.tin.spy.server.domain.protocol;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PhotoMessage implements Message {
    private Header header;
    private byte[] photo;
}
