package pl.edu.pw.elka.tin.spy.server.domain.task;

public enum TaskStatus {
    IN_PROGRESS("IN PROGRESS"),
    DONE("DONE"),
    WAITING("WAITING");

    private String text;

    TaskStatus(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public static TaskStatus fromString(String text) {
        for (TaskStatus s : TaskStatus.values()) {
            if (s.text.equalsIgnoreCase(text)) {
                return s;
            }
        }
        return null;
    }

}