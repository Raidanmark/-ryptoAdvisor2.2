package bot.data;


import bot.analytics.TickerAnalyzer;
import bot.data.model.Ticker;

import java.util.List;

public class Data {
    private final TickerRepository tickerRepository;
    private final TickerAnalyzer tickerAnalyzer;
    private final DataCollecting dataCollecting;

    public Data(TickerRepository tickerRepository, TickerAnalyzer tickerAnalyzer, DataCollecting dataCollecting) {
        this.tickerRepository = tickerRepository;
        this.tickerAnalyzer = tickerAnalyzer;
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
        tickerRepository.getAllTickers().forEach(ticker -> {
            try {
                tickerAnalyzer.analyzeTicker(ticker);
            } catch (Exception e) {
                logError("Error analyzing ticker: " + ticker.symbol(), e);
            }
        });
    }

    public void updateTicker(Ticker updatedTicker) {
        tickerRepository.updateTicker(updatedTicker);
        try {
            tickerAnalyzer.analyzeTicker(updatedTicker);
        } catch (Exception e) {
            logError("Error analyzing updated ticker: " + updatedTicker.symbol(), e);
        }
    }

    private void log(String message) {
        System.out.println("[Data] " + message);
    }

    private void logError(String message, Throwable e) {
        System.err.println("[Data] " + message);
        e.printStackTrace();
    }
}

