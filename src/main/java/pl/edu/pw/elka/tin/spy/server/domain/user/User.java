package pl.edu.pw.elka.tin.spy.server.domain.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class User {
    private int ID;
    private String name;
    private UserStatus status;
}
