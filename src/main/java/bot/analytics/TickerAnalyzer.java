package bot.analytics;

import bot.data.model.Ticker;

import java.util.ArrayList;
import java.util.List;

public class TickerAnalyzer {
    private final List<Analyzer> analyzers = new ArrayList<>();

    public void addAnalyzer(Analyzer analyzer) {
        analyzers.add(analyzer);
    }

    public void analyzeTicker(Ticker ticker) {
        for (Analyzer analyzer : analyzers) {
            analyzer.analyze(ticker);
        }
    }
}
