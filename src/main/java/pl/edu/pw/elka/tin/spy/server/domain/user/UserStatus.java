package pl.edu.pw.elka.tin.spy.server.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum  UserStatus {
    LOGOUT("LOGOUT"),
    ACTIVE("ACTIVE");

    @Getter
    private String text;

    public static UserStatus fromString(String text) {
        for (UserStatus s : UserStatus.values()) {
            if (s.text.equalsIgnoreCase(text)) {
                return s;
            }
        }
        throw new IllegalArgumentException(text + " is invalid UserStatus name");
    }
}
