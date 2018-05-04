package pl.edu.pw.elka.tin.spy.server.domain.protocol.message;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.elka.tin.spy.server.domain.protocol.Header;

@Data
@RequiredArgsConstructor
public class RegistrationRequest implements Message {
    @Getter
    private Header header = Header.REGISTRATION_REQUEST;
    private final String name;
    private final String password;

    @Override
    public Header header() {
        return header;
    }
}
