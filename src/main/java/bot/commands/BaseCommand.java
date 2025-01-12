package bot.commands;

import bot.messages.CommandContext;
import bot.status.Status;

public abstract class BaseCommand implements Command {

    @Override
    public boolean isAvailableInStatus(Status status) {
        return true; // Доступна по умолчанию, переопределяется в подклассах
    }

    @Override
    public Status getNewStatus() {
        return null; // Не меняет статус по умолчанию
    }

    protected void logExecution(String commandName) {
        System.out.println("Executing command: " + commandName);
    }
}