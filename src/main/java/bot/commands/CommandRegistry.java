package bot.commands;

import bot.data.TickerRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {
    private final Map<String, Command> commands = new HashMap<>();
    private final Command unknownCommand;
    private  TickerRepository tickerRepository;

    public CommandRegistry(TickerRepository tickerRepository) {
        this.unknownCommand = new UnknownCommand();
        this.tickerRepository = tickerRepository;
        registerDefaultCommands();
    }

    private void registerDefaultCommands() {
        registerCommand(new HelpCommand());
        registerCommand(new StartCommand());
        registerCommand(new StopCommand());
        registerCommand(new StatusCommand(tickerRepository));
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