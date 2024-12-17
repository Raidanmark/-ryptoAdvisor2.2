package bot.chatbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class BotCore {
    private static JDA jda;
    public BotCore(String token) {
        try {
            jda = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                    .build();

            jda.addEventListener(new BotListener(jda));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
