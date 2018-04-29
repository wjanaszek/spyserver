package pl.edu.pw.elka.tin.spy.server;

public class Task {

    public enum Status {
        IN_PROGRESS("IN PROGRESS"),
        DONE("DONE"),
        WAITING("WAITING");

        private String text;

        Status(String text) {
            this.text = text;
        }

        public String getText() {
            return this.text;
        }

        public static Status fromString(String text) {
            for (Status s : Status.values()) {
                if (s.text.equalsIgnoreCase(text)) {
                    return s;
                }
            }
            return null;
        }

    }

    private int id;
    private String name;
    private Status status;
    // @TODO change this to timestamp?
    private String timestamp;

    public Task(int id, String name, Status status, String timestamp) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
