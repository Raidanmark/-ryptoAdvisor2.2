package bot.chatbot;

import bot.commands.*;
import bot.messages.CommandContext;
import bot.messages.MessageSender;
import bot.status.Status;


public class ChatBotSession {

    private Status currentStatus;
    private final MessageSender messageSender;
    private final CommandRegistry commandRegistry;

    public ChatBotSession(MessageSender messageSender) {
        this.messageSender = messageSender;
        this.commandRegistry = new CommandRegistry();
        this.currentStatus = new Status("INACTIVE"); // Начальный статус
    }

    // Метод для обработки команд
    public void processCommand(String commandText) {
        if (!isCommand(commandText)) {
            handleNonCommandMessage(commandText);
            return;
        }

        Command command = commandRegistry.getCommand(commandText);
        if (!isCommandAvailable(command)) {
            handleUnavailableCommand(commandText);
            return;
        }

        executeCommand(command);
    }


    private boolean isCommand(String commandText) {
        return commandText.startsWith("!");
    }

    private void handleNonCommandMessage(String message) {
        log("Received non-command message: " + message);
    }

    private boolean isCommandAvailable(Command command) {
        return command != null && command.isAvailableInStatus(currentStatus);
    }

    private void handleUnavailableCommand(String commandText) {
        messageSender.sendMessage("Command <" + commandText + "> is not available in the current status.");
    }

    private void executeCommand(Command command) {
        try {
            CommandContext context = new CommandContext(messageSender, currentStatus);
            command.execute(context);
            updateStatus(command);
        } catch (Exception e) {
            logError("Error executing command: " + command.getName(), e);
        }
    }

    private void updateStatus(Command command) {
        Status newStatus = command.getNewStatus();
        if (newStatus != null) {
            log("Updating status from " + currentStatus.getName() + " to " + newStatus.getName());
            currentStatus = newStatus;
        }
    }

    public Status getCurrentStatus() {
        return currentStatus;
    }

    public MessageSender getMessageSender() {
        return messageSender;
    }

    private void log(String message) {
        System.out.println("[ChatBotSession] " + message);
    }

    private void logError(String message, Throwable e) {
        System.err.println("[ChatBotSession] " + message);
        e.printStackTrace();
    }


}
