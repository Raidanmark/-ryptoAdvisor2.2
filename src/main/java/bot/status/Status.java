package bot.status;

import java.util.HashSet;
import java.util.Set;

public class Status {
    private final String name;
    private final Set<String> availableCommands;

    public Status(String name) {
        this.name = name;
        this.availableCommands = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public Set<String> getAvailableCommands() {
        return availableCommands;
    }

    public void addCommand(String commandName) {
        availableCommands.add(commandName);
    }

    public boolean isCommandAvailable(String commandName) {
        return availableCommands.contains(commandName);
    }
}