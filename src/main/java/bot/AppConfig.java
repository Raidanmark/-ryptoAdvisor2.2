package bot;

import bot.analytics.Analyzer;
import bot.analytics.MACD;
import bot.analytics.SMA;
import bot.analytics.TickerAnalyzer;
import bot.chatbot.BotCore;
import bot.chatbot.BotListener;
import bot.chatbot.Config;
import bot.commands.CommandRegistry;
import bot.data.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.List;


public class AppConfig {
    public Config createConfig(){
        return new Config();
    }


    public BotCore createBotCore(Config config, BotListener botListener){
        String token = config.getDiscordToken();
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Discord token is missing or empty");
        }

        JDA jda = createJDA(token);
        return new BotCore(jda, botListener);
    }

    private JDA createJDA(String token){
        try {
            return JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("JDA creating error" + e.getMessage(), e);
        }
    }


    public List<Analyzer> createAnalyzers(Data data, BotListener botListener){
        return List.of(new MACD(data,botListener), new SMA(data,botListener));
    }

    public CommandRegistry createCommandRegistry(TickerRepository tickerRepository){
        return new CommandRegistry(tickerRepository);
    }

    public TickerAnalyzer createTickerAnalyzer(List<Analyzer> analyzers){
        return new TickerAnalyzer(analyzers);
    }

    public Data createData(TickerRepository tickerRepository, DataCollecting dataCollecting){
        return new Data(tickerRepository, dataCollecting);
    }

    private ObjectMapper createObjectMapper(){
       return  new ObjectMapper().registerModule(new JavaTimeModule());
    }

    private CloseableHttpClient createHttpClient(){
        CloseableHttpClient client = HttpClients.createDefault();
        return client;
    }

    private CandleFilter createCandleFilter(){
        return new CandleFilter();
    }
    public static TickerRepository createTickerRepository() {
        return new TickerRepository();
    }

    private DataConfig createDataConfig(){
        return new DataConfig();
    }

    private ApiClient createApiClient() {
        return new HuobiApi(createHttpClient(), createObjectMapper());
    }

    private Websocket createWebsocket() {
        return new HuobiApiWebsocket(createObjectMapper());
    }

    public DataCollecting createDataCollecting(TickerRepository tickerRepository) {
        return new DataCollecting(createApiClient(), createWebsocket(), tickerRepository, createDataConfig(), createCandleFilter());
    }
}
