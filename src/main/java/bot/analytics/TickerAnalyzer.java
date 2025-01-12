package bot.analytics;

import bot.data.model.Ticker;

public class TickerAnalyzer {

    public void analyzeTicker(Ticker ticker) {
        try {
            analyzeMACD(ticker);
        } catch (Exception e) {
            logError("Error analyzing MACD for ticker: " + ticker.symbol(), e);
        }

        try {
            analyzeSMA(ticker);
        } catch (Exception e) {
            logError("Error analyzing SMA for ticker: " + ticker.symbol(), e);
        }
    }

    protected void analyzeMACD(Ticker ticker) {
        // Логика анализа MACD
        System.out.println("Analyzing MACD for: " + ticker.symbol());
    }

    protected void analyzeSMA(Ticker ticker) {
        // Логика анализа SMA
        System.out.println("Analyzing SMA for: " + ticker.symbol());
    }

    private void logError(String message, Throwable e) {
        System.err.println("[TickerAnalyzer] " + message);
        e.printStackTrace();
    }
}
