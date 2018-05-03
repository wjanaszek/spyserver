package pl.edu.pw.elka.tin.spy.server.domain.task;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum TaskStatus {
    IN_PROGRESS("IN PROGRESS"),
    DONE("DONE"),
    NEW("NEW");

    @Getter private String text;

    public static TaskStatus fromString(String text) {
        for (TaskStatus s : TaskStatus.values()) {
            if (s.text.equalsIgnoreCase(text)) {
                return s;
            }
        }
        throw new IllegalArgumentException(text + " is invalid TaskStatus name");
    }
}