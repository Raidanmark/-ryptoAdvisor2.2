package bot.messages;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;


public class JDAMessageSender implements MessageSender {
    private final MessageChannel channel;

    public JDAMessageSender(MessageChannel channel) {
        this.channel = channel;
    }

    @Override
    public void sendMessage(String message) {
        channel.sendMessage(message).queue();
    }
}
