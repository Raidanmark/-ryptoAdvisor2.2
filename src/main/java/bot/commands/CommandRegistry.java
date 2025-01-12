package bot.commands;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {
    private final Map<String, Command> commands = new HashMap<>();
    private final Command unknownCommand;

    public CommandRegistry() {
        this.unknownCommand = new UnknownCommand();
        registerDefaultCommands();
    }

    private void registerDefaultCommands() {
        registerCommand(new HelpCommand());
        registerCommand(new StartCommand());
        registerCommand(new StopCommand());
    }

    public void registerCommand(Command command) {
        if (commands.containsKey(command.getName().toLowerCase())) {
            throw new IllegalArgumentException("Command already registered: " + command.getName());
        }
        commands.put(command.getName().toLowerCase(), command);
    }

    public Command getCommand(String name) {
        return commands.getOrDefault(name.toLowerCase(), unknownCommand);
    }

    public Collection<Command> getAllCommands() {
        return commands.values();
    }
}