package bot.chatbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class BotCore {
    private static JDA jda;
    private final BotListener botListener;

    public BotCore(String token) {
        BotListener tempListener = null;
        try {
            jda = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                    .build();

            tempListener = new BotListener(jda); // Создаём Listener
            jda.addEventListener(tempListener);

        } catch (Exception e) {
            e.printStackTrace();
        }
        this.botListener = tempListener; // Присваиваем финальной переменной
        if (this.botListener == null) {
            throw new IllegalStateException("Failed to initialize BotListener");
        }
    }

    public BotListener getBotListener() {
        return botListener;
    }
}



