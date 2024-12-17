package bot.commands;

import bot.messages.CommandContext;
import bot.status.Status;

import static bot.commands.CommandResponse.HELP_COMMAND_MESSAGE_ACTIVE;
import static bot.commands.CommandResponse.HELP_COMMAND_MESSAGE_INACTIVE;

public class HelpCommand implements Command {

    @Override
    public String getName() {
        return "!help"; // Приведено к нижнему регистру для согласованности
    }

    @Override
    public boolean isAvailableInStatus(Status status) {
        return true; // Команда доступна во всех статусах
    }

    @Override
    public void execute(CommandContext context){
        String statusName = context.getStatus().getName();

        switch (statusName) {
            case "INACTIVE":
                context.getMessageSender().sendMessage(HELP_COMMAND_MESSAGE_INACTIVE);
                break;

            case "ACTIVE":

                context.getMessageSender().sendMessage(HELP_COMMAND_MESSAGE_ACTIVE);
                break;

            default:
                context.getMessageSender().sendMessage("Unknown status. Commands may be unavailable.");
                break;
        }

    }

    @Override
    public Status getNewStatus() {
        return null; // Команда не меняет статус
    }
}
