package bot.chatbot;

import net.dv8tion.jda.api.JDA;


public class BotCore {
    private final JDA jda;
    private final BotListener botListener;

    public BotCore(JDA jda, BotListener botListener) {
        this.botListener = botListener;
        this.jda = jda;

        if (this.jda == null) {
            throw new IllegalArgumentException("JDA can't be null");
        }

        this.jda.addEventListener(botListener);

    }

    public JDA getJDA() {
        return jda;
    }

}



