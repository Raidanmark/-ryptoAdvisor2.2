package bot.analytics;

import bot.data.model.Ticker;
import java.util.List;

public class TickerAnalyzer {

        private final List<Analyzer> analyzers;

        public TickerAnalyzer(List<Analyzer> analyzers) {
            this.analyzers = analyzers;
        }

        public void analyzeTicker(Ticker ticker) {
            for (Analyzer analyzer : analyzers) {
                try {
                    analyzer.analyze(ticker);
                } catch (Exception e) {
                    logError("Error analyzing ticker: " + ticker.symbol(), e);
                }
            }
        }

        private void logError(String message, Throwable e) {
            System.err.println("[TickerAnalyzer] " + message);
            e.printStackTrace();
        }
    }
