package bot.commands;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {
    private final Map<String, Command> commands = new HashMap<>();
    private final Command unknownCommand = new UnknownCommand();

    public CommandRegistry() {
    registerCommand(new HelpCommand());
    registerCommand(new StartCommand());
    registerCommand(new StopCommand());
    }

    private void registerCommand(Command command) {
        commands.put(command.getName().toLowerCase(), command);
    }

    public Command getCommand(String name) {
        return commands.getOrDefault(name.toLowerCase(), unknownCommand);
    }

    public Collection<Command> getAllCommands() {
        return commands.values();
    }
}
