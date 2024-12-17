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
        if (!isItCommand(commandText)) {
            return;
        }


        Command command = commandRegistry.getCommand(commandText);
// Проверка доступности команды в текущем статусе
        if (!command.isAvailableInStatus(currentStatus)) {
            messageSender.sendMessage("Command <" + commandText + "> is not available in status ");
            return;
        }

        // Выполняем команду
        CommandContext context = new CommandContext(messageSender, currentStatus);
        command.execute(context);

        // Обновляем текущий статус, если команда меняет статус
        Status newStatus = command.getNewStatus();
        if (newStatus != null) {
            currentStatus = newStatus;
        }

    }


    private boolean isItCommand (String commandText)
    {
        return commandText.startsWith("!");
    }


}
