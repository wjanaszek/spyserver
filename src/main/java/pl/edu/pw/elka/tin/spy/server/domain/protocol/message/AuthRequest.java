package pl.edu.pw.elka.tin.spy.server.domain.protocol.message;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.elka.tin.spy.server.domain.protocol.Header;

@Data
@RequiredArgsConstructor
public class AuthRequest implements Message {
    @Getter private Header header = Header.AUTHENTICATION_REQUEST;
    private final int userID;
    private final String password;

    @Override
    public Header header() {
        return header;
    }
}
