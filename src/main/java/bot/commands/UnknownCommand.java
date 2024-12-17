package bot.commands;

import bot.messages.CommandContext;
import bot.status.Status;

import static bot.commands.CommandResponse.UNKNOWN_COMMAND_MESSAGE;

public class UnknownCommand implements Command {

    @Override
    public String getName() {
        return "__unknown__"; // Приведено к нижнему регистру для согласованности
    }

    @Override
    public boolean isAvailableInStatus(Status status) {
        return true; // Доступна во всех статусах
    }

    @Override
    public void execute(CommandContext context) {
        context.getMessageSender().sendMessage(UNKNOWN_COMMAND_MESSAGE);
    }

    @Override
    public Status getNewStatus() {
        return null; // Команда не меняет статус
    }
}
