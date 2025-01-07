package bot.analytics;

import bot.data.model.Ticker;

public interface Analyzer {
    void analyze(Ticker ticker);
}
