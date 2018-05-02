package pl.edu.pw.elka.tin.spy.server.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum  UserStatus {
    LOGOUT("LOGOUT"),
    ACTIVE("ACTIVE");

    @Getter
    private String text;
}
