package bot.status;

public class Status {
    private final String name;

    public Status(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Status name cannot be null or empty");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Status{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Status status = (Status) o;
        return name.equals(status.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
