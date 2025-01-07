package bot.data;

import bot.analytics.MACD;
import bot.analytics.SMA;
import bot.analytics.TickerAnalyzer;
import bot.chatbot.BotCore;
import bot.data.model.Ticker;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.ArrayList;
import java.util.List;

public class Data {
    private final TickerAnalyzer tickerAnalyzer;
    private final TickerRepository tickerRepository;
    private final DataCollecting dataCollecting;

    public Data() {
        this.tickerRepository  = new TickerRepository();
        this.tickerAnalyzer = new TickerAnalyzer();
        this.dataCollecting = new DataCollecting();

        Start();
    }

    public void Start(){
        ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule());
        CloseableHttpClient client = HttpClients.createDefault();

        HuobiApi huobiApi = new HuobiApi(client, om);
        HuobiApiWebsocket huobiApiWebsocket = new HuobiApiWebsocket(om);
        DataCollecting dataCollecting = new DataCollecting(huobiApi,huobiApiWebsocket, data);

        List<Ticker> initialTickers = dataCollecting.start();
        initializeTickers(initialTickers);

        // Анализ загруженных тикеров
        System.out.println("Инициализация завершена. Загруженные тикеры: " + tickerRepository.getAllTickers());
        analyzeLoadedTickers();
    }

    public void initializeTickers(List<Ticker> initialTickers) {
        tickerRepository.addTickers(initialTickers); // Используем оригинальное название метода
    }

    public Ticker findTicker(String symbol, String timeframe) {
        return tickerRepository.findTicker(symbol, timeframe);
    }

    public void analyzeLoadedTickers() {
        tickerRepository.getAllTickers().forEach(tickerAnalyzer::analyzeTicker);
    }

    public void updateTicker(Ticker updatedTicker) {
        tickerRepository.updateTicker(updatedTicker);
        tickerAnalyzer.analyzeTicker(updatedTicker); // Анализ обновленного тикера
    }

}
