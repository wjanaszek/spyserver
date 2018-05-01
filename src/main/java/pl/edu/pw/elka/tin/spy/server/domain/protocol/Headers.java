package pl.edu.pw.elka.tin.spy.server.domain.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Headers {
    PHOTO_REQUEST("SPH");

    @Getter String value;
}
