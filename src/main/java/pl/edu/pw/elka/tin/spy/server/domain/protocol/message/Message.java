package pl.edu.pw.elka.tin.spy.server.domain.protocol.message;

import pl.edu.pw.elka.tin.spy.server.domain.protocol.Header;

public interface Message {
    int intFieldInBytes = 4;
    int headerSizeInBytes = 3;
    Header header();
}
