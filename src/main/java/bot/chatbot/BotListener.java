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
        if (event.getAuthor().isBot()) {
            return;
        }

        String chatId = event.getChannel().getId();
        String message = event.getMessage().getContentRaw();

        log("[Message Received] Chat ID: " + chatId + ", Message: " + message);

        try {
            MessageSender messageSender = new JDAMessageSender(event.getChannel());
            ChatBotSession session = chatSessions.computeIfAbsent(chatId, id -> {
                log("[New Session] Creating session for chat ID: " + chatId);
                return new ChatBotSession(messageSender);
            });

            session.processCommand(message);
        } catch (Exception e) {
            logError("Error processing message: " + message, e);
        }
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
