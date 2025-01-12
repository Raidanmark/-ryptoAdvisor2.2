package bot.chatbot;

import bot.messages.JDAMessageSender;
import bot.messages.MessageSender;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;


public class BotListener extends ListenerAdapter {
    private final Map<String, ChatBotSession> chatSessions = new HashMap<>();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (shouldIgnore(event)) {
            return;
        }

        String chatId = event.getChannel().getId();
        String message = event.getMessage().getContentRaw();

        log("[Message Received] Chat ID: " + chatId + ", Message: " + message);

        try {
            ChatBotSession session = getOrCreateSession(chatId, event);
            session.processCommand(message);
        } catch (Exception e) {
            logError("Error processing message: " + message, e);
        }
    }

    private boolean shouldIgnore(MessageReceivedEvent event) {
        return event.getAuthor().isBot();
    }

    private ChatBotSession getOrCreateSession(String chatId, MessageReceivedEvent event) {
        return chatSessions.computeIfAbsent(chatId, id -> {
            log("Creating new session for chat ID: " + chatId);
            MessageSender messageSender = new JDAMessageSender(event.getChannel());
            return new ChatBotSession(messageSender);
        });
    }

    public void broadcastMessage(String message) {
        chatSessions.values().forEach(session -> {
            if ("ACTIVE".equals(session.getCurrentStatus().getName())) {
                try {
                    session.getMessageSender().sendMessage(message);
                    log("[Broadcast] Message sent: " + message);
                } catch (Exception e) {
                    logError("Failed to send broadcast message: " + message, e);
                }
            }
        });
    }

    private void log(String message) {
        System.out.println("[BotListener] " + message);
    }

    private void logError(String message, Throwable e) {
        System.err.println("[BotListener] " + message);
        e.printStackTrace();
    }

}
