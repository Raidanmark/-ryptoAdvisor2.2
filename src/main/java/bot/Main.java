package bot;

import bot.chatbot.BotCore;
import bot.chatbot.Config;
import bot.data.Data;
import bot.data.HuobiApi;

public class Main {
    public static void main(String[] args) {
        try {
            AppConfig appConfig = new AppConfig();

            Config config = appConfig.createConfig();
            BotCore botCore = appConfig.createBotCore(config);
            Data data = appConfig.createData();
            HuobiApi huobiApi

            System.out.println("App successfully started!");
        } catch (Exception e) {
            System.err.println("Initializing app error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}