package bot.data;


import bot.analytics.TickerAnalyzer;
import bot.data.model.Ticker;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


import java.util.List;

public class Data {
    private final TickerAnalyzer tickerAnalyzer;
    private final TickerRepository tickerRepository;
    private final DataCollecting dataCollecting;

    public Data(TickerRepository tickerRepository, TickerAnalyzer tickerAnalyzer, DataCollecting dataCollecting) {
        this.tickerRepository  = tickerRepository;
        this.tickerAnalyzer = tickerAnalyzer;
        this.dataCollecting = dataCollecting;

        Start();
    }

    public void Start(){

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
