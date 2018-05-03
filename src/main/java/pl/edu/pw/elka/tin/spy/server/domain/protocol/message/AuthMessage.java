package pl.edu.pw.elka.tin.spy.server.domain.protocol.message;

import pl.edu.pw.elka.tin.spy.server.domain.protocol.Header;

public class AuthMessage {
    private Header header = Header.AUTHENTICATION_REQUEST;
    private int clientID;
    private String password;
}
