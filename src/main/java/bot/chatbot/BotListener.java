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
    private final JDA jda;

    public BotListener(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (isBotMessage(event)) {
            return;
        }

        String chatId = extractChatId(event);
        String message = extractMessageContent(event);
        MessageSender messageSender = createMessageSender(event);
        ChatBotSession session = getOrCreateSession(chatId, messageSender);
        session.processCommand(message);
    }

    private boolean isBotMessage(MessageReceivedEvent event) {
        return event.getAuthor().isBot();
    }

    private String extractChatId(MessageReceivedEvent event) {
        return event.getChannel().getId();
    }

    private String extractMessageContent(MessageReceivedEvent event) {
        return event.getMessage().getContentRaw();
    }

    private MessageSender createMessageSender(MessageReceivedEvent event) {
        return new JDAMessageSender(event.getChannel());
    }

    private ChatBotSession getOrCreateSession(String chatId, MessageSender messageSender) {
        return chatSessions.computeIfAbsent(chatId, id -> new ChatBotSession(messageSender));
    }

}
