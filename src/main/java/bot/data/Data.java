package bot.data;


import bot.analytics.TickerAnalyzer;
import bot.data.model.Ticker;

import java.util.List;

public class Data {
    private final TickerRepository tickerRepository;
    private final DataCollecting dataCollecting;

    public Data(TickerRepository tickerRepository, DataCollecting dataCollecting) {
        this.tickerRepository = tickerRepository;
        this.dataCollecting = dataCollecting;
    }


    public void start() {
        loadTickers();
        analyzeTickers();
    }


    private void loadTickers() {
        List<Ticker> initialTickers = dataCollecting.start();
        tickerRepository.addTickers(initialTickers);
        log("Tickers loaded: " + tickerRepository.getAllTickers());
    }

    private void analyzeTickers() {
        tickerRepository.analyzeAllTickers();
    }

    public void updateTicker(Ticker updatedTicker) {
        tickerRepository.updateTicker(updatedTicker);
    }

    private void log(String message) {
        System.out.println("[Data] " + message);
    }

}

