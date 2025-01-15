package bot;

import bot.analytics.Analyzer;
import bot.analytics.TickerAnalyzer;
import bot.chatbot.BotCore;
import bot.chatbot.BotListener;
import bot.chatbot.Config;
import bot.commands.CommandRegistry;
import bot.commands.StatusCommand;
import bot.data.Data;
import bot.data.DataCollecting;
import bot.data.HuobiApi;
import bot.data.TickerRepository;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            AppConfig appConfig = new AppConfig();

            Config config = appConfig.createConfig();
            TickerRepository tickerRepository = appConfig.createTickerRepository();
            CommandRegistry commandRegistry = appConfig.createCommandRegistry(tickerRepository);
            BotListener botListener = new BotListener(commandRegistry);
            BotCore botCore = appConfig.createBotCore(config, botListener);
            DataCollecting dataCollecting = appConfig.createDataCollecting(tickerRepository);
            Data data = appConfig.createData(tickerRepository, dataCollecting);
            List<Analyzer> analyzers = appConfig.createAnalyzers(data, botListener);
            TickerAnalyzer tickerAnalyzer = appConfig.createTickerAnalyzer(analyzers);

            tickerRepository.setTickerAnalyzer(tickerAnalyzer);
            data.start();


            System.out.println("App successfully started!");
        } catch (Exception e) {
            System.err.println("Initializing app error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}