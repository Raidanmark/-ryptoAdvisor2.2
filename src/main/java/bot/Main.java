package bot;

import bot.chatbot.BotCore;
import bot.chatbot.Config;
import bot.data.Data;
import bot.data.DataCollecting;
import bot.data.HuobiApi;
import bot.data.TickerRepository;

public class Main {
    public static void main(String[] args) {
        try {
            AppConfig appConfig = new AppConfig();

            Config config = appConfig.createConfig();
            BotCore botCore = appConfig.createBotCore(config);
            TickerRepository tickerRepository = appConfig.createTickerRepository();
            DataCollecting dataCollecting = appConfig.createDataCollecting(tickerRepository);
            Data data = appConfig.createData(tickerRepository, dataCollecting);


            System.out.println("App successfully started!");
        } catch (Exception e) {
            System.err.println("Initializing app error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}